package Domain;

public class Subforum {

    private final int forumId;
    private String name;
    int postcount;

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
}