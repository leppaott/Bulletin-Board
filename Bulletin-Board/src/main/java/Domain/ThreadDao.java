package Domain;

import BulletinBoard.Database;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class ThreadDao implements Dao<Thread, Integer> {

    private final Database database;

    public ThreadDao(Database database) {
        this.database = database;
    }

    @Override
    public Thread findOne(Integer key) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Thread> findAll() throws SQLException {
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
                            null); //handle dateTime:LocalDateTime/DateTime
                }, forumId);
    }

    @Override
    public List<Thread> findAllIn(Collection<Integer> keys) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(Integer key) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
