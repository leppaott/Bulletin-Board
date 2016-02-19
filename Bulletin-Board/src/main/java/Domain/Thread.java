package Domain;

import java.sql.Timestamp;

public class Thread {

    private final int threadId;
    private final int forumId;
    private User sender;
    private Message lastMessage;
    private String name;
    private Timestamp dateTime;

    public Thread(int threadId, int forumId, User sender, Message lastMessage,
            String name, Timestamp dateTime) {
        this.threadId = threadId;
        this.forumId = forumId;
        this.sender = sender;
        this.lastMessage = lastMessage;
        this.name = name;
        this.dateTime = dateTime;
    }

    public int getThreadId() {
        return threadId;
    }

    public int getForumId() {
        return forumId;
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

}
