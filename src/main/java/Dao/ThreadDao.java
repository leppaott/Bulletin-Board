package Dao;

import BulletinBoard.Database;
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

    public int addThread(int forumId, int senderId, String name) throws SQLException {
        long dateTime = System.currentTimeMillis();

        int threadId = database.insert("INSERT INTO Thread (forumId, sender,"
                + " dateTime, name, postcount) VALUES(?, ?, ?, ?, ?);",
                forumId, senderId, dateTime, name, 0);

        return threadId;
    }

    public boolean editThread(int threadId, int lastMessageId, int postcount) throws SQLException {
        int changes = database.update("UPDATE Thread SET lastMessage=?, postcount=? WHERE threadId=?;",
                lastMessageId, postcount, threadId);

        return changes != 0;
    }

    public boolean editThread(int threadId, String name) throws SQLException {
        int changes = database.update("UPDATE Thread SET name=? WHERE threadId=?;",
                name, threadId);

        return changes != 0;
    }

    public Thread findOne(int threadId) throws SQLException {
        List<Thread> row = database.queryAndCollect(
                "SELECT * FROM Thread WHERE threadId=?;",
                rs -> {
                    return new Thread(
                            threadId,
                            forumDao.findOne(rs.getInt("forumId")),
                            userDao.findOne(rs.getInt("sender")),
                            rs.getInt("lastMessage"),
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

        try (ResultSet rs = database.query("SELECT * FROM Thread;")) {
            while (rs.next()) {
                int id = rs.getInt("threadId");
                int forum = rs.getInt("forumId");
                int sender = rs.getInt("sender");
                int lastMsg = rs.getInt("lastMessage");
                Timestamp date = new Timestamp(rs.getLong("dateTime"));
                String name = rs.getString("name");
                int postcount = rs.getInt("postcount");

                Thread thread = new Thread(id, null, null, lastMsg, name, date, postcount);
                threads.add(thread);

                subforumRefs.putIfAbsent(forum, new ArrayList<>());
                subforumRefs.get(forum).add(thread);
                senderRefs.putIfAbsent(sender, new ArrayList<>());
                senderRefs.get(sender).add(thread);
            }
        }

        if (!subforumRefs.isEmpty()) {
            for (Subforum forum : forumDao.findAllIn(subforumRefs.keySet())) {
                for (Thread thread : subforumRefs.get(forum.getForumId())) {
                    thread.setForum(forum);
                }
            }
        }

        if (!senderRefs.isEmpty()) {
            for (User user : userDao.findAllIn(senderRefs.keySet())) {
                for (Thread thread : senderRefs.get(user.getUserId())) {
                    thread.setSender(user);
                }
            }
        }

        return threads;
    }

    public List<Thread> findAllIn(Collection<Integer> keys) throws SQLException {
        List<Thread> threads = new ArrayList<>();
        Map<Integer, List<Thread>> subforumRefs = new HashMap<>();
        Map<Integer, List<Thread>> senderRefs = new HashMap<>();

        try (ResultSet rs = database.query("SELECT * FROM Thread WHERE threadId IN ("
                + database.getListPlaceholder(keys.size()) + ");", keys)) {
            while (rs.next()) {
                int id = rs.getInt("threadId");
                int forum = rs.getInt("forumId");
                int sender = rs.getInt("sender");
                int lastMsg = rs.getInt("lastMessage");
                Timestamp date = new Timestamp(rs.getLong("dateTime"));
                String name = rs.getString("name");
                int postcount = rs.getInt("postcount");

                Thread thread = new Thread(id, null, null, lastMsg, name, date, postcount);
                threads.add(thread);

                subforumRefs.putIfAbsent(forum, new ArrayList<>());
                subforumRefs.get(forum).add(thread);
                senderRefs.putIfAbsent(sender, new ArrayList<>());
                senderRefs.get(sender).add(thread);
            }
        }

        if (!subforumRefs.isEmpty()) {
            for (Subforum forum : forumDao.findAllIn(subforumRefs.keySet())) {
                for (Thread thread : subforumRefs.get(forum.getForumId())) {
                    thread.setForum(forum);
                }
            }
        }

        if (!senderRefs.isEmpty()) {
            for (User user : userDao.findAllIn(senderRefs.keySet())) {
                for (Thread thread : senderRefs.get(user.getUserId())) {
                    thread.setSender(user);
                }
            }
        }

        return threads;
    }

    public List<Thread> findAllIn(int forumId) throws SQLException {
        List<Thread> threads = new ArrayList<>();
        Map<Integer, List<Thread>> senderRefs = new HashMap<>();

        Subforum forum = forumDao.findOne(forumId);

        try (ResultSet rs = database.query("SELECT t.threadId, t.sender, t.lastMessage, "
                + "t.dateTime, t.name, t.postcount FROM Thread t, Message m WHERE t.forumId=? "
                + "AND t.lastMessage=m.messageId ORDER BY m.dateTime DESC LIMIT 10;", forumId)) {
            while (rs.next()) {
                int id = rs.getInt("threadId");
                int sender = rs.getInt("sender");
                int lastMsg = rs.getInt("lastMessage");
                Timestamp date = new Timestamp(rs.getLong("dateTime"));
                String name = rs.getString("name");
                int postcount = rs.getInt("postcount");

                Thread thread = new Thread(id, forum, null, lastMsg, name, date, postcount);
                threads.add(thread);

                senderRefs.putIfAbsent(sender, new ArrayList<>());
                senderRefs.get(sender).add(thread);
            }
        }

        if (!senderRefs.isEmpty()) {
            for (User user : userDao.findAllIn(senderRefs.keySet())) {
                for (Thread thread : senderRefs.get(user.getUserId())) {
                    thread.setSender(user);
                }
            }
        }

        return threads;
    }
}
