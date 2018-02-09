package de.akesting;

class TrajectoryDataPoint {

    /**
     * in meter, absolute position along road stretch
     */
    private final float position;

    /**
     * in utc
     */
    private final long timestamp;

    /**
     * in km/h
     */
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
        sb.append(timestamp).append(separator);
        sb.append(position).append(separator);
        sb.append(speedKph);
        sb.append(lineEnding);
        return sb.toString();
    }
}
