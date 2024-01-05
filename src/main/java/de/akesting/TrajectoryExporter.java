package de.akesting;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.google.common.base.Stopwatch;

import de.akesting.utils.MiscUtils;

class TrajectoryExporter {

    private static final long MAX_TIME_GAP_SINGLE_TRAJECTORY_MILLIS = TimeUnit.MINUTES.toMillis(5);
    private static final String NEW_LINE = "\n";
    private static final char SEPARATOR = ',';

    private static final DateTimeZone LOCAL_DATE_TIME_ZONE = DateTimeZone.forTimeZone(TimeZone.getTimeZone("Europe/Berlin"));

    private TrajectoryExporter() {
        // utility class
    }

    public static void writeFile(File file, Collection<Trajectory> trajectories) throws IOException {
        Stopwatch stopwatch = Stopwatch.createStarted();
        try (Writer writer = MiscUtils.getWriter(file)) {
            writer.write(String.format("#epochSeconds, localTimeOfDay[s], position[m], speed[km/h]%n"));
            List<Trajectory> sorted = trajectories.stream()
                    .sorted(Comparator.comparingLong(it -> it.getFirst().timestampEpochSeconds()))
                    .toList();
            for (Trajectory trajectory : sorted) {
                TrajectoryDataPoint previousDataPoint = null;
                for (TrajectoryDataPoint dataPoint : trajectory) {
                    if (hasLargeTimeDifference(dataPoint, previousDataPoint)) {
                        writer.write(NEW_LINE + NEW_LINE);  // for gnuplot, start new trajectory
                    }
                    if (!Float.isInfinite(dataPoint.speedKph())) {
                        writer.write(toLine(dataPoint));
                    }
                    previousDataPoint = dataPoint;
                }
                writer.write(NEW_LINE + NEW_LINE);  // for gnuplot, start new trajectory
            }
            System.out.printf("wrote output to %s in %s%n", file, stopwatch);
        }
    }

    private static String toLine(TrajectoryDataPoint dp) {
        StringBuilder sb = new StringBuilder();
        sb.append(dp.timestampEpochSeconds()).append(SEPARATOR);
        sb.append(getSecondsOfLocalDay(dp)).append(SEPARATOR);
        sb.append(dp.position()).append(SEPARATOR);
        sb.append(dp.speedKph());
        sb.append(NEW_LINE);
        return sb.toString();
    }

    public static long getSecondsOfLocalDay(TrajectoryDataPoint trajectoryDataPoint) {
        DateTime dateTime = new DateTime(trajectoryDataPoint.timestampEpochMillis(), LOCAL_DATE_TIME_ZONE);
        return dateTime.getSecondOfDay();
    }

    private static boolean hasLargeTimeDifference(TrajectoryDataPoint dp, @Nullable TrajectoryDataPoint previousDp) {
        return previousDp != null
                && Math.abs(dp.timestampEpochMillis() - previousDp.timestampEpochMillis()) > MAX_TIME_GAP_SINGLE_TRAJECTORY_MILLIS;
    }

}
