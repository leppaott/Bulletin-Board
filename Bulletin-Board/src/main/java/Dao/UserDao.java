package Dao;

import BulletinBoard.Database;
import Domain.Dao;
import Domain.User;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class UserDao implements Dao<User, Integer> {

    private final Database database;

    public UserDao(Database database) {
        this.database = database;
    }

    @Override
    public User findOne(Integer key) throws SQLException {
                List<User> row = database.queryAndCollect(
                "SELECT * FROM Subforum WHERE forumId = ?;", rs -> {
                    return new User(
                            rs.getInt("userId"),
                            rs.getString("username"),
                            rs.getTimestamp("joinDate"),
                            rs.getInt("postcount")
                    );
                }, key);

        return !row.isEmpty() ? row.get(0) : null;
    }

    @Override
    public List<User> findAll() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<User> findAllIn(Collection<Integer> keys) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(Integer key) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
