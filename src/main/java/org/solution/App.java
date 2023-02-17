package org.solution;
import java.sql.* ;
import java.util.*;

import static org.solution.Utils.*;

/**
Mapping Solution
 Step:
 1.
 2.
 3.
 4.
 5.

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
        //1.connect to Postgres through JDBC and mapping data to list and map
        Connection conn = null;
        Statement stm=null;
        ResultSet rs=null;
        String sqlGetTableList="SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'";
        String sqlGetColumns="SELECT column_name, udt_name FROM information_schema.columns WHERE table_schema='public' AND table_name='";
        String sqlGetId="SELECT c.column_name FROM information_schema.table_constraints t JOIN information_schema.constraint_column_usage c ON c.constraint_name=t.constraint_name " +
                "WHERE t.constraint_type='PRIMARY KEY' AND c.TABLE_NAME='";
        String sqlGetReference="SELECT tc.table_name, kcu.column_name, ccu.table_name AS foreign_table_name, ccu.column_name AS foreign_column_name FROM information_schema.table_constraints AS tc " +
                "JOIN information_schema.key_column_usage AS kcu ON tc.constraint_name = kcu.constraint_name AND tc.table_schema = kcu.table_schema " +
                "JOIN information_schema.constraint_column_usage AS ccu ON ccu.constraint_name = tc.constraint_name AND ccu.table_schema = tc.table_schema " +
                "WHERE tc.constraint_type = 'FOREIGN KEY' AND tc.table_name='";
        List<String> tableList = new ArrayList<>();
        Map<String, List<List<String>>> tableMap = new HashMap<>();
        try {
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the PostgreSQL server successfully.");
            stm= conn.createStatement();

            //get table list
            rs=stm.executeQuery(sqlGetTableList);
            while (rs.next()) {
                tableList.add(rs.getString(1));
            }

            //make mapping data XML
            for (String tableName : tableList) {
                String tempSql=sqlGetColumns+tableName+"'";
                rs=stm.executeQuery(tempSql);
                List<List<String>> tableMapValue = new ArrayList<>();
                while (rs.next()) {
                    List<String> tempColumnList = new ArrayList<>();
                    //normal tag:
                    //index=0->tag
                    tempColumnList.add(columnNameToTag(rs.getString(1)));
                    //index=1->name
                    tempColumnList.add(rs.getString(1));
                    //index=2->data_type
                    tempColumnList.add(columnTypeDBConvert(rs.getString(2)));
                    tableMapValue.add(tempColumnList);
                }
                //querry table id, simple case only 1 id per table (assumed)
                String tempSql1=sqlGetId+tableName+"'";
                rs=stm.executeQuery(tempSql1);
                String id = rs.getString(1);
                //querry table reference
                String tempSql2=sqlGetReference+tableName+"'";
                rs=stm.executeQuery(tempSql2);
                List<String> tempReferenceList = new ArrayList<>();
                while (rs.next()) {
                    //reference tag:
                    tempReferenceList.add(rs.getString("reference" ));
                    //column name
                    tempReferenceList.add(rs.getString(2 ));
                    //foreign table
                    tempReferenceList.add(rs.getString(3));
                    //foreign column
                    tempReferenceList.add(rs.getString(4));
                }
                //replace id/reference
                for (List<String> columnList : tableMapValue) {
                    if(columnList.get(1).equals(id)) {
                        columnList.set(0,"id");
                    } else (columnList.get(1).equals(tempReferenceList.get(2)))

                }
                tableMapValue = validateIdOrder(tableMapValue);
                //validate table reference

                tableMap.put(tableName,tableMapValue);

            }
            //make mapping data XML
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }
}
