package de.akesting;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import com.google.common.base.Preconditions;

import de.akesting.utils.MiscUtils;

public class TrajectoryReaderFilterWriter {

    private List<Trajectory> trajectories;

    public static void main(String[] args) throws IOException {
        System.out.println("args: " + Arrays.toString(args));
        Preconditions.checkArgument(args.length == 2, "expect path and filename");

        String path = args[0];
        String inputFileName = args[1];
        File inputFile = new File(path, inputFileName);

        TrajectoryReaderFilterWriter trajectoryReader = new TrajectoryReaderFilterWriter();
        trajectoryReader.read(inputFile);

        // A5 with offsetNB=494.6
        double x0 = 1000; // km 493.6
        double x1 = 30000; // km 464.6

        String baseOutputFileName = inputFileName.replace(".csv", "");
        File outputFile = new File(path, baseOutputFileName + "_trajectories_" + Math.round(x0) + "_" + Math.round(x1) + ".csv");
        trajectoryReader.writeTrajectories(outputFile, x0, x1);

        // 488 => 6.6
        File outputFileTravelTimes = new File(path, baseOutputFileName + "_traveltimes_" + Math.round(x0) + "_" + Math.round(x1) + ".csv");
        trajectoryReader.writeTravelTimes(outputFileTravelTimes, x0, x1);

        writeCrossSection(path, baseOutputFileName, trajectoryReader, 7600);  // km 487
        writeCrossSection(path, baseOutputFileName, trajectoryReader, 8600);  // km 486
        writeCrossSection(path, baseOutputFileName, trajectoryReader, 18600); // km 476
        writeCrossSection(path, baseOutputFileName, trajectoryReader, x1);
    }

    private static void writeCrossSection(String path, String baseOutputFileName, TrajectoryReaderFilterWriter trajectoryReader, double x)
            throws IOException {
        File outputFileCrossSection = new File(path, baseOutputFileName + "_cross_section_" + Math.round(x) + ".csv");
        trajectoryReader.writeCrossSections(outputFileCrossSection, x);
    }

    private int read(File file) throws IOException {
        Preconditions.checkArgument(file.exists(), "file does not exist: " + file);
        trajectories = new ArrayList<>(20000);
        try (BufferedReader in = MiscUtils.getReader(file)) {
            String line;
            Trajectory trajectory = new Trajectory();
            while ((line = in.readLine()) != null) {
                if (line.startsWith("#")) {
                    continue;
                }
                String[] split = line.split(",");
                if (split.length == 4) {
                    TrajectoryDataPoint dp = readLine(split);
                    trajectory.add(dp);
                } else {
                    if (trajectory.size() > 0) {
                        trajectories.add(trajectory);
                    }
                    trajectory = new Trajectory();
                }
            }
        }
        System.out.printf("read %d trajectories from %s%n", trajectories.size(), file);
        return trajectories.size();
    }

    private TrajectoryDataPoint readLine(String[] split) {
        long utc = Long.parseLong(split[0]);
        // int timeOfDaySeconds = Integer.parseInt(split[1]);
        float positionMeters = Float.parseFloat(split[2]);
        Preconditions.checkArgument(positionMeters >= 0);
        float speedKph = Float.parseFloat(split[3]);
        return new TrajectoryDataPoint(positionMeters, 1000 * utc, speedKph);
    }

    private void writeTrajectories(File outputFile, double x0, double x1) throws IOException {
        Predicate<Trajectory> spacialFilter = withinSpacialInterval(x0, x1);
        List<Trajectory> withinRange = trajectories.stream().filter(spacialFilter).toList();
        TrajectoryExporter.writeFile(outputFile, withinRange);
        System.out.printf("wrote %d from %d trajectories to %s%n", withinRange.size(), trajectories.size(), outputFile);
    }

    private void writeTravelTimes(File outputFile, double x0, double x1) throws IOException {
        Predicate<Trajectory> spacialFilter = withinSpacialInterval(x0, x1);
        List<Trajectory> withinRange = trajectories.stream().filter(spacialFilter).toList();
        try (Writer writer = MiscUtils.getWriter(outputFile)) {
            writer.write(String.format(
                    "#entryEpochSeconds, entryLocalTimeOfDay[s], exitEpochSeconds, exitLocalTimeOfDay[s], travelTime[s], maxSpeed[km/h], avgSpeed[km/h], dataPoints%n"));
            for (Trajectory trajectory : withinRange) {
                TrajectoryDataPoint dp0 = trajectory.findClosestDataPoint(x0);
                TrajectoryDataPoint dp1 = trajectory.findClosestDataPoint(x1);
                if (dp0 != null && dp1 != null) {
                    double travelTime = trajectory.travelTime(dp0, dp1);
                    if (travelTime > 0) {
                        double avgSpeed = 3.6 * (x1 - x0) / travelTime;
                        writer.write(String.format("%d, %d, %d, %d, %.1f, %.1f, %.1f, %d%n", dp0.timestampEpochSeconds(),
                                TrajectoryExporter.getSecondsOfLocalDay(dp0), dp1.timestampEpochSeconds(),
                                TrajectoryExporter.getSecondsOfLocalDay(dp1), travelTime, trajectory.getMaxSpeedKMH(), avgSpeed,
                                trajectory.size()));
                    }
                }
            }
        }
    }

    private void writeCrossSections(File outputFile, double x) throws IOException {
        // sort data points by time
        List<TrajectoryDataPoint> timeSorted = trajectories.stream()
                .map(it -> it.findClosestDataPoint(x))
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingLong(TrajectoryDataPoint::timestampEpochSeconds))
                .toList();
        try (Writer writer = MiscUtils.getWriter(outputFile)) {
            writer.write(String.format("#epochSeconds, localTimeOfDay[s], speed[km/h]%n"));
            for (TrajectoryDataPoint dp : timeSorted) {
                writer.write(String.format("%d, %d,  %.1f%n", dp.timestampEpochSeconds(), TrajectoryExporter.getSecondsOfLocalDay(dp),
                        dp.speedKph()));
            }
        }
    }

    private Predicate<Trajectory> withinSpacialInterval(double x0, double x1) {
        Preconditions.checkArgument(x0 < x1, "x0 should be smaller than x1");
        return dp -> dp.getFirst().position() < x0 && dp.getLast().position() > x1;
    }

}
