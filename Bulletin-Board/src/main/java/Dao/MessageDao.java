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

    public void initDaos(ThreadDao threadDao, UserDao userDao) {
        this.threadDao = threadDao;
        this.userDao = userDao;
    }
    
    public boolean addMessage(int threadId, int senderId, String content) throws SQLException {
        Thread thread = threadDao.findOne(threadId);

        if (thread == null) {
            return false;
        }

        Message lastMsg = findOne(thread.getLastMessage());
        int order = lastMsg.getOrder() + 1;
        long dateTime = System.currentTimeMillis();

        database.update("INSERT INTO Message (threadId, sender, order, dateTime, content)"
                + "VALUES(?, ?, ?, ?, '?');", threadId, senderId, order, dateTime, content);

        Message newLastMsg = findOne(threadId, order);
        
        if (newLastMsg == null) {
            return false;
        }
        
        return threadDao.editThread(threadId, newLastMsg.getMessageId(), thread.getPostcount() + 1);
    }

    public boolean editMessage(int messageId, String newContent) throws SQLException {
        int changes = database.update("UPDATE Message SET content='?' WHERE messageId=?;", 
                newContent, messageId);

        return changes != 0;
    }
    
    public Message findOne(int threadId, int order) throws SQLException {
        List<Message> row = database.queryAndCollect(
                "SELECT * FROM Message WHERE threadId=? AND order=?;", rs -> {
                    return new Message(
                            rs.getInt("messageId"),
                            threadDao.findOne(rs.getInt("threadId")),
                            userDao.findOne(rs.getInt("sender")),
                            rs.getInt("order"),
                            new Timestamp(rs.getLong("dateTime")),
                            rs.getString("content")
                    );
                }, threadId, order);

        return !row.isEmpty() ? row.get(0) : null;
    }

    public Message findOne(int messageId) throws SQLException {
        List<Message> row = database.queryAndCollect(
                "SELECT * FROM Message WHERE messageId=?;", rs -> {
                    return new Message(
                            rs.getInt("messageId"),
                            threadDao.findOne(rs.getInt("threadId")),
                            userDao.findOne(rs.getInt("sender")),
                            rs.getInt("order"),
                            new Timestamp(rs.getLong("dateTime")),
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
                Timestamp date = new Timestamp(rs.getLong("dateTime"));
                String content = rs.getString("content");

                Message msg = new Message(id, null, null, order, date, content);
                messages.add(msg);

                threadRefs.putIfAbsent(thread, new ArrayList<>());
                threadRefs.get(thread).add(msg);
                senderRefs.putIfAbsent(sender, new ArrayList<>());
                senderRefs.get(sender).add(msg);
            }
        }

        for (User user : userDao.findAllIn(senderRefs.keySet())) {
            for (Message message : senderRefs.get(user.getUserId())) {
                message.setSender(user);
            }
        }

        for (Thread thread : threadDao.findAllIn(threadRefs.keySet())) {
            for (Message message : threadRefs.get(thread.getThreadId())) {
                message.setThread(thread);
            }
        }

        return messages;
    }

    public List<Message> findAllIn(Collection<Integer> keys) throws SQLException {
        List<Message> messages = new ArrayList<>();
        Map<Integer, List<Message>> threadRefs = new HashMap<>();
        Map<Integer, List<Message>> senderRefs = new HashMap<>();

        try (ResultSet rs = database.query("SELECT * FROM Message WHERE messageId IN ("
                + database.getListPlaceholder(keys.size()) + ");", keys)) {
            while (rs.next()) {
                int id = rs.getInt("messageId");
                int sender = rs.getInt("sender");
                int thread = rs.getInt("threadId");
                int order = rs.getInt("order");
                String content = rs.getString("content");
                Timestamp date = new Timestamp(rs.getLong("dateTime"));

                Message msg = new Message(id, null, null, order, date, content);
                messages.add(msg);

                senderRefs.putIfAbsent(sender, new ArrayList<>());
                senderRefs.get(sender).add(msg);
                threadRefs.putIfAbsent(thread, new ArrayList<>());
                threadRefs.get(thread).add(msg);
            }
        }

        for (User user : userDao.findAllIn(senderRefs.keySet())) {
            for (Message message : senderRefs.get(user.getUserId())) {
                message.setSender(user);
            }
        }

        for (Thread thread : threadDao.findAllIn(threadRefs.keySet())) {
            for (Message message : threadRefs.get(thread.getThreadId())) {
                message.setThread(thread);
            }
        }

        return messages;
    }

    public List<Message> findAllIn(int threadId) throws SQLException {
        List<Message> messages = new ArrayList<>();
        Map<Integer, List<Message>> senderRefs = new HashMap<>();

        Thread thread = threadDao.findOne(threadId);

        try (ResultSet rs = database.query("SELECT * FROM Message WHERE threadId=?;", threadId)) {
            while (rs.next()) {
                int id = rs.getInt("messageId");
                int sender = rs.getInt("sender");
                int order = rs.getInt("order");
                String content = rs.getString("content");
                Timestamp date = new Timestamp(rs.getLong("dateTime"));

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

    public Message findLastMessageForForum(int forumId) throws SQLException {
        List<Message> row = database.queryAndCollect(
                "SELECT * FROM Message m, Thread t WHERE t.forumId=? "
                + "AND t.lastMessage=m.messageId "
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

    public Message findLastMessageForThread(int threadId) throws SQLException {
        List<Message> row = database.queryAndCollect(
                "SELECT * FROM Message m, Thread t WHERE t.threadId=? "
                + "AND t.lastMessage=m.messageId;",
                rs -> {
                    return new Message(
                            rs.getInt("messageId"),
                            threadDao.findOne(rs.getInt("threadId")),
                            userDao.findOne(rs.getInt("sender")),
                            rs.getInt("order"),
                            rs.getTimestamp("dateTime"),
                            rs.getString("content"));
                }, threadId);

        return !row.isEmpty() ? row.get(0) : null;
    }
}
