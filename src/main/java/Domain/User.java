package Domain;

import java.sql.Timestamp;

public class User {

    private final int userId;
    private final String username;
    private final Timestamp joinDate;
    private int postcount;

    public User(int userId, String username, Timestamp joinDate, int postcount) {
        this.userId = userId;
        this.username = username;
        this.joinDate = joinDate;
        this.postcount = postcount;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public Timestamp getJoinDate() {
        return joinDate;
    }

    public int getPostcount() {
        return postcount;
    }

    public String toString() {
        return joinDate.toString().substring(0, 16);
    }
}
