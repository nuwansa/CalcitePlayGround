/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.calcitecsvtest;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 *
 * @author nuwansa
 */
public class CsvUtils {

    public static Class<?> getType(String type) {
        switch (type) {
            case "String":
                return String.class;
            case "int":
                return int.class;
            case "number":
                return BigDecimal.class;
            default:
                return String.class;
        }
    }

    public static CsvField[] toCsvField(String table, String[] headers, CsvFieldDictionary dic) {
        return Arrays.asList(headers).stream().map(header->header.trim().replace(" ", "_")).map(header -> dic.get(table, header)).toArray(CsvField[]::new);
    }
}
