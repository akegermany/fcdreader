package de.akesting;

import org.joda.time.DateTime;

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

    public long timestamp() {
        return timestamp;
    }

    public float speedKmp() {
        return speedKph;
    }

    public String toLine(char separator, String lineEnding) {
        StringBuilder sb = new StringBuilder();
        sb.append(timestamp / 1000L).append(separator);  // from millis to seconds
        DateTime dateTime = new DateTime(timestamp);
        sb.append(dateTime.getSecondOfDay()).append(separator);  // from millis to seconds
        sb.append(position).append(separator);
        sb.append(speedKph);
        sb.append(lineEnding);
        return sb.toString();
    }
}
