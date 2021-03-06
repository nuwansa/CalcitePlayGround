/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.calcitecsvtest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.apache.calcite.jdbc.CalciteConnection;
import org.apache.calcite.schema.SchemaPlus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author nuwansa
 */
public class CsvTest {

    static {
        try {
            Class.forName("org.apache.calcite.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CsvTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Connection connection;

    @Before
    public void setUp() throws SQLException {
        Stream<String[]> streamA = Arrays.asList(new String[]{"Q","R","S"}, new String[]{"aaaa","eeee","yyyy"}, new String[]{"bbbb","ttttt","uuuuuu"},
                new String[]{"cccc","yyyy","oooo"}).stream();
        CsvTable tableA = new CsvTable("TableA", Arrays.asList(new CsvField("Q", "string"),new CsvField("R", "string"),new CsvField("S", "string")).stream().toArray(CsvField[]::new), () -> streamA);

        Stream<String[]> streamB = Arrays.asList(new String[]{"A","B","C"}, new String[]{"aaaa","ssss","ppppp"},
                new String[]{"1111","4444","77777"}, new String[]{"2222","5555","88888"}).stream();
        CsvTable tableB = new CsvTable("TableB", Arrays.asList(new CsvField("A", "string"),new CsvField("B", "string"),new CsvField("C", "string")).stream().toArray(CsvField[]::new), () -> streamB);
        Properties info = new Properties();
        info.setProperty("lex", "JAVA");
        connection
                = DriverManager.getConnection("jdbc:calcite:", info);

        CalciteConnection calciteConnection
                = connection.unwrap(CalciteConnection.class);
        SchemaPlus rootSchema = calciteConnection.getRootSchema();
        SchemaPlus schema = rootSchema.add("Test", new CsvSchema(() -> Arrays.asList(tableA, tableB)));
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of parse method, of class CsvParser.
     */
    @Test
    public void notInTest() {
        int count = 0;
        try {
            ResultSet resultSet = connection.createStatement().executeQuery("select Q ,Q not in (select A from Test.TableB) from Test.TableA");

            StringBuilder buf = new StringBuilder();
            while (resultSet.next()) {
                int n = resultSet.getMetaData().getColumnCount();
                for (int i = 1; i <= n; i++) {
                    buf.append(i > 1 ? "; " : "")
                            .append(resultSet.getMetaData().getColumnLabel(i))
                            .append("=")
                            .append(resultSet.getObject(i));
                }
                System.out.println(count + " : " +buf.toString());
                buf.setLength(0);
                count++;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Test
    public void notInTest2() {
        int count = 0;
        try {
            ResultSet resultSet = connection.createStatement().executeQuery("select Q ,Q not in ('aaaa','1111') from Test.TableA");

            StringBuilder buf = new StringBuilder();
            while (resultSet.next()) {
                int n = resultSet.getMetaData().getColumnCount();
                for (int i = 1; i <= n; i++) {
                    buf.append(i > 1 ? "; " : "")
                            .append(resultSet.getMetaData().getColumnLabel(i))
                            .append("=")
                            .append(resultSet.getObject(i));
                }
                System.out.println(count + " : " + buf.toString());
                buf.setLength(0);
                count++;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
