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
    private UserDao userDao;
    private MessageDao messageDao;
    private SubforumDao forumDao;

    public ThreadDao(Database database) {
        this.database = database;
    }

    public void initDaos(UserDao userDao, MessageDao messageDao, SubforumDao forumDao) {
        this.userDao = userDao;
        this.messageDao = messageDao;
        this.forumDao = forumDao;
    }

    public Thread findOne(int threadId) throws SQLException {
        List<Thread> row = database.queryAndCollect(
                "SELECT * FROM Thread WHERE threadId = ?;",
                rs -> {
                    return new Thread(
                            threadId,
                            forumDao.findOne(rs.getInt("forumId")),
                            userDao.findOne(rs.getInt("sender")),
                            null,
                            rs.getString("name"),
                            new Timestamp(rs.getLong("dateTime")),
                            rs.getInt("postcount")
                 );
                }, threadId);

        return !row.isEmpty() ? row.get(0) : null;
    }

    public List<Thread> findAll() throws SQLException {
        List<Thread> threads = new ArrayList<>();
        Map<Integer, List<Thread>> subforumRefs = new HashMap<>();
        Map<Integer, List<Thread>> senderRefs = new HashMap<>();
        Map<Integer, List<Thread>> messageRefs = new HashMap<>();

        try (ResultSet rs = database.query("SELECT * FROM Thread;")) {
            while (rs.next()) {
                int id = rs.getInt("threadId");
                int forum = rs.getInt("forumId");
                int sender = rs.getInt("sender");
                int lastMsg = rs.getInt("lastMessage");
                Timestamp date = new Timestamp(rs.getLong("dateTime"));
                String name = rs.getString("name");
                int postcount = rs.getInt("postcount");

                Thread thread = new Thread(id, null, null, null, name, date, postcount);
                thread.setForum(forumDao.findOne(forum));
                thread.setLastMessage(messageDao.findOne(lastMsg));
                thread.setSender(userDao.findOne(sender));

                threads.add(thread);

                subforumRefs.putIfAbsent(forum, new ArrayList<>());
                subforumRefs.get(forum).add(thread);
                senderRefs.putIfAbsent(sender, new ArrayList<>());
                senderRefs.get(sender).add(thread);
                messageRefs.putIfAbsent(lastMsg, new ArrayList<>());
                messageRefs.get(lastMsg).add(thread);
            }
        }

        for (Subforum forum : forumDao.findAllIn(subforumRefs.keySet())) {
            for (Thread thread : subforumRefs.get(forum.getForumId())) {
                thread.setForum(forum);
            }
        }

        for (User user : userDao.findAllIn(senderRefs.keySet())) {
            for (Thread thread : senderRefs.get(user.getUserId())) {
                thread.setSender(user);
            }
        }

        for (Message message : messageDao.findAllIn(messageRefs.keySet())) {
            for (Thread thread : messageRefs.get(message.getMessageId())) {
                thread.setLastMessage(message);
            }
        }

        return threads;
    }

    public List<Thread> findAllIn(Collection<Integer> keys) throws SQLException {
        return new ArrayList<>();
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
                Timestamp date = new Timestamp(rs.getLong("dateTime"));
                String name = rs.getString("name");
                int postcount = rs.getInt("postcount");
                
                Thread thread = new Thread(id, forum, name, date, postcount);
                threads.add(thread);

                senderRefs.putIfAbsent(sender, new ArrayList<>());
                senderRefs.get(sender).add(thread);
                lastMessageRefs.putIfAbsent(lastMessage, new ArrayList<>());
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
