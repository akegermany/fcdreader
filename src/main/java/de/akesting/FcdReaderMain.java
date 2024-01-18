package de.akesting;

import com.google.common.base.Preconditions;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FcdReaderMain {

    public static void main(String[] args) throws IOException {
        System.out.println("args: " + Arrays.toString(args));

        File sourcePath = new File(args[0]);

        File dsegFile1 = new File(sourcePath, args[1]);
        File dsegFile2 = new File(sourcePath, args[2]);
        DsegMapping dsegMapping = new DsegMapping(List.of(dsegFile1, dsegFile2));

        Collection<String> files = args.length == 4 ? List.of(args[3]) : listFilesInDirectory(sourcePath).stream().filter(it -> it.endsWith(".csv.gz")).toList();

        for (String fcdFileName : files) {
            exportFile(sourcePath, fcdFileName, dsegMapping);
        }
    }

    private static void exportFile(File sourcePath, String fcdFileName, DsegMapping dsegMapping) throws IOException {
        File fcdFile = new File(sourcePath, fcdFileName);
        FcdReader fcdReader = new FcdReader(fcdFile, dsegMapping);
        File fcdExportFile = new File(sourcePath, createExportFile(fcdFileName));
        TrajectoryExporter.writeFile(fcdExportFile, fcdReader.getTrajectories().values());
        System.out.printf("read %d trajectories in %s %n", fcdReader.getTrajectories().size());
    }

    private static String createExportFile(String name) {
        return "pos-" + name;
    }

    private static Set<String> listFilesInDirectory(File dir) {
        Preconditions.checkArgument(dir.exists(), "directory %s does not exit", dir);
        return Stream.of(Objects.requireNonNull(dir.listFiles())).filter(Predicate.not(File::isDirectory)).map(File::getName).collect(Collectors.toSet());
    }

}
