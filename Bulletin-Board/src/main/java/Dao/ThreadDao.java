package Dao;

import BulletinBoard.Database;
import Domain.Message;
import Domain.Subforum;
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

public class ThreadDao {

    private final Database database;
    private final UserDao userDao;
    private final MessageDao messageDao;
    private final SubforumDao forumDao;

    public ThreadDao(Database database) {
        this.database = database;
        userDao = new UserDao(database);
        messageDao = new MessageDao(database);
        forumDao = new SubforumDao(database); 
    }

    public Thread findOne(int threadId) throws SQLException {
        List<Thread> row = database.queryAndCollect(
                "SELECT * FROM Thread WHERE forumId = ?;",
                rs -> {
                    return new Thread(
                            rs.getInt("threadId"),
                            forumDao.findOne(rs.getInt("forumId")),
                            userDao.findOne(rs.getInt("sender")),
                            messageDao.findOne(rs.getInt("lastMessage")),
                            rs.getString("name"),
                            rs.getTimestamp("dateTime"));
                }, threadId);
        
         return !row.isEmpty() ? row.get(0) : null;
    }

    public List<Thread> findAll() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<Thread> findAllIn(Collection<Integer> keys) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<Thread> findAllIn(int forumId) throws SQLException {
        List<Thread> threads = new ArrayList<>();
        Map<Integer, List<Thread>> senderRefs = new HashMap<>();
        Map<Integer, List<Thread>> lastMessageRefs = new HashMap<>();
        
        Subforum forum = forumDao.findOne(forumId);

        try (ResultSet rs = database.query("SELECT * FROM Thread WHERE forumId = ?;", forumId)) {
            while (rs.next()) {
                int id = rs.getInt("threadId");
                int sender = rs.getInt("sender");
                int lastMessage = rs.getInt("lastMessage");
                Timestamp date = rs.getTimestamp("dateTime");
                String name = rs.getString("name");

                Thread thread = new Thread(id, forum, name, date);
                threads.add(thread);

                senderRefs.putIfAbsent(sender, new ArrayList<>());
                senderRefs.get(sender).add(thread);
                lastMessageRefs.putIfAbsent(lastMessage,  new ArrayList<>());
                lastMessageRefs.get(lastMessage).add(thread);    
            }
        }

        for (User user : userDao.findAllIn(senderRefs.keySet())) {
            for (Thread thread : senderRefs.get(user.getUserId())) {
                thread.setSender(user);
            }
        }
        
        for (Message message : messageDao.findAllIn(lastMessageRefs.keySet())) {
            for (Thread thread : lastMessageRefs.get(message.getMessageId())) {
                thread.setLastMessage(message);
            }
        }

        return threads;
    }
}
