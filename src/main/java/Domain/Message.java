package Domain;

import java.sql.Timestamp;

public class Message {

    private final int messageId;
    private Thread thread;
    private User sender;
    private final int order;
    private Timestamp dateTime;
    private String content;

    public Message(int messageId, Thread thread, User sender, int order, Timestamp dateTime, String content) {
        this.messageId = messageId;
        this.thread = thread;
        this.sender = sender;
        this.order = order;
        this.dateTime = dateTime;
        this.content = content;
    }

    public int getMessageId() {
        return messageId;
    }

    public Thread getThread() {
        return thread;
    }

    public User getSender() {
        return sender;
    }

    public int getOrder() {
        return order;
    }

    public Timestamp getDateTime() {
        return dateTime;
    }

    public String getContent() {
        return content;
    }
    
    public void setThread(Thread thread) {
        this.thread = thread;
    }
    
    public void setSender(User user) {
        this.sender = user;
    }
    
    public String toString() {
        return dateTime.toString().substring(0, 16);
    }
}
