package de.akesting;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPOutputStream;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;

class TrajectoryExporter {

    private static final long MAX_TIME_GAP_SINGLE_TRAJ = TimeUnit.MINUTES.toMillis(5);

    private static final String NEW_LINE = "\n";

    private static final char SEPARATOR = ',';

    private final Map<Long, Trajectory> trajectories;

    public TrajectoryExporter(File fcdExportFile, Map<Long, Trajectory> trajectories) throws IOException {
        this.trajectories = Preconditions.checkNotNull(trajectories);
        writeFile(fcdExportFile);
    }

    private final void writeFile(File file) throws IOException {
        Stopwatch stopwatch = Stopwatch.createStarted();
        try (Writer writer = new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(file)), "UTF-8")) {
            for (Trajectory trajectory : trajectories.values()) {
                TrajectoryDataPoint previousDataPoint = null;
                for (TrajectoryDataPoint dataPoint : trajectory) {
                    if (hasLargeTimeDifference(dataPoint, previousDataPoint)) {
                        writer.write(NEW_LINE);  // for gnuplot, start new trajectory
                    }
                    if (!Float.isInfinite(dataPoint.speedKmp())) {
                        writer.write(dataPoint.toLine(SEPARATOR, NEW_LINE));
                    }
                    previousDataPoint = dataPoint;
                }
                writer.write(NEW_LINE);  // for gnuplot
            }
            System.out.printf("wrote output to %s in %s%n", file, stopwatch);
        }
    }

    private boolean hasLargeTimeDifference(TrajectoryDataPoint dp, @Nullable TrajectoryDataPoint previousDp) {
        return previousDp != null && Math.abs(dp.timestamp() - previousDp.timestamp()) > MAX_TIME_GAP_SINGLE_TRAJ;

    }

}
