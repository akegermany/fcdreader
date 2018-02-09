package de.akesting;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;

import java.io.*;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

class TrajectoryExporter {

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
                for (TrajectoryDataPoint dataPoint : trajectory) {
                    writer.write(dataPoint.toLine(SEPARATOR, NEW_LINE));
                }
                writer.write(NEW_LINE);  // for gnuplot
            }

        }
        System.out.printf("wrote output to %s in %s%n", file, stopwatch);
    }

}
