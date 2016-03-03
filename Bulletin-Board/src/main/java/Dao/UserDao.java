package Dao;

import BulletinBoard.Database;
import Domain.User;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

public class UserDao {
    
    private final Database database;
    
    public UserDao(Database database) {
        this.database = database;
    }
    
    public boolean addUser(String name) throws SQLException {
        long dateTime = System.currentTimeMillis();
        
        int changes = database.update("INSERT INTO User (username, joinDate, postcount)"
                + " VALUES(?, ?, 0);", name, dateTime); 

        return changes != 0;         
    }
    
    public boolean editUser(int userId, int postcount) throws SQLException {
        int changes = database.update("UPDATE User SET postcount=? WHERE userId=?;",
                userId, postcount);
        
        return changes != 0;
    }
    
    public boolean editUser(int userId, String name) throws SQLException {
        int changes = database.update("UPDATE User SET username=? WHERE userId=?;",
                name, userId);
        
        return changes != 0;
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
        List<User> users = database.queryAndCollect("SELECT * FROM User;", rs -> {
            return new User(
                    rs.getInt("userId"),
                    rs.getString("username"),
                    new Timestamp(rs.getLong("joinDate")),
                    rs.getInt("postcount")
            );
        });
        
        return users;
    }
    
    public List<User> findAllIn(Collection<Integer> keys) throws SQLException {
        List<User> users = database.queryAndCollect("SELECT * FROM User WHERE userId IN ("
                + database.getListPlaceholder(keys.size()) + ");", rs -> {
                    return new User(
                            rs.getInt("userId"),
                            rs.getString("username"),
                            new Timestamp(rs.getLong("joinDate")),
                            rs.getInt("postcount")
                    );
                }, keys);
        
        return users;
    }
}
