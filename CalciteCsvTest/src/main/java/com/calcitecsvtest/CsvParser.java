/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.calcitecsvtest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 *
 * @author nuwansa
 */

public class CsvParser {

    public Stream<String[]> parse(String filePath, char delimiter) {
        try {
            //Pattern pattern = Pattern.compile(delimiter + "(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
            //return CsvFile.lines(toPath(filePath)).map(line -> pattern.split(line));
            return CsvFile.lines(toPath(filePath), delimiter);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private Path toPath(String filePath) {
        return new File(filePath).toPath();
    }
}
