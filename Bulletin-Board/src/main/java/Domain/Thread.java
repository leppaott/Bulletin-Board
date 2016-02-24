package Domain;

import java.sql.Timestamp;

public class Thread {

    private final int threadId;
    private Subforum forum;
    private User sender;
    private Message lastMessage;
    private String name;
    private Timestamp dateTime;

    public Thread(int threadId, Subforum forum, String name, Timestamp dateTime) {
        this(threadId, null, null, null, name, dateTime);
    }
    
    public Thread(int threadId, Subforum forum, User sender, Message lastMessage,
            String name, Timestamp dateTime) {
        this.threadId = threadId;
        this.forum = forum;
        this.sender = sender;
        this.lastMessage = lastMessage;
        this.name = name;
        this.dateTime = dateTime;
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

    public Message getLastMessage() {
        return lastMessage;
    }

    public String getName() {
        return name;
    }

    public Timestamp getDateTime() {
        return dateTime;
    }
    
    public void setSender(User sender) {
        this.sender = sender;
    }
    
    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }
    
    public void setForum(Subforum forum) {
        this.forum = forum;
    }
}
