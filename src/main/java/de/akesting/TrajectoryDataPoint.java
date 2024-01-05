package de.akesting;

class TrajectoryDataPoint {

    /** in meter, absolute position along road stretch */
    private final float position;

    /** milliseconds, utc */
    private final long timestamp;

    /** in km/h */
    private final float speedKph;

    public TrajectoryDataPoint(float position, long timestamp, float speedKph) {
        this.position = position;
        this.timestamp = timestamp;
        this.speedKph = speedKph;
    }

    public float position() {
        return position;
    }

    public long timestampEpochMillis() {
        return timestamp;
    }

    public long timestampEpochSeconds() {
        return timestamp / 1000L;
    }

    public float speedKph() {
        return speedKph;
    }

}
