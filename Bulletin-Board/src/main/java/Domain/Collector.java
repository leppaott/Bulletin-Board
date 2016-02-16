package Domain;

import java.sql.*;

public interface Collector<T> {
    T collect(ResultSet rs) throws SQLException;
}
