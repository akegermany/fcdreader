package de.akesting;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import com.google.common.io.Files;

class DsegMapping {

    private final LinkedHashMap<Long, Float> dsegs = new LinkedHashMap<>();

    public DsegMapping(Collection<File> dsegFiles) throws IOException {
        for (File dsegFile : dsegFiles) {
            readData(dsegFile);
        }
    }

    private void readData(File file) throws IOException {
        List<String> lines = Files.readLines(file, StandardCharsets.UTF_8);
        final String separator = ",";

        float position = 0;
        for (String line : lines) {
            String[] split = line.split(separator);
            // System.out.println(Arrays.toString(split));
            if (split.length != 6) {
                System.err.printf("unexpected columns: %s", Arrays.toString(split));
            } else {
                try {
                    Long dsegId = Long.parseLong(split[0]);
                    dsegs.put(dsegId, position);
                    position += Float.parseFloat(split[5]);
                } catch (NumberFormatException e) {
                    System.out.println("ignore not-parsable line " + line);
                }
            }
        }
        System.out.printf("read %d dsegs, last position is %.1f %n", dsegs.size(), position);

    }

    public float getPosition(Long dsegId) {
        return dsegs.get(dsegId);
    }
}
