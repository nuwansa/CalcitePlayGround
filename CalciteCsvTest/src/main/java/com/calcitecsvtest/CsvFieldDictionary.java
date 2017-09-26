/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.calcitecsvtest;

import com.google.common.collect.HashMultimap;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author nuwansa
 */

public class CsvFieldDictionary {

    private final HashMultimap<String, CsvField> mapFields = HashMultimap.create();
    private String dictionaryPath;

    public void load(String dictionaryPath) {
        this.dictionaryPath = dictionaryPath;
        File[] f = new File(dictionaryPath).listFiles();
        if(f != null)
            Arrays.asList(f).stream().filter(file -> !file.isDirectory())
                .filter(file -> FilenameUtils.getExtension(file.getName()).equals("dic")).forEach(this::loadFile);
    }

    public void put(String table, String field, String type) {
        try {
            mapFields.put(table, new CsvField(field, type));
            writeToFile(table);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public CsvField get(String table, String field) {
        CsvField csvField = mapFields.get(table).stream().filter(f -> f.getField().equals(field)).findAny().orElse(null);
        if (csvField == null) {
            csvField = mapFields.get("global").stream().filter(f -> f.getField().equals(field)).findAny().orElse(null);
        }
        if (csvField == null) {
            csvField = new CsvField(field, "string");
        }
        return csvField;
    }

    private void writeToFile(String table) throws IOException {
        Collection<CsvField> cols = mapFields.get(table);
        if (cols == null) {
            return;
        }
        List<String> list = cols.stream().map(field -> field.toString()).collect(Collectors.toList());
        Files.write(new File(dictionaryPath + "/" + table + ".dic").toPath(), new StreamIterable<>(list.iterator()), Charset.forName("utf-8"));
    }

    private void loadFile(File file) {
        String name = FilenameUtils.getBaseName(file.getName());
        try {
            Files.lines(file.toPath()).filter(line->!line.isEmpty()).map(line -> line.split(":")).map(arr -> new CsvField(arr[0], arr[1]))
                    .forEach(f -> mapFields.put(name, f));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }
}
