package Domain;

public class Subforum {

    private final int forumId;
    private String name;
    int postcount;
    private Message lastMessage;
    private String lastPostTime;

    public Subforum(int forumId, String name, int postcount) {
        this.forumId = forumId;
        this.name = name;
        this.postcount = postcount;
    }
    
    public int getForumId() {
        return forumId;
    }
    
    public String getName() {
        return name;
    }
    
    public int getPostcount() {
        return postcount;
    }
    
    public Message getLastMessage() {
        return lastMessage;
    }
    
    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }
    
    public String getLastPostTime() {
        if(lastMessage != null) {
            return lastMessage.getDateTime().toString();
        }
        return "";
    }
}
