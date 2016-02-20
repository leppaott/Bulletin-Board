package BulletinBoard;

import Dao.MessageDao;
import Dao.SubforumDao;
import Dao.ThreadDao;
import Dao.UserDao;
import Domain.Message;
import Domain.Subforum;
import Domain.Thread;
import java.sql.SQLException;

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
    }

    private void listSubforums() throws SQLException {
        System.out.println("Alue\t\tViestejä yhteensä\tViimeisin Viesti");
        for (Subforum forum : subforums.findAll()) {
            Message lastMessage = messages.findLastMessage(forum.getForumId());
            System.out.println(forum.getName() + "\t" + forum.getPostcount()
                    + "\t\t\t" + (lastMessage != null ? lastMessage.getDateTime() : ""));
        }
        //print time without ms
    }

    public void start() throws SQLException {
        listSubforums();
        System.out.println();
//        for (Thread thread : threads.findAllIn(subforums.findAll().get(0).getForumId())) {
//            System.out.println(thread.getName());
//        }
    }
}
