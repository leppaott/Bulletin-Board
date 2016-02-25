package BulletinBoard;

import Dao.MessageDao;
import Dao.SubforumDao;
import Dao.ThreadDao;
import Dao.UserDao;
import Domain.Thread;
import Domain.Message;
import Domain.Subforum;
import Domain.User;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class BulletinBoard {

    private final Database database;
    private final SubforumDao subforums;
    private final ThreadDao threads;
    private final MessageDao messages;
    private final UserDao users;

    public BulletinBoard(Database database) throws SQLException {
        this.database = database;
        //this.database.setDebugMode(true);   

        subforums = new SubforumDao(database);
        threads = new ThreadDao(database);
        messages = new MessageDao(database);
        users = new UserDao(database);

        threads.initDaos(users, messages, subforums);
        messages.initDaos(threads, users);
    }

    //subforums
    public Subforum getSubforum(int forumId) throws SQLException {
        return subforums.findOne(forumId);
    }

    public List<Subforum> getSubforums() throws SQLException {
        List<Subforum> forums = subforums.findAll();

        for (Subforum forum : forums) {
            forum.setLastMessage(messages.findLastMessageForForum(forum.getForumId()));
        }

        return forums;
    }

    public List<Subforum> getSubforumsIn(Collection<Integer> keys) throws SQLException {
        List<Subforum> forums = subforums.findAllIn(keys);

        for (Subforum forum : forums) {
            forum.setLastMessage(messages.findLastMessageForForum(forum.getForumId()));
        }

        return forums;
    }

    //messages
    public Message getMessage(int messageId) throws SQLException {
        return messages.findOne(messageId);
    }

    public List<Message> getMessages() throws SQLException {
        return messages.findAll();
    }

    public List<Message> getMessagesIn(Collection<Integer> keys) throws SQLException {
        return messages.findAllIn(keys);
    }

    public List<Message> getMessagesIn(int threadId) throws SQLException {
        return messages.findAllIn(threadId);
    }

    public Message findLastMessageForForum(int forumId) throws SQLException {
        return messages.findLastMessageForForum(forumId);
    }

    public Message findLastMessageForThread(int threadId) throws SQLException {
        return messages.findLastMessageForThread(threadId);
    }

    //threads
    public Thread getThread(int threadId) throws SQLException {
        return threads.findOne(threadId);
    }

    public List<Thread> getThreads() throws SQLException {
        return threads.findAll();
    }

    public List<Thread> getThreadsIn(Collection<Integer> keys) throws SQLException {
        return threads.findAllIn(keys);
    }

    public List<Thread> getThreadsIn(int forumId) throws SQLException {
        return threads.findAllIn(forumId);
    }

    //users
    public User getUser(int userId) throws SQLException {
        return users.findOne(userId);
    }

    public List<User> getUsers() throws SQLException {
        return users.findAll();
    }

    public List<User> getUsersIn(Collection<Integer> keys) throws SQLException {
        return users.findAllIn(keys);
    }
}
