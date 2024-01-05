package de.akesting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPInputStream;

import com.google.common.base.Preconditions;

class FcdReader {

    // linked map to keep order of primary data which is essentially time-sorted
    private final Map<Long, Trajectory> trajectories = new LinkedHashMap<>();

    private final DsegMapping dsegMapping;

    public FcdReader(File fcdFile, DsegMapping dsegMapping) throws IOException {
        this.dsegMapping = Preconditions.checkNotNull(dsegMapping);
        readData(fcdFile);
    }

    private final void readData(File file) throws IOException {
        AtomicInteger lineCount = new AtomicInteger();

        try (BufferedReader in = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file))))) {
            // System.out.println(Arrays.toString(split));
            in.lines().map(it -> it.split(",")).forEach(split -> {
                if (split.length != 5) {
                    System.err.printf("unexpected columns: %s%n", Arrays.toString(split));
                } else {
                    lineCount.incrementAndGet();
                    int col = 0;
                    try {
                        Long dsegId = Long.parseLong(split[col++]);
                        long vehicleId = Long.parseLong(split[col++]);
                        float offsetOnDseg = Float.parseFloat(split[col++]);
                        Preconditions.checkArgument(offsetOnDseg >= 0);
                        long timestamp = Long.parseLong(split[col++]);
                        float speedKph = Float.parseFloat(split[col++]);
                        float dsegStartPosition = dsegMapping.getPosition(dsegId);
                        TrajectoryDataPoint dataPoint = new TrajectoryDataPoint(dsegStartPosition + offsetOnDseg, timestamp, speedKph);
                        addOrCreate(vehicleId, dataPoint);
                    } catch (NumberFormatException e) {
                        System.out.println("ignore not-parsable line " + Arrays.toString(split));
                    }
                }
            });
        }

        System.out.printf("read %d fcd lines%n", lineCount.get());
    }

    private void addOrCreate(Long vehicleId, TrajectoryDataPoint dataPoint) {
        Trajectory trajectory = trajectories.get(vehicleId);
        if (trajectory == null) {
            trajectory = new Trajectory(vehicleId);
            trajectories.put(vehicleId, trajectory);
        }
        trajectory.add(dataPoint);
    }

    public Map<Long, Trajectory> getTrajectories() {
        return Collections.unmodifiableMap(trajectories);
    }
}
