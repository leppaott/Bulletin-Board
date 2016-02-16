package BulletinBoard;

import java.sql.*;

public class Main {

    public static void main(String[] args) throws Exception {
        Connection connection = DriverManager.getConnection("jdbc:sqlite:tietokanta.db");
        
        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.executeQuery("SELECT * FROM ALUE;");

        while (resultSet.next()) {
            System.out.println(resultSet.getString("nimi"));
        }
        
        statement.close();
        resultSet.close();
        connection.close();
    }

}
