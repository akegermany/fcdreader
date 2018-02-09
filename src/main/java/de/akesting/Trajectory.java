package de.akesting;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class Trajectory implements Iterable<TrajectoryDataPoint> {

    private final List<TrajectoryDataPoint> dataPoints = new ArrayList<>();

    private final long id;

    public Trajectory(long id) {
        this.id = id;
    }

    public void add(TrajectoryDataPoint dataPoint) {
        if (!dataPoints.isEmpty()) {
            long prevTimestamp = dataPoints.get(dataPoints.size() - 1).timestamp();
            // System.out.printf("time shift by %d seconds%n", (dataPoint.timestamp() - prevTimestamp) / 1000L);
            //Preconditions.checkArgument(dataPoint.timestamp() > prevTimestamp, "time reversed by %d seconds", (dataPoint.timestamp() - prevTimestamp) / 1000L);
        }
        dataPoints.add(dataPoint);
    }

    @Override
    public Iterator<TrajectoryDataPoint> iterator() {
        return dataPoints.iterator();
    }
}
