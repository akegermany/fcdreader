package de.akesting.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class MiscUtils {

    public static Writer getWriter(File file) throws IOException {
        if (file.getName().endsWith(".gz")) {
            return new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(file)), StandardCharsets.UTF_8);
        }
        return new PrintWriter(new BufferedWriter(new FileWriter(file, false)));
    }

    public static BufferedReader getReader(File file) throws IOException {
        if (file.getName().endsWith(".gz")) {
            return new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file))));
        }
        return new BufferedReader(new InputStreamReader(new FileInputStream(file)));
    }

}
