package Dao;

import BulletinBoard.Database;
import Domain.Message;
import Domain.Thread;
import Domain.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Message WHERE threadId = ?;");
        stmt.setInt(1, threadId);
        ResultSet rs = stmt.executeQuery();

        List<Message> messages = new ArrayList<>();
        Map<Integer, List<Message>> threadRefs = new HashMap<>();
        Map<Integer, List<Message>> senderRefs = new HashMap<>();

        while (rs.next()) {
            int id = rs.getInt("messageId");
            int thread = rs.getInt("threadId");
            int sender = rs.getInt("sender");
            int order = rs.getInt("order");
            Timestamp date = rs.getTimestamp("dateTime");
            String content = rs.getString("content");

            Message msg = new Message(id, order, date, content);
            messages.add(msg);

            threadRefs.putIfAbsent(thread, new ArrayList<Message>());
            threadRefs.get(thread).add(msg);
            senderRefs.putIfAbsent(sender, new ArrayList<Message>());
            senderRefs.get(sender).add(msg);
        }

        rs.close();
        stmt.close();
        connection.close();

        ThreadDao threadDao = new ThreadDao(database);
        for (Thread thread : threadDao.findAllIn(threadRefs.keySet())) {
            for (Message message : threadRefs.get(thread.getThreadId())) {
                message.setThread(thread);
            }
        }

        UserDao userDao = new UserDao(database);
        for (User user : userDao.findAllIn(senderRefs.keySet())) {
            for (Message message : senderRefs.get(user.getUserId())) {
                message.setSender(user);
            }
        }

        return messages;
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
