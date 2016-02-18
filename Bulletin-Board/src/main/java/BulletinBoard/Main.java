package BulletinBoard;

import Domain.SubforumDao;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {

        Database database = new Database("jdbc:sqlite:tietokanta.db");
        //database.setDebugMode(true);
        
        List<String> subforums = database.queryAndCollect("SELECT * FROM Subforum;",
                rs -> rs.getString("name"));

        for (String subforum : subforums) {
            System.out.println(subforum);
        }
        
        System.out.println(new SubforumDao(database).findOne(1).getName());

    }

}
