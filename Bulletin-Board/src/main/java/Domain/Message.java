package Domain;

import java.sql.Timestamp;

public class Message {

    private final int messageId;
    private Thread threadId;
    private User sender;
    private final int order;
    private Timestamp dateTime;
    private String content;

    public Message(int messageId, int order, Timestamp dateTime, String content) {
        this.messageId = messageId;
        this.order = order;
        this.dateTime = dateTime;
        this.content = content;
    }

    public Message(int messageId, Thread threadId, User sender, int order, Timestamp dateTime, String content) {
        this.messageId = messageId;
        this.threadId = threadId;
        this.sender = sender;
        this.order = order;
        this.dateTime = dateTime;
        this.content = content;
    }

    public int getMessageId() {
        return messageId;
    }

    public Thread getThreadId() {
        return threadId;
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
        this.threadId = thread;
    }
    
    public void setSender(User user) {
        this.sender = user;
    }
}
