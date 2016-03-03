package BulletinBoard;

import java.util.HashMap;
import spark.ModelAndView;
import static spark.Spark.get;
import spark.TemplateEngine;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

public class SparkInterface {
    private final BulletinBoard board;
    private final TemplateEngine templateEngine;
    
    public SparkInterface(BulletinBoard board) {
        this.board = board;
        this.templateEngine = new ThymeleafTemplateEngine();
    }

    public void start() throws Exception {
        get("/", (req, res) -> {    //http://localhost:4567/
            HashMap map = new HashMap<>();
            map.put("subforums", board.getSubforums());

            return new ModelAndView(map, "index");
        }, templateEngine);
        
        get("/subforum", (req, res) -> { // /subforum?id=1
            HashMap map = new HashMap<>();
            int forumId = Integer.parseInt(req.queryParams("id"));
            map.put("subforum", board.getSubforum(forumId).getName());
            map.put("threads", board.getThreadsIn(forumId));

            return new ModelAndView(map, "subforum");
        }, templateEngine);
        
        get("/thread", (req, res) -> { // /thread?id=1
            HashMap map = new HashMap<>();
            int threadId = Integer.parseInt(req.queryParams("id"));
            map.put("thread", board.getThread(threadId).getName());
            map.put("messages", board.getMessagesIn(threadId));
            
            return new ModelAndView(map, "thread");
        }, templateEngine);
    }
}
