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
        System.out.println("Lähettäjä\t\tAika\t\t\tSisältö");
        
        for (Message message : board.getMessagesIn(threadId)) {
            
            String sender = "null";
            if(message.getSender() != null) {
                sender = message.getSender().getUsername();
            }            
            String date = message.getDateTime().toString().substring(0, 16);
            
            System.out.print(sender +"\t\t\t");
            System.out.print(date);
            System.out.println("\t" + message.getContent());
        }
    }

    private void listMessagesForUsers(int userId) throws SQLException {
        System.out.println("Lähettäjä\t\tAika\t\t\tSisältö");
        
        for (Message message : board.getMessages()) {
            if(message.getSender() != null) {
                if(message.getSender().getUserId() == userId) {
                    String date = message.getDateTime().toString().substring(0, 16);
                    
                    System.out.println(message.getSender().getUsername() + "\t\t\t");
                    System.out.println(date);
                    System.out.println("\t" + message.getContent());
                }
            }
            
        }
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
    
        /*
        listSubforums();

        List<Integer> list = Arrays.asList(1, 2, 3);
        for (Message message : board.getMessagesIn(list)) {
            System.out.println(message.getContent());
        }
         
         */       
                
        while (true) {
            //commands
            Scanner reader = new Scanner(System.in);
            listSubforums();
            System.out.println("[1] Tarkastele aluetta");
            System.out.println("[2] Hae käyttäjän perusteella");
            System.out.println("[3] Lisää subforum");
            System.out.println("[x] Lopeta");
            String command = reader.nextLine();
            
            if(command.equals("1")) {
                System.out.print("Forumid: ");
                int forumid = Integer.parseInt(reader.nextLine());
                listThreads(forumid);
                
                System.out.println("[1] Tarkastele aihetta");
                System.out.println("[2] Lisää aihe");
                String command2 = reader.nextLine();
                
                if(command2.equals("1")) {
                    System.out.print("Threadid: ");
                    int threadid = Integer.parseInt(reader.nextLine());
                    listMessages(threadid);
                    
                    System.out.println("[1] Lisää viesti");
                    String command4 = reader.nextLine();
                    if(command4.equals("1")) {
                        addMessage();
                    }     
                }
                if(command2.equals("2")) {
                    addThread();
                }
            }
            
            if(command.equals("2")) {
                System.out.println("[1] Viestin perusteella");
                System.out.println("[2] Aiheen perusteella");
                String command3 = reader.nextLine();
                System.out.print("Userid: ");
                int userid = Integer.parseInt(reader.nextLine());
                
                if(command3.equals("1")) {
                    listMessagesForUsers(userid);
                }
                if(command3.equals("2")) {
                    listThreadsForUsers(userid);
                }
            }
            
            if(command.equals("3")) {
                addSubforum();
            }
            
            if(command.equals("x")) {
                break;
            }

        }
    }
}
