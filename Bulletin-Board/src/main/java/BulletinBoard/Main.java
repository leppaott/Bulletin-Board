package BulletinBoard;

import Domain.SubforumDao;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {

        Database database = new Database("jdbc:sqlite:tietokanta.db");
        List<String> subforums = database.queryAndCollect("SELECT * FROM ALUE",
                rs -> rs.getString("nimi"));

        for (String subforum : subforums) {
            System.out.println(subforum);
        }
        
        new SubforumDao(database).findOne(1);

    }

}
