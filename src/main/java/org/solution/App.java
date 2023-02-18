package org.solution;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.* ;
import java.util.*;

import static org.solution.Utils.*;

/**
Mapping Solution
 Step:
 1.querry list of table / columns / id + reference each table (for validate columns to right format for mapping to XML)
 2.mapping to table list, reference list (for sorting), tableMap(map to XML)
 3.sorting according reference list
 4.mapping tableMap to String
 5.String write to BeneratorXML.txt

 */
public class App 
{
    //schema created with init.sql name "solution"
    static private final String url = "jdbc:postgresql://localhost/solution";
    //just random user :)
    static private final String user = "postgres";
    //and simple password :))
    static private final String password = "root";
    public static void main( String[] args )
    {
        //1
        Connection conn = null;
        Statement stm=null;
        ResultSet rs=null;
        String sqlGetTableList="SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'";
        String sqlGetColumns="SELECT column_name, data_type FROM information_schema.columns WHERE table_schema='public' AND table_name='";
        String sqlGetId="SELECT c.column_name FROM information_schema.table_constraints t JOIN information_schema.constraint_column_usage c ON c.constraint_name=t.constraint_name " +
                "WHERE t.constraint_type='PRIMARY KEY' AND c.TABLE_NAME='";
        String sqlGetReference="SELECT tc.table_name, kcu.column_name, ccu.table_name AS foreign_table_name, ccu.column_name AS foreign_column_name FROM information_schema.table_constraints AS tc " +
                "JOIN information_schema.key_column_usage AS kcu ON tc.constraint_name = kcu.constraint_name AND tc.table_schema = kcu.table_schema " +
                "JOIN information_schema.constraint_column_usage AS ccu ON ccu.constraint_name = tc.constraint_name AND ccu.table_schema = tc.table_schema " +
                "WHERE tc.constraint_type = 'FOREIGN KEY' AND tc.table_name='";
        List<String> tableList = new ArrayList<>();
        List<List<String>> referencedList = new ArrayList<>();
        Map<String, List<List<String>>> tableMap = new HashMap<>();
        try {
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the PostgreSQL server successfully.");
            stm= conn.createStatement();

            //query table list
            rs=stm.executeQuery(sqlGetTableList);
            while (rs.next()) {
                tableList.add(rs.getString(1));
            }
            //+make HashMap of Mapping Data
            for (String tableName : tableList) {
                //init referencedList
                List<String> referencedListEachTable = new ArrayList<>();
                referencedListEachTable.add(tableName);
                //query table column and metadata
                String tempSql=sqlGetColumns+tableName+"'";
                //tableMap=HashMap{tablename, tableMapValue}
                List<List<String>> tableMapValue = new ArrayList<>();
                rs=stm.executeQuery(tempSql);
                //++create initial tableMapValue with all column, no validation for id and reference yet
                while (rs.next()) {
                    List<String> tempColumnList = new ArrayList<>();
                    //normal attribute tag:
                    //index=0->tag
                    tempColumnList.add(columnNameToTag(rs.getString(1)));
                    //index=1->name
                    tempColumnList.add(rs.getString(1));
                    //index=2->data_type
                    tempColumnList.add(columnTypeDBConvert(rs.getString(2)));
                    tableMapValue.add(tempColumnList);
                }
                //query table id, simple case only 1 id per table (assumed)
                String tempSql1=sqlGetId+tableName+"'";
                String id="";
                rs=stm.executeQuery(tempSql1);
                while(rs.next()) {
                    id = rs.getString(1);
                }
                //query table reference
                String tempSql2=sqlGetReference+tableName+"'";
                List<List<String>> tempReferenceListList = new ArrayList<>();
                rs=stm.executeQuery(tempSql2);
                while (rs.next()) {
                    List<String> tempReferenceList = new ArrayList<>();
                    //index=0->"reference" tag
                    tempReferenceList.add("reference");
                    //index=1->column name
                    tempReferenceList.add(rs.getString(2));
                    //index=2->foreign table name
                    tempReferenceList.add(rs.getString(3));
                    //index=3->foreign column name
                    tempReferenceList.add(rs.getString(4));
                    tempReferenceListList.add(tempReferenceList);
                }
                //++validate and replace attribute -> id tag
                for (List<String> columnList : tableMapValue) {
                    if(columnList.get(1).equals(id)) {
                        columnList.set(0,"id");
                    }
                }
                //++validate replace attribute -> reference tag
                List<Integer> referenceIndex = new ArrayList<>();
                //because I'm not sure with concurency problem to delete arraylist element while looping in it->take index then delete better
                if(!tempReferenceListList.isEmpty()) {
                    for (List<String> referenceList : tempReferenceListList) {
                        for (int i = tableMapValue.size() - 1; i >= 0; i--) {
                            if (tableMapValue.get(i).get(1).equals(referenceList.get(1))) {
                                referenceIndex.add(i);
                                if(!referenceList.get(2).equals(tableName)) {
                                    referencedListEachTable.add(referenceList.get(2));
                                } else {
                                    referencedListEachTable.add("none");
                                }
                            }
                        }
                    }
                } else {
                    referencedListEachTable.add("none");
                }
                referencedList.add(referencedListEachTable);
                //remove attribute as reference
                for (int index : referenceIndex) {
                 tableMapValue.remove(index);
                }
                //add reference tag to the end of list, no sort
                for ( List<String> referenceList : tempReferenceListList) {
                 tableMapValue.add(referenceList);
                }
                //validate ID order -> take it to top list
                tableMapValue = validateIdOrder(tableMapValue);
                tableMap.put(tableName,tableMapValue);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        //sort
        List<String> orderedList = ReferencedMustBeInAdvanceSort(referencedList);
        // mapping to XML String
        String resultXML = CustomMapToXML(orderedList, tableMap);
        // Write to text file
        try {
            Path path
                    = Paths.get("C:\\Users\\Danh\\Desktop\\BeneratorXML.txt");
            Files.writeString(path, resultXML);
        }
        catch (IOException ex) {
            System.out.print("Cannot Write Text File");
        }
    }



}
