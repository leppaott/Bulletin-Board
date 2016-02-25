package BulletinBoard;

import Domain.Message;
import Domain.Subforum;
import Domain.Thread;
import Domain.User;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class TextInterface {

    private final BulletinBoard board;

    public TextInterface(BulletinBoard board) throws SQLException {
        this.board = board;
    }

    private void listSubforums() throws SQLException {
        System.out.println("Alue\t\tViestejä yhteensä\tViimeisin Viesti");

        for (Subforum forum : board.getSubforums()) {
            Message lastMsg = forum.getLastMessage();

            System.out.print(forum.getName() + "\t" + forum.getPostcount());

            String date = "";
            if (lastMsg != null) {
                date = lastMsg.getDateTime().toString().substring(0, 16); //without ms
            }

            System.out.println("\t\t\t" + date);
        }
    }

    private void listThreads(int forumId) throws SQLException {
        System.out.println("Aihe\t\t\t\t\t\t\tViestejä\tViimeisin Viesti");

        for (Thread thread : board.getThreadsIn(forumId)) {
            Message lastMsg = board.findLastMessageForThread(thread.getThreadId());

            String date = "";
            if (lastMsg != null) {
                date = lastMsg.getDateTime().toString().substring(0, 16);
            }

            System.out.print(thread.getName() + "\t\t\t");
            System.out.print(thread.getPostcount() + "\t");
            System.out.println(date);
        }
    }

    private void listMessages(int threadId) throws SQLException {

    }

    private void listMessagesForUsers(int userId) throws SQLException {

    }

    private void listThreadsForUsers(int userId) throws SQLException {

    }

    private void addMessage() throws SQLException {

    }

    private void addThread() throws SQLException {

    }

    private void addSubforum() throws SQLException {

    }

    public void start() throws SQLException {
        listSubforums();

        List<Integer> list = Arrays.asList(1, 2, 3);
        for (Message message : board.getMessagesIn(list)) {
            System.out.println(message.getContent());
        }

        while (true) {
            //commands
            Scanner reader = new Scanner(System.in);

            if ("listThreads".equals("listThreads")) {
                listThreads(1);
                break;
            }
        }
    }
}
