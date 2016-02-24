package BulletinBoard;

import Domain.Collector;
import java.sql.*;
import java.util.*;

public class Database {

    private boolean debug;
    private Connection connection;

    public Database(String address) throws Exception {
        this.connection = DriverManager.getConnection(address);
    }

    public Connection getConnection() {
        return connection;
    }
    
    public void setDebugMode(boolean d) {
        debug = d;
    }

    public ResultSet query(String query, Object... params) throws SQLException {
        ResultSet rs;
        
        if (debug) {
            System.out.println("---");
            System.out.println("Executing: " + query);
            System.out.println("---");
        }
       
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
 
            rs = stmt.executeQuery();

            if (debug) {
                System.out.println("---");
                System.out.println(query);
                debug(rs);
                System.out.println("---");
            }
        }
        
        return rs;
    }
    //TODO
    public ResultSet query(String query, Collection<Integer> keys) throws SQLException {
        ResultSet rs; 
       
        if (debug) {
            System.out.println("---");
            System.out.println("Executing: " + query);
            System.out.println("---");
        }
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) { 
            int i = 1;
            for (Integer key : keys) {
                stmt.setObject(i++, key);
            }
            //stmt.setArray(1, null);?
            rs = stmt.executeQuery();
            
            if (debug) {
                System.out.println("---");
                System.out.println(query);
                debug(rs);
                System.out.println("---");
            }
        }
        
        return rs;
    }
    
    //use query to do
    public <T> List<T> queryAndCollect(String query, Collector<T> col, Object... params) throws SQLException {
        if (debug) {
            System.out.println("---");
            System.out.println("Executing: " + query);
            System.out.println("---");
        }

        List<T> rows = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    if (debug) {
                        System.out.println("---");
                        System.out.println(query);
                        debug(rs);
                        System.out.println("---");
                    }

                    rows.add(col.collect(rs));
                }
            }
        }

        return rows;
    }

    public int update(String updateQuery, Object... params) throws SQLException {
        int changes;
        try (PreparedStatement stmt = connection.prepareStatement(updateQuery)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            changes = stmt.executeUpdate();

            if (debug) {
                System.out.println("---");
                System.out.println(updateQuery);
                System.out.println("Changed rows: " + changes);
                System.out.println("---");
            }
        }

        return changes;
    }

    private void debug(ResultSet rs) throws SQLException {
        int columns = rs.getMetaData().getColumnCount();
        for (int i = 0; i < columns; i++) {
            System.out.print(
                    rs.getObject(i + 1) + ":"
                    + rs.getMetaData().getColumnName(i + 1) + "  ");
        }

        System.out.println();
    }
}
