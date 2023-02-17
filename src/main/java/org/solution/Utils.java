package org.solution;

import java.util.List;

public class Utils {
    public static String columnNameToTag(String columnName) {
        String[] encrypt = new String[]{"name", "address", "email"};
        for (String data : encrypt) {
            if (columnName.toLowerCase().contains(data)) {
                return "encrypt";
            }
        }
        if (columnName.toLowerCase().contains("password")) {
            return "hash";
        } else if (columnName.toLowerCase().contains("credit")) {
            return "mask";
        } else {
            return "attribute";
        }
    }

    public static String columnTypeDBConvert(String columnType) {
        String checkString = columnType.toUpperCase();
        switch (checkString) {
            case "VARCHAR":
                return "string";
            case "VARBINARY":
                return "binary";
            case "TINYINT":
                return "byte";
            case "TIMESTAMP":
                return "timestamp";
            case "TIME":
                return "date";
            case "SMALLINT":
                return "short";
            case "REAL":
                return "double";
            case "NUMERIC":
                return "double";
            case "LONGVARCHAR":
                return "string";
            case "JAVA_OBJECT":
                return "object";
            case "INTEGER":
                return "int";
            case "FLOAT":
                return "float";
            case "DOUBLE":
                return "double";
            case "DECIMAL":
                return "big_decimal";
            case "DATE":
                return "date";
            case "CLOB":
                return "string";
            case "CHAR":
                return "string";
            case "BOOLEAN":
                return "boolean";
            case "BLOB":
                return "binary";
            case "BIT":
                return "byte";
            case "BINARY":
                return "binary";
            case "BIGINT":
                return "big_integer";
            default:
                return "string";
        }
    }
    public static List<List<String>> validateIdOrder(List<List<String>> input) {
        List<List<String>> output = input;
        //Normally id on top. In general case, maybe need to re-arrange id to top of list_can expand later
        return output;
    }

}
