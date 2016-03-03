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
    /**
     * Adds a new subforum with a given name.
     *
     * @param forumName
     * @return
     * @throws SQLException
     */
    public boolean addSubforum(String forumName) throws SQLException {
        return subforums.addSubforum(forumName);
    }

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
    /**
     * Adds a new message into the database.
     *
     * @param threadId
     * @param senderId
     * @param content
     * @return
     * @throws SQLException
     */
    public boolean addMessage(int threadId, int senderId, String content) throws SQLException {
        return messages.addMessage(threadId, senderId, content);
    }

    /**
     * Updates message's content.
     *
     * @param messageId
     * @param newContent
     * @return
     * @throws SQLException
     */
    public boolean editMessage(int messageId, String newContent) throws SQLException {
        return messages.editMessage(messageId, newContent);
    }

    public Message getMessage(int messageId) throws SQLException {
        return messages.findOne(messageId);
    }

    public Message getMessage(int threadId, int order) throws SQLException {
        return messages.findOne(threadId, order);
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
    /**
     * Adds a new thread with a given sender and name.
     *
     * @param forumId
     * @param senderId
     * @param name
     * @return
     * @throws SQLException
     */
    public boolean addThread(int forumId, int senderId, String name) throws SQLException {
        return threads.addThread(forumId, senderId, name);
    }

    /**
     * Updates thread's lastMessage and postcount.
     *
     * @param threadId
     * @param lastMessageId
     * @param postcount
     * @return
     * @throws SQLException
     */
    public boolean editThread(int threadId, int lastMessageId, int postcount) throws SQLException {
        return threads.editThread(threadId, lastMessageId, postcount);
    }

    /**
     * Updates thread's name.
     *
     * @param threadId
     * @param name
     * @return
     * @throws SQLException
     */
    public boolean editThread(int threadId, String name) throws SQLException {
        return threads.editThread(threadId, name);
    }

    public Thread getThread(int threadId) throws SQLException {
        Thread thread = threads.findOne(threadId);
        thread.setLastMessage(messages.findLastMessageForThread(threadId));
        return thread;
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
    /**
     * Adds a new user with a given name.
     *
     * @param name
     * @return
     * @throws SQLException
     */
    public boolean addUser(String name) throws SQLException {
        return users.addUser(name);
    }

    /**
     * Updates user's postcount.
     *
     * @param userId
     * @param postcount
     * @return
     * @throws SQLException
     */
    public boolean editUser(int userId, int postcount) throws SQLException {
        return users.editUser(userId, postcount);
    }

    /**
     * Updates user's name.
     *
     * @param userId
     * @param name
     * @return
     * @throws SQLException
     */
    public boolean editUser(int userId, String name) throws SQLException {
        return users.editUser(userId, name);
    }

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
