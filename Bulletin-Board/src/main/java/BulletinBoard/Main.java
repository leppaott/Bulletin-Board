package BulletinBoard;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException {

        new TextInterface(
                new BulletinBoard(new Database("jdbc:sqlite:tietokanta.db"))
        ).start();
        
//        new SparkInterface(
//                new BulletinBoard(new Database("jdbc:sqlite:tietokanta.db"))
//        ).start();
    }
}
