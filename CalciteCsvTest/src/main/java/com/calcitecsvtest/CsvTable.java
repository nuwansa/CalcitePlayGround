/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.calcitecsvtest;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Stream;
import org.apache.calcite.DataContext;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.Linq4j;
import org.apache.calcite.linq4j.function.Function1;
import org.apache.calcite.rel.RelCollations;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.schema.ScannableTable;
import org.apache.calcite.schema.Schema;
import org.apache.calcite.schema.Statistic;
import org.apache.calcite.schema.Statistics;
import org.apache.calcite.util.ImmutableBitSet;

/**
 *
 * @author nuwansa
 */
public class CsvTable implements ScannableTable {

    private final CsvField[] headers;
    private final Supplier<Stream<String[]>> rowSupplier;
    private String name;
    public CsvTable(String name,CsvField[] headers, Supplier<Stream<String[]>> rowSupplier) {
        this.name = name;
        this.headers = headers;
        this.rowSupplier = rowSupplier;
    }

    public String getName() {
        return name;
    }

    
    @Override
    public Enumerable<Object[]> scan(DataContext root) {
        Enumerable enumerable = Linq4j.asEnumerable(new StreamIterable(rowSupplier.get().skip(1).iterator()));
        return enumerable.select(new FieldSelector(headers));
    }

    @Override
    public RelDataType getRowType(RelDataTypeFactory typeFactory) {
        RelDataTypeFactory.FieldInfoBuilder builder = typeFactory.builder();
        Arrays.asList(headers).stream().forEach(field -> {
            builder.add(field.getField(),
                    typeFactory.createJavaType(field.getType()));
        });
        return builder.build();
    }

    @Override
    public Statistic getStatistic() {
        return Statistics.of(100d,
                ImmutableList.<ImmutableBitSet>of(),
                RelCollations.createSingleton(0));
    }

    @Override
    public Schema.TableType getJdbcTableType() {
        return Schema.TableType.TABLE;
    }

    private static class FieldSelector implements Function1<String[], Object[]> {

        private final CsvField[] headers;

        public FieldSelector(CsvField[] headers) {
            this.headers = headers;
        }

        @Override
        public Object[] apply(String[] record) {

            int len = headers.length;
            final Object[] objects = new Object[len];

            for (int i = 0; i < headers.length; i++) {
                objects[i] = this.headers[i].getValue(record[i].trim());
            }

            return objects;

        }
    }

}
