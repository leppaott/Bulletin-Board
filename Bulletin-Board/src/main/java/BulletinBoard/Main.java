package BulletinBoard;

public class Main {
    
    public static void main(String[] args) throws Exception {
        new TextInterface(new Database("jdbc:sqlite:tietokanta.db")).start();
    }
}
