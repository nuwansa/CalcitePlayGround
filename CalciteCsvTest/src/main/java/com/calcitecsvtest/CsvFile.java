/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.calcitecsvtest;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 *
 * @author nuwansa
 */
public class CsvFile {

    private BufferedReader reader;
    private StringBuilder builder = new StringBuilder(1024);
    private ArrayList<String> row = new ArrayList();
   // private String[] arr = new String[5];
    public CsvFile(String file) throws FileNotFoundException {
        reader = new BufferedReader(new FileReader(file), 4 * 1024 * 1024);
    }

    private boolean isLineEnd(String line) {
        int i = 0;
        boolean quote = false;
        while (i < line.length()) {
            char ch = line.charAt(i);
            if (ch == '"') {
                quote = !quote;
            }
            i++;
        }
        return !quote;
    }

    private String tranform(String val)
    {
        if(val.startsWith("\"") && val.endsWith("\""))
            return val.substring(1, val.length() -1).replaceAll("\"\"", "\"");
        return val;
    }
    
    private boolean split(String line, char delimiter) {
        int i = 0;
        boolean quote = false;
        while (i < line.length()) {
            char ch = line.charAt(i);
            if (ch == '"') {
                quote = !quote;
            }

            if (ch == delimiter && !quote) {
              //  arr[index] = builder.toString();
              //  index++;
                row.add(tranform(builder.toString()));
                builder.setLength(0);
            } else {
                builder.append(ch);
            }
            i++;
        }
        if (!quote) {
           // arr[index] = builder.toString();
         //    index++;
            row.add(tranform(builder.toString()));
            builder.setLength(0);
        }
        return !quote;
    }

    private String[] splitLine(char delimiter) throws IOException {
        String line = null;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) {
                return null;
            }
            if (split(line, delimiter)) {
                break;
            }
        }
        if (row.size() > 0) {
     //       index = 0;
    //        return arr;
            String[] arr = row.toArray(new String[row.size()]);
            row.clear();
           return arr;
        }
        return null;

    }

    public Stream<String[]> splitLines(char delimiter) {
        Iterator<String[]> iter = new Iterator<String[]>() {
            String[] nextLine = null;

            @Override
            public boolean hasNext() {
                if (nextLine != null) {
                    return true;
                } else {
                    try {
                        nextLine = splitLine(delimiter);
                        return (nextLine != null);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }
            }

            @Override
            public String[] next() {
                if (nextLine != null || hasNext()) {
                    String[] line = nextLine;
                    nextLine = null;
                    return line;
                } else {
                    throw new NoSuchElementException();
                }
            }
        };
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                iter, Spliterator.ORDERED | Spliterator.NONNULL), false);
    }

    public static Stream<String[]> lines(Path path, char delimiter) throws IOException {
        CsvFile csvFile = new CsvFile(path.toString());
        try {
            return csvFile.splitLines(delimiter).onClose(asUncheckedRunnable(csvFile.reader));
        } catch (Error | RuntimeException e) {
            try {
                csvFile.reader.close();
            } catch (IOException ex) {
                try {
                    e.addSuppressed(ex);
                } catch (Throwable ignore) {
                }
            }
            throw e;
        }
    }

    private String readLine() throws IOException {
        builder.setLength(0);
        String line = null;
        while ((line = reader.readLine()) != null) {

            builder.append(line);
            if (isLineEnd(line)) {
                break;
            }
        }

        //    String out = builder.toString();
//        if (out.trim().isEmpty()) {
//            return null;
//        }
        if (builder.length() == 0) {
            return null;
        }
        return builder.toString();
    }

    public Stream<String> lines() {
        Iterator<String> iter = new Iterator<String>() {
            String nextLine = null;

            @Override
            public boolean hasNext() {
                if (nextLine != null) {
                    return true;
                } else {
                    try {
                        nextLine = readLine();
                        return (nextLine != null);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }
            }

            @Override
            public String next() {
                if (nextLine != null || hasNext()) {
                    String line = nextLine;
                    nextLine = null;
                    return line;
                } else {
                    throw new NoSuchElementException();
                }
            }
        };
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                iter, Spliterator.ORDERED | Spliterator.NONNULL), false);
    }

    public static Stream<String> lines(Path path, Charset cs) throws IOException {
        CsvFile csvFile = new CsvFile(path.toString());
        try {
            return csvFile.lines().onClose(asUncheckedRunnable(csvFile.reader));
        } catch (Error | RuntimeException e) {
            try {
                csvFile.reader.close();
            } catch (IOException ex) {
                try {
                    e.addSuppressed(ex);
                } catch (Throwable ignore) {
                }
            }
            throw e;
        }
    }

    public static Stream<String> lines(Path path) throws IOException {
        return lines(path, StandardCharsets.UTF_8);
    }

    private static Runnable asUncheckedRunnable(Closeable c) {
        return () -> {
            try {
                c.close();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        };
    }
}
