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
import Domain.User;
import java.util.ArrayList;

public class SparkInterface {

    private final BulletinBoard board;
    private final TemplateEngine templateEngine;

    public SparkInterface(BulletinBoard board) {
        this.board = board;
        this.templateEngine = new ThymeleafTemplateEngine();
    }

    public void start() throws Exception {
        System.out.println("messageId " + board.addMessage(3, 2, "asd"));
        //exception^^
        
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
                threads.forEach(t -> lastMsgs.add(((Thread) t).getLastMessage()));

                map.put("lastMessages", board.getMessagesIn(lastMsgs)); //useful to have Message for future
            } catch (NumberFormatException | SQLException e) {
                res.status(404);
            }

            return new ModelAndView(map, "subforum");
        }, templateEngine);

        get("/thread", (req, res) -> { // /thread?id=1
            HashMap map = new HashMap<>();

            try {
                int threadId = Integer.parseInt(req.queryParams("id"));
                map.put("thread", board.getThread(threadId));
                map.put("messages", board.getMessagesIn(threadId));
            } catch (NumberFormatException | SQLException e) {
                res.status(404);
            }

            return new ModelAndView(map, "thread");
        }, templateEngine);

        post("/thread", (req, res) -> {
            try {
                int threadId = Integer.parseInt(req.queryParams("id"));
                String username = req.queryParams("name");
                String comment = req.queryParams("comment");

                int userId = board.getUserId(username);
                if (userId == -1) {
                    userId = board.addUser(username);
                }
                
                board.addMessage(threadId, userId, comment);
                res.redirect("/thread?id=1");
            } catch (NumberFormatException | SQLException e) {
                res.status(404);
            }
            
            return null;
        });
        
        get("/addthread", (req, res) -> {   // /addthread
            HashMap map = new HashMap<>();
            return new ModelAndView(map, "addthread");
        }, templateEngine); 
        
        post("/addthread", (req, res)  -> {          
            int forumId = Integer.parseInt(req.queryParams("id"));
            String title = req.queryParams("title");
            String message = req.queryParams("message");
            
            board.addThread(forumId, 0, title);
            return null;
        });
    }
}
