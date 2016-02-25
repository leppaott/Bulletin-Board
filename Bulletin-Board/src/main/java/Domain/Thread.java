package Domain;

import java.sql.Timestamp;

public class Thread {

    private final int threadId;
    private Subforum forum;
    private User sender;
    private Message lastMessage;
    private String name;
    private Timestamp dateTime;
    private int postcount;

    public Thread(int threadId, Subforum forum, String name, Timestamp dateTime, int postcount) {
        this(threadId, null, null, null, name, dateTime, postcount);
    }

    public Thread(int threadId, Subforum forum, User sender, Message lastMessage,
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
    
    /**
     * DEPRECATED
     * @return 
     */
    public Message getLastMessage() {
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

      /**
     * DEPRECATED
     * @param lastMessage
     */
    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }

    public void setForum(Subforum forum) {
        this.forum = forum;
    }
}
