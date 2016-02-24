package Dao;

import BulletinBoard.Database;
import Domain.Message;
import Domain.Thread;
import Domain.User;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageDao {

    private final Database database;
    private ThreadDao threadDao;
    private UserDao userDao;

    public MessageDao(Database database) {
        this.database = database;
    }
    
    public void setDaos(ThreadDao threadDao, UserDao userDao) {
        this.threadDao = threadDao;
        this.userDao = userDao;
    }

    public Message findOne(int messageId) throws SQLException {
        List<Message> row = database.queryAndCollect(
                "SELECT * FROM Message WHERE messageId = ?;", rs -> {
                    return new Message(
                            rs.getInt("messageId"),
                            threadDao.findOne(rs.getInt("threadId")),
                            userDao.findOne(rs.getInt("sender")),
                            rs.getInt("order"),
                            rs.getTimestamp("dateTime"),
                            rs.getString("content")
                    );
                }, messageId);

        return !row.isEmpty() ? row.get(0) : null;
    }

    public List<Message> findAll() throws SQLException {
        List<Message> messages = new ArrayList<>();
        Map<Integer, List<Message>> threadRefs = new HashMap<>();
        Map<Integer, List<Message>> senderRefs = new HashMap<>();

        try (ResultSet rs = database.query("SELECT * FROM Message;")) {
            while (rs.next()) {
                int id = rs.getInt("messageId");
                int thread = rs.getInt("threadId");
                int sender = rs.getInt("sender");
                int order = rs.getInt("order");
                Timestamp date = rs.getTimestamp("dateTime");
                String content = rs.getString("content");

                Message msg = new Message(id, null, null, order, date, content);
                messages.add(msg);

                threadRefs.putIfAbsent(thread, new ArrayList<>());
                threadRefs.get(thread).add(msg);
                senderRefs.putIfAbsent(sender, new ArrayList<>());
                senderRefs.get(sender).add(msg);
            }
        }

        for (Thread thread : threadDao.findAllIn(threadRefs.keySet())) {
            for (Message message : threadRefs.get(thread.getThreadId())) {
                message.setThread(thread);
            }
        }

        for (User user : userDao.findAllIn(senderRefs.keySet())) {
            for (Message message : senderRefs.get(user.getUserId())) {
                message.setSender(user);
            }
        }

        return messages;
    }

    public List<Message> findAllIn(int threadId) throws SQLException {
        List<Message> messages = new ArrayList<>();
        Map<Integer, List<Message>> senderRefs = new HashMap<>();

        Thread thread = threadDao.findOne(threadId);

        try (ResultSet rs = database.query("SELECT * FROM Message WHERE threadId = ?;", threadId)) {
            while (rs.next()) {
                int id = rs.getInt("messageId");
                int sender = rs.getInt("sender");
                int order = rs.getInt("order");
                Timestamp date = rs.getTimestamp("dateTime");
                String content = rs.getString("content");

                Message msg = new Message(id, thread, null, order, date, content);
                messages.add(msg);

                senderRefs.putIfAbsent(sender, new ArrayList<>());
                senderRefs.get(sender).add(msg);
            }
        }

        for (User user : userDao.findAllIn(senderRefs.keySet())) {
            for (Message message : senderRefs.get(user.getUserId())) {
                message.setSender(user);
            }
        }

        return messages;
    }

    public List<Message> findAllIn(Collection<Integer> keys) throws SQLException {
        List<Message> messages = new ArrayList<>();
        
        StringBuilder params = new StringBuilder("?");
        for (int i = 1; i < keys.size(); i++) {
            params.append(", ?");
        }
        
        try (ResultSet rs = database.query("SELECT * FROM Message WHERE messageId IN (" 
                + params +");", keys)) {
            while (rs.next()) {
               messages.add(new Message(rs.getInt("messageId"),
                            threadDao.findOne(rs.getInt("threadId")), //TODO perhaps
                            userDao.findOne(rs.getInt("sender")),   //TODO
                            rs.getInt("order"),
                            rs.getTimestamp("dateTime"),
                            rs.getString("content")));
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
                            threadDao.findOne(rs.getInt("threadId")),
                            userDao.findOne(rs.getInt("sender")),
                            rs.getInt("order"),
                            rs.getTimestamp("dateTime"),
                            rs.getString("content"));
                }, forumId);

        return !row.isEmpty() ? row.get(0) : null;
    }
}
