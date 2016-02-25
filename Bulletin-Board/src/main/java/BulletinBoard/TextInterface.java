package BulletinBoard;

import Dao.MessageDao;
import Dao.SubforumDao;
import Dao.ThreadDao;
import Dao.UserDao;
import Domain.Message;
import Domain.Subforum;
import Domain.Thread;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TextInterface {

    private final Database database; //should these be somewhere else?
    private final SubforumDao subforums;
    private final ThreadDao threads;
    private final MessageDao messages;
    private final UserDao users;

    public TextInterface(Database database) throws SQLException {
        this.database = database;
        //this.database.setDebugMode(true);   

        subforums = new SubforumDao(database);
        threads = new ThreadDao(database);
        messages = new MessageDao(database);
        users = new UserDao(database);

        threads.setDaos(users, messages, subforums);
        messages.setDaos(threads, users);
    }

    private void listSubforums() throws SQLException {
        System.out.println("Alue\t\tViestejä yhteensä\tViimeisin Viesti");
        for (Subforum forum : subforums.findAll()) {
            Message lastMessage = messages.findLastMessageForForum(forum.getForumId());
            System.out.println(forum.getName() + "\t" + forum.getPostcount()
                    + "\t\t\t" + (lastMessage != null ? lastMessage.getDateTime() : ""));
        }
        //print time without ms
    }

    public void start() throws SQLException {
        listSubforums();

        List<Integer> list = Arrays.asList(1, 2, 3);
        for (Message message : messages.findAllIn(list)) {
            System.out.println(message.getContent());
        }
    }
}
