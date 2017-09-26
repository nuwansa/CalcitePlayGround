package com.calcitecsvtest;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.calcite.schema.Table;
import org.apache.calcite.schema.impl.AbstractSchema;

public class CsvSchema extends AbstractSchema {

    private Supplier<Collection<CsvTable>> tableSupplier;

    public CsvSchema(Supplier<Collection<CsvTable>> tableSupplier) {
        this.tableSupplier = tableSupplier;
    }

    @Override
    protected Map<String, Table> getTableMap() {
        final ImmutableMap.Builder<String, Table> builder = ImmutableMap.builder();
        Map<String, Table> tables = tableSupplier.get().stream().collect(Collectors.toMap(table -> table.getName(), Function.identity()));
        builder.putAll(tables);
        return builder.build();

    }
}
