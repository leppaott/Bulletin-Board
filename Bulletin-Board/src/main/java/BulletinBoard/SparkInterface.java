package BulletinBoard;

import Domain.Message;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import spark.ModelAndView;
import spark.Spark;
import static spark.Spark.get;
import static spark.Spark.post;
import spark.TemplateEngine;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import Domain.Thread;
import java.util.ArrayList;

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
            
            try {
                int forumId = Integer.parseInt(req.queryParams("id"));
                map.put("subforum", board.getSubforum(forumId).getName());

                List<Thread> threads = board.getThreadsIn(forumId);
                map.put("threads", threads);
                
                List<Integer> lastMsgs = new ArrayList<>();
                threads.forEach(t -> lastMsgs.add(((Thread)t).getLastMessage()));

                map.put("lastMessages", board.getMessagesIn(lastMsgs)); //useful to have Message for future
            } catch(NumberFormatException | SQLException e) {}

            return new ModelAndView(map, "subforum");
        }, templateEngine);
        
        get("/thread", (req, res) -> { // /thread?id=1
            HashMap map = new HashMap<>();
            int threadId = Integer.parseInt(req.queryParams("id"));
            map.put("thread", board.getThread(threadId));
            map.put("messages", board.getMessagesIn(threadId));
            
            return new ModelAndView(map, "thread");
        }, templateEngine);     
    }
}
