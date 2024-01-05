package de.akesting;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.CheckForNull;

import com.google.common.base.Preconditions;

class Trajectory implements Iterable<TrajectoryDataPoint> {

    private final List<TrajectoryDataPoint> dataPoints = new ArrayList<>(100);

    public void add(TrajectoryDataPoint dataPoint) {
        if (!dataPoints.isEmpty()) {
            long prevTimestamp = dataPoints.get(dataPoints.size() - 1).timestampEpochMillis();
            // System.out.printf("time shift by %d seconds%n", (dataPoint.timestamp() - prevTimestamp) / 1000L);
            //Preconditions.checkArgument(dataPoint.timestamp() > prevTimestamp, "time reversed by %d seconds", (dataPoint.timestamp() - prevTimestamp) / 1000L);
        }
        dataPoints.add(dataPoint);
    }

    public int size() {
        return dataPoints.size();
    }

    public TrajectoryDataPoint getFirst() {
        Preconditions.checkArgument(!dataPoints.isEmpty());
        return dataPoints.get(0);
    }

    public TrajectoryDataPoint getLast() {
        return dataPoints.get(dataPoints.size() - 1);
    }

    /**
     * @return travel time in seconds
     */
    public double travelTime(TrajectoryDataPoint dp0, TrajectoryDataPoint dp1) {
        return dp1.timestampEpochSeconds() - dp0.timestampEpochSeconds();
    }

    @CheckForNull
    public TrajectoryDataPoint findClosestDataPoint(double x) {
        TrajectoryDataPoint prev = null;
        for (TrajectoryDataPoint dp : dataPoints) {
            if (dp.position() > x) {
                if (prev == null) {
                    return dp;
                } else {
                    return Math.abs(dp.position() - x) < Math.abs(prev.position() - x) ? dp : prev;
                }
            }
            prev = dp;
        }
        return null;
    }

    @Override
    public Iterator<TrajectoryDataPoint> iterator() {
        return dataPoints.iterator();
    }

    /**
     * @return max speed in km/h, zero if no data points
     */
    public double getMaxSpeedKMH() {
        return dataPoints.stream().map(TrajectoryDataPoint::speedKph).max(Float::compareTo).orElse(0f);
    }

}
