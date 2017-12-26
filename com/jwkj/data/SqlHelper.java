package com.jwkj.data;

import java.util.HashMap;
import java.util.Map.Entry;

public class SqlHelper {
    public static String formCreateTableSqlString(String tableName, HashMap<String, String> columnNameAndType) {
        StringBuffer sqlCreateTable = new StringBuffer("CREATE TABLE IF NOT EXISTS " + tableName + " (");
        for (Entry entry : columnNameAndType.entrySet()) {
            sqlCreateTable.append(" ");
            sqlCreateTable.append(entry.getKey());
            sqlCreateTable.append(" ");
            sqlCreateTable.append(entry.getValue());
            sqlCreateTable.append(",");
        }
        sqlCreateTable.deleteCharAt(sqlCreateTable.lastIndexOf(","));
        sqlCreateTable.append(");");
        return sqlCreateTable.toString();
    }

    public static String formDeleteTableSqlString(String tableName) {
        return "DROP TABLE IF EXISTS " + tableName;
    }
}
