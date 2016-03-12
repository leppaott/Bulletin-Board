package BulletinBoard;

public class Main {

    public static void main(String[] args) throws Exception {
        new SparkInterface(
                new BulletinBoard(new Database("jdbc:sqlite:tietokanta.db"))
        ).start();
    }
}
