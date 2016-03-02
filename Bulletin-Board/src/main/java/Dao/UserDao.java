package Dao;

import BulletinBoard.Database;
import Domain.User;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
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
                            new Timestamp(rs.getLong("joinDate")),
                            rs.getInt("postcount")
                    );
                }, userId);

        return !row.isEmpty() ? row.get(0) : null;
    }

    public List<User> findAll() throws SQLException {
        List<User> users = new ArrayList<>();
        
        try (ResultSet rs = database.query("SELECT * FROM User;")) {
            while (rs.next()) {
                int userid = rs.getInt("userId");
                String username = rs.getString("username");
                Timestamp joinDate = rs.getTimestamp("joinDate");
                int postcount = rs.getInt("postcount");
                
                users.add(new User(userid, username, joinDate, postcount));
                
            }
        }
        return users;
    }

    public List<User> findAllIn(Collection<Integer> keys) throws SQLException {
        List<User> users = new ArrayList<>();
        
        StringBuilder params = new StringBuilder("?");
        for (int i = 1; i < keys.size(); i++) {
            params.append(", ?");
        }
        
        try (ResultSet rs = database.query("SELECT * FROM User WHERE userId IN ("
             + params + ");", keys)) {
                while (rs.next()) {
                    int userid = rs.getInt("userId");
                    String username = rs.getString("username");
                    Timestamp joinDate = rs.getTimestamp("joinDate");
                    int postcount = rs.getInt("postcount");

                    users.add(new User(userid, username, joinDate, postcount));
                    }
        }
        
        return new ArrayList<>();
    }
}
