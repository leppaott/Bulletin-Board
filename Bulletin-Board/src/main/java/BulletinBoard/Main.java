package BulletinBoard;

import java.sql.SQLException;
import static spark.Spark.port;

public class Main {

    public static void main(String[] args) throws Exception {

//        new TextInterface(
//                new BulletinBoard(new Database("jdbc:sqlite:tietokanta.db"))
//        ).start();

        if (System.getenv("PORT") != null) {
            port(Integer.valueOf(System.getenv("PORT")));
        }
        
        new SparkInterface(
                new BulletinBoard(new Database("jdbc:sqlite:tietokanta.db"))
        ).start();
    }
}
