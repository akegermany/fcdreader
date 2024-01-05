package de.akesting;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;

public class FcdReaderMain {

    public static void main(String[] args) throws IOException {
        System.out.println("args: " + Arrays.toString(args));
        Preconditions.checkArgument(args.length == 4, "expect dseg and fcd filenames");

        File sourcePath = new File(args[0]);
        File dsegFile1 = new File(sourcePath, args[1]);
        File dsegFile2 = new File(sourcePath, args[2]);

        DsegMapping dsegMapping = new DsegMapping(List.of(dsegFile1, dsegFile2));

        Stopwatch stopwatch = Stopwatch.createStarted();
        String fcdFileName = args[3];
        File fcdFile = new File(sourcePath, fcdFileName);
        FcdReader fcdReader = new FcdReader(fcdFile, dsegMapping);

        System.out.printf("read %d trajectories in %s %n", fcdReader.getTrajectories().size(), stopwatch);

        File fcdExportFile = new File(sourcePath, createExportFile(fcdFileName));
        TrajectoryExporter.writeFile(fcdExportFile, fcdReader.getTrajectories().values());
    }

    private static String createExportFile(String name) {
        return "pos-" + name;
    }

}
