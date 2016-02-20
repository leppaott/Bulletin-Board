package Dao;

import BulletinBoard.Database;
import Domain.User;
import java.sql.SQLException;
import java.util.List;

public class UserDao {

    private final Database database;

    public UserDao(Database database) {
        this.database = database;
    }

    public User findOne(int userId) throws SQLException {
        List<User> row = database.queryAndCollect(
                "SELECT * FROM User WHERE userId = ?;", rs -> {
                    return new User(
                            rs.getInt("userId"),
                            rs.getString("username"),
                            rs.getTimestamp("joinDate"),
                            rs.getInt("postcount")
                    );
                }, userId);

        return !row.isEmpty() ? row.get(0) : null;
    }

    public List<User> findAll() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
