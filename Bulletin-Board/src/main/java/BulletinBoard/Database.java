package BulletinBoard;

import Domain.Collector;
import java.sql.*;
import java.util.*;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;

public class Database {

    private boolean debug;
    private Connection connection;
    private RowSetFactory factory;

    public Database(String address) throws Exception {
        this.connection = DriverManager.getConnection(address);
        this.factory = RowSetProvider.newFactory();
    }

    public Connection getConnection() {
        return connection;
    }

    public void setDebugMode(boolean d) {
        debug = d;
    }

    public CachedRowSet query(String query, Object... params) throws SQLException {
        CachedRowSet crs = factory.createCachedRowSet();

        if (debug) {
            System.out.println("---");
            System.out.println("Executing: " + query);
            System.out.println("---");
        }

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            int i = 1;
            for (Object object : params) {
                if (object instanceof Collection) { //perhaps better way...
                    for (Object obj : (Collection) object) {
                        stmt.setObject(i++, obj);
                    }
                } else {
                    stmt.setObject(i++, object);
                }
            }

            ResultSet rs = stmt.executeQuery();
            crs.populate(rs);

            if (debug) {
                System.out.println("---");
                System.out.println(query);
                debug(rs);
                System.out.println("---");
            }
        }

        return crs;
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
