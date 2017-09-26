/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.calcitecsvtest;

import java.math.BigDecimal;
import java.util.Objects;

/**
 *
 * @author nuwansa
 */
public class CsvField {

    private final String field;
    private final String type;

    public CsvField(String field, String type) {
        this.field = field.trim().replaceAll(" ", "_");
        this.type = type;
    }

    public String getField() {
        return field;
    }

    public Class getType() {
        switch (type) {
            case "string": {
                return String.class;
            }
            case "int": {
                return int.class;
            }
            case "long": {
                return int.class;
            }
            case "boolean": {
                return boolean.class;
            }
            case "number": {
                return BigDecimal.class;
            }
            default: {
                return String.class;
            }
        }
    }

    public Object getValue(String value) {
        switch (type) {
            case "string": {
                return value;
            }
            case "int": {
                return Integer.valueOf(value);
            }
            case "long": {
                return Long.valueOf(value);
            }
            case "boolean": {
                return Boolean.valueOf(value);
            }
            case "number": {
                return new BigDecimal(value);
            }
            default:
                return value;
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode(this.field);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CsvField other = (CsvField) obj;
        return Objects.equals(this.field, other.field);
    }

    @Override
    public String toString() {
        return field + ":" + type;
    }
}
