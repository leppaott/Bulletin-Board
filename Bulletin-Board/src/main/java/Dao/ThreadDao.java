package Dao;

import BulletinBoard.Database;
import Domain.Thread;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public class ThreadDao {

    private final Database database;

    public ThreadDao(Database database) {
        this.database = database;
    }

    public Thread findOne(Integer key) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<Thread> findAll() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<Thread> findAllIn(Set<Integer> keys) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<Thread> findAllIn(int forumId) throws SQLException {
        return database.queryAndCollect(
                "SELECT * FROM Thread WHERE forumId = ?;",
                rs -> {
                    return new Thread(
                            rs.getInt("threadId"),
                            rs.getInt("forumId"),
                            null, //handle finding sender:User
                            null, //handle lastMessage:Message
                            rs.getString("name"),
                            rs.getTimestamp("dateTime"));
                }, forumId);
    }
}
