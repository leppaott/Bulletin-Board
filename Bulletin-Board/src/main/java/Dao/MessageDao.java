package Dao;

import BulletinBoard.Database;
import Domain.Message;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MessageDao {

    private final Database database;

    public MessageDao(Database database) {
        this.database = database;
    }

    public Message findOne(int messageId) throws SQLException {
        List<Message> row = database.queryAndCollect(
                "SELECT * FROM Message WHERE messageId = ?;", rs -> {
                    return new Message(
                            rs.getInt("messageId"),
                            null, // get threadId:Thread
                            null, // get sender:User
                            rs.getInt("order"),
                            rs.getTimestamp("dateTime"),
                            rs.getString("content")
                    );
                }, messageId);

        return !row.isEmpty() ? row.get(0) : null;
    }

    public List<Message> findAll() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<Message> findAllIn(int threadId) throws SQLException {
        return database.queryAndCollect(
                "SELECT * FROM Message WHERE threadId = ?;",
                rs -> {
                    return new Message(
                            rs.getInt("messageId"),
                            null, // get threadId:Thread
                            null, // get sender:User
                            rs.getInt("order"),
                            rs.getTimestamp("dateTime"),
                            rs.getString("content"));
                }, threadId);
    }
    
    public Message findLastMessage(int forumId) throws SQLException {
         List<Message> row = database.queryAndCollect(
                "SELECT * FROM Message m, Thread t WHERE t.forumId = ? "
                        + "AND t.lastMessage = m.messageId "
                        + "ORDER BY m.dateTime DESC LIMIT 1;",
                rs -> {
                    return new Message(
                            rs.getInt("messageId"),
                            null, // get threadId:Thread
                            null, // get sender:User
                            rs.getInt("order"),
                            rs.getTimestamp("dateTime"),
                            rs.getString("content"));
                }, forumId);
         return !row.isEmpty() ? row.get(0) : null;
    }
}
