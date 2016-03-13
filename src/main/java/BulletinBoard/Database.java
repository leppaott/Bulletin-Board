package BulletinBoard;

import Domain.Collector;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.*;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;

public class Database {

    private final Connection connection;
    private final RowSetFactory factory;
    private boolean postgres;
    private boolean debug;

    public Database(String address) throws SQLException {
        String envAddress = System.getenv("JDBC_DATABASE_URL");

        if (envAddress != null) {
            address = envAddress;
        }

        this.postgres = false;
        this.connection = getConnection(address);
        this.factory = RowSetProvider.newFactory();
    }

    private Connection getConnection(String address) throws SQLException {
        if (address.contains("postgres")) {
            try {
                URI dbUri = new URI(address);

                String username = dbUri.getUserInfo().split(":")[0];
                String password = dbUri.getUserInfo().split(":")[1];
                String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':'
                        + dbUri.getPort() + dbUri.getPath();
                postgres = true;
                
                return DriverManager.getConnection(dbUrl, username, password);
            } catch (URISyntaxException | SQLException e) {
            }
        }
        
        return DriverManager.getConnection(address);
    }
   
    public boolean getPostgres() {
        return postgres;
    }
    
    public void setDebugMode(boolean d) {
        debug = d;
    }

    public String getListPlaceholder(int size) {
        if (size == 0) {
            return "";
        }
        
        StringBuilder params = new StringBuilder("?");

        for (int i = 1; i < size; i++) {
            params.append(", ?");
        }

        return params.toString();
    }

    private void handleParams(PreparedStatement stmt, Object... params) throws SQLException {
        int i = 1;
        for (Object object : params) {
            if (object instanceof Collection) {
                for (Object obj : (Collection) object) {
                    stmt.setObject(i++, obj);
                }
            } else {
                stmt.setObject(i++, object);
            }
        }
    }

    private PreparedStatement prepareStatement(String query, Object... params) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(query);

        handleParams(stmt, params);

        return stmt;
    }

    public CachedRowSet query(String query, Object... params) throws SQLException {
        if (debug) {
            System.out.println("---");
            System.out.println("Executing: " + query);
            System.out.println("---");
        }

        CachedRowSet crs = factory.createCachedRowSet();

        try (PreparedStatement stmt = prepareStatement(query, params)) {
            ResultSet rs = stmt.executeQuery();
            crs.populate(rs);
        }

        return crs;
    }

    public <T> List<T> queryAndCollect(String query, Collector<T> col, Object... params) throws SQLException {
        if (debug) {
            System.out.println("---");
            System.out.println("Executing: " + query);
            System.out.println("---");
        }

        List<T> rows = new ArrayList<>();

        try (PreparedStatement stmt = prepareStatement(query, params)) {
            ResultSet rs = stmt.executeQuery();

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

        return rows;
    }

    public int update(String updateQuery, Object... params) throws SQLException {
        int changes;

        try (PreparedStatement stmt = prepareStatement(updateQuery, params)) {
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

    public int insert(String insertQuery, Object... params) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            handleParams(stmt, params);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1); //id
            }
        }

        return -1;
    }

    private void debug(ResultSet rs) throws SQLException {
        int columns = rs.getMetaData().getColumnCount();
        for (int i = 0; i < columns; i++) {
            System.out.print(rs.getObject(i + 1) + ":"
                    + rs.getMetaData().getColumnName(i + 1) + "  ");
        }

        System.out.println();
    }
    

}
