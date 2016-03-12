package BulletinBoard;

import java.sql.SQLException;
import static spark.Spark.port;

public class Main {

    public static void main(String[] args) throws Exception {

//        new TextInterface(
//                new BulletinBoard(new Database("jdbc:sqlite:tietokanta.db"))
//        ).start();

        
        new SparkInterface(
                new BulletinBoard(new Database("jdbc:sqlite:tietokanta.db"))
        ).start();
    }
}
