package BulletinBoard;

import Domain.Subforum;
import Domain.SubforumDao;
import Domain.ThreadDao;
import Domain.Thread;

public class Main {

    public static void main(String[] args) throws Exception {

        Database database = new Database("jdbc:sqlite:tietokanta.db");
        //database.setDebugMode(true);   

        SubforumDao subforums = new SubforumDao(database);

        for (Subforum forum : subforums.findAll()) {
            System.out.println(forum.getName());
        }

        System.out.println();
        
        ThreadDao threads = new ThreadDao(database);

        for (Thread thread : threads.findAllIn(subforums.findAll().get(0).getForumId())) {
            System.out.println(thread.getName());
        }

    }

}
