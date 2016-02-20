package Domain;

import java.sql.Timestamp;

public class Message {

    private final int messageId;
    private Thread threadId;
    private User sender;
    private int order;
    private Timestamp dateTime;
    private String content;

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
}
