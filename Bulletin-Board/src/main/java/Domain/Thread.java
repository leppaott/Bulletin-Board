package Domain;

import java.sql.Timestamp;

public class Thread {

    private final int threadId;
    private Subforum forum;
    private User sender;
    private int lastMessage;
    private String name;
    private Timestamp dateTime;
    private int postcount;

    public Thread(int threadId, Subforum forum, User sender, int lastMessage,
            String name, Timestamp dateTime, int postcount) {
        this.threadId = threadId;
        this.forum = forum;
        this.sender = sender;
        this.lastMessage = lastMessage;
        this.name = name;
        this.dateTime = dateTime;
        this.postcount = postcount;
    }

    public int getThreadId() {
        return threadId;
    }

    public Subforum getForum() {
        return forum;
    }

    public User getSender() {
        return sender;
    }
    
    public int getLastMessage() {
        return lastMessage;
    } 

    public String getName() {
        return name;
    }

    public Timestamp getDateTime() {
        return dateTime;
    }
    
    public int getPostcount() {
        return postcount;
    }
    
    public void setSender(User sender) {
        this.sender = sender;
    }

    public void setForum(Subforum forum) {
        this.forum = forum;
    }
}
