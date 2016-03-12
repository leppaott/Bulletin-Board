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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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
        messages.initDaos(threads, users, subforums);
    }

    public void createTable(String table) throws SQLException {
        database.update("CREATE TABLE " + table + ";"); //not sure why ?;" didn't work
    }

    public void dropTable(String table) throws SQLException {
        database.update("DROP TABLE " + table + ";");
    }

    public void createDb() throws SQLException {
        List<String> statements = this.getStatements();

        for (String table : Arrays.asList("Subforum", "Thread", "Message", "User")) {
            try {
                dropTable(table); //required for postgres
            } catch (SQLException e) {
                //nothing to drop
            }
        }

        if (database.getPostgres()) {
            statements = statements.stream()
                    .map(s -> ((String) s)
                            .replace("integer PRIMARY", "SERIAL PRIMARY"))
                    .collect(Collectors.toList());
        }

        for (String statement : statements) {
            createTable(statement);
        }

        addUser("Arto");
        addUser("Matti");
        addUser("Ada");
        addSubforum("Ohjelmointi");
        addSubforum("Lemmikit");
        addSubforum("Lentokoneet");
        addThread(1, 1, "Java on jees");
        addThread(1, 1, "Python on jeesimpi");
        addThread(1, 2, "LISP on parempi kuin");
        addThread(1, 3, "Ohjelmointikielet on turhia");
        addMessage(1, 1, "Mun mielestä Java on just hyvä kieli.");
        addMessage(1, 2, "No eipäs, Ruby on parempi.");
        addMessage(1, 3, "Ada on selkeästi parempi kuin kumpikin noista.");
        addMessage(1, 1, "Mun mielestä Java on just hyvä kieli.");
        addMessage(2, 1, "Python rulaa :)");
        addMessage(3, 3, "LISP<3");
        addMessage(3, 3, "LISP<3");
        addMessage(4, 3, "LISP<<<<<<");
    }

    private List<String> getStatements() throws SQLException {
        List<String> statements = new ArrayList<>();

        statements.add("Subforum (forumId integer PRIMARY KEY, name text, postcount integer)");
        statements.add("Thread (threadId integer PRIMARY KEY, forumId integer, sender integer, "
                + "lastMessage integer, name text, dateTime Timestamp, postcount integer, "
                + "FOREIGN KEY(forumId) REFERENCES Subforum(forumId), FOREIGN KEY(sender) REFERENCES User(userId), "
                + "FOREIGN KEY(lastMessage) REFERENCES Message(messageId))");
        statements.add("Message (messageId integer PRIMARY KEY, threadId integer, sender integer, "
                + "'order' integer, dateTime Timestamp, content text, FOREIGN KEY(threadId) REFERENCES Thread(threadId), "
                + "FOREIGN KEY(sender) REFERENCES User(userId))");
        statements.add("User (userId integer PRIMARY KEY, username text, joinDate Timestamp, postcount integer)");

        return statements;
    }

    //subforums
    /**
     * Adds a new subforum with a given name. Returns forumId.
     *
     * @param forumName
     * @return forumId
     * @throws SQLException
     */
    public int addSubforum(String forumName) throws SQLException {
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
     * Adds a new message into the database. Returns messageId.
     *
     * @param threadId
     * @param senderId
     * @param content
     * @return messageId
     * @throws SQLException
     */
    public int addMessage(int threadId, int senderId, String content) throws SQLException {
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

    /**
     * [begin,end]
     *
     * @param threadId
     * @param begin
     * @param end
     * @return
     * @throws SQLException
     */
    public List<Message> getMessagesIn(int threadId, int begin, int end) throws SQLException {
        return messages.findAllIn(threadId, begin, end);
    }

    public Message findLastMessageForForum(int forumId) throws SQLException {
        return messages.findLastMessageForForum(forumId);
    }

    public Message findLastMessageForThread(int threadId) throws SQLException {
        return messages.findLastMessageForThread(threadId);
    }

    //threads
    /**
     * Adds a new thread with a given sender and name. Returns threadId.
     *
     * @param forumId
     * @param senderId
     * @param name
     * @return threadId
     * @throws SQLException
     */
    public int addThread(int forumId, int senderId, String name) throws SQLException {
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
        return threads.findOne(threadId);
    }

    public List<Thread> getThreads() throws SQLException {
        return threads.findAll();
    }

    public List<Thread> getThreadsIn(Collection<Integer> keys) throws SQLException {
        return threads.findAllIn(keys);
    }

    /**
     * Ordered by date, only 10 latest.
     *
     * @param forumId
     * @return
     * @throws SQLException
     */
    public List<Thread> getThreadsIn(int forumId) throws SQLException {
        return threads.findAllIn(forumId);
    }

    //users
    /**
     * Adds a new user with a given name. Returns userId.
     *
     * @param name
     * @return userId
     * @throws SQLException
     */
    public int addUser(String name) throws SQLException {
        return users.addUser(name);
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

    /**
     * Returns -1 if not found.
     *
     * @param name
     * @return
     * @throws SQLException
     */
    public int getUserId(String name) throws SQLException {
        return users.findIdByName(name);
    }

    public List<User> getUsers() throws SQLException {
        return users.findAll();
    }

    public List<User> getUsersIn(Collection<Integer> keys) throws SQLException {
        return users.findAllIn(keys);
    }
}
