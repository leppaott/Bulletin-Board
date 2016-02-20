
package Dao;

import BulletinBoard.Database;
import Domain.Dao;
import Domain.Message;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class MessageDao implements Dao<Message, Integer> {
    
    private final Database database;
    
    public MessageDao(Database database) {
        this.database = database;
    }
    
    @Override
    public Message findOne(Integer key) throws SQLException {
                        List<Message> row = database.queryAndCollect(
                "SELECT * FROM Subforum WHERE forumId = ?;", rs -> {
                    return new Message(
                            rs.getInt("messageId"),
                            null, // get threadId:Thread
                            null, // get sender:User
                            rs.getInt("order"),
                            rs.getTimestamp("dateTime"),
                            rs.getString("content")
                    );
                }, key);

        return !row.isEmpty() ? row.get(0) : null;
    }

    @Override
    public List<Message> findAll() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public List<Message> findAllIn(int threadId) {
        //todo
        return null;
    }

    @Override
    public List<Message> findAllIn(Collection<Integer> keys) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(Integer key) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
