package org.solution;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
            //character varying
            case "VARCHAR":
                return "string";
            case "VARBINARY":
                return "binary";
            case "TINYINT":
                return "byte";
                //change "timestamp" to "timestamp without time zone"
            case "TIMESTAMP WITHOUT TIME ZONE":
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
        //Normally id on top. In general case, maybe need to re-arrange id to top of list_can update later
        return output;
    }

    public static List<String> ReferencedMustBeInAdvanceSort(List<List<String>> input) {
        List<String> output = new ArrayList<>();
        while (!input.isEmpty()) {
            List<Integer>  removedIndex = new ArrayList<>();
            for (int i= input.size()-1;i>=0;i--) {
                for (int k=1;k<input.get(i).size();k++) {
                    if(input.get(i).get(k).equals("none")||output.contains(input.get(i).get(k))) {
                        removedIndex.add(i);
                        output.add(input.get(i).get(0));
                        break;
                    }
                }
            }
            //delete item in input
            for(int j=0;j<removedIndex.size();j++) {
                input.remove(removedIndex.get(j).intValue());
            }
        }
        System.out.println(output);
        return output;
    }

    public static String CustomMapToXML(List<String> orderedList, Map<String, List<List<String>>> tableMap) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String tableName : orderedList) {
            List<List<String>> tableMeta = tableMap.get(tableName);
            stringBuilder.append("<generate type=\""+tableName+"\">\n");
            for (List<String> columnMeta : tableMeta) {
                stringBuilder.append(CustomListToXMLTag(columnMeta));
            }
            stringBuilder.append("</generate>\n");
        }
        return stringBuilder.toString();
    }

    public static String CustomListToXMLTag(List<String> list) {
        switch (list.get(0)) {
            case "reference":
                return "\t<reference name=\""+ list.get(1)+"\" selector=\"select "+ list.get(3)+" from "+ list.get(2)+"\" distribution=\"random\"/>\n";
            default:
                return "\t<"+list.get(0)+" name=\""+list.get(1)+"\" type=\""+list.get(2)+"\"/>\n";
        }
    }
}
