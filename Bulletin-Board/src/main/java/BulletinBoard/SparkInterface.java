package BulletinBoard;

import Domain.Subforum;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import spark.ModelAndView;
import static spark.Spark.get;
import static spark.Spark.post;
import spark.TemplateEngine;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import Domain.Thread;
import Domain.User;
import java.util.ArrayList;
import static spark.Spark.port;

public class SparkInterface {

    private final BulletinBoard board;
    private final TemplateEngine templateEngine;

    public SparkInterface(BulletinBoard board) {
        this.board = board;
        this.templateEngine = new ThymeleafTemplateEngine();
    }

    public void start() throws Exception {
        if (System.getenv("PORT") != null) {
            port(Integer.valueOf(System.getenv("PORT")));
        }
        
        board.createDb();
        
        get("/", (req, res) -> {    //http://localhost:4567/
            HashMap map = new HashMap<>();
            map.put("subforums", board.getSubforums());

            return new ModelAndView(map, "index");
        }, templateEngine);

        get("/subforum", (req, res) -> { // /subforum?id=1
            HashMap map = new HashMap<>();

            try {
                int forumId = Integer.parseInt(req.queryParams("id"));
                Subforum subforum = board.getSubforum(forumId);

                if (subforum == null) {
                    throw new SQLException();
                }

                List<Integer> lastMessageIds = new ArrayList<>();
                List<Thread> threads = board.getThreadsIn(forumId);
                threads.forEach(t -> lastMessageIds.add(((Thread) t).getLastMessage()));

                map.put("subforum", subforum);
                map.put("threads", threads);
                map.put("lastMessages",  board.getMessagesIn(lastMessageIds));
            } catch (NumberFormatException | SQLException e) {
                res.redirect("/");
            }

            return new ModelAndView(map, "subforum");
        }, templateEngine);

        get("/thread", (req, res) -> { // /thread?id=1
            HashMap map = new HashMap<>();

            try {
                int threadId = Integer.parseInt(req.queryParams("id"));
                Thread thread = board.getThread(threadId);

                if (thread == null) {
                    throw new SQLException();
                }

                int page;
                int pageCount = thread.getPageCount();
                try {
                    page = Math.min(pageCount, Integer.parseInt(req.queryParams("page")));
                    page = Math.max(page, 1);
                } catch (NumberFormatException e) {
                    page = 1;
                }

                int begin = page * 10 - 9;
                int end = begin + 9;

                map.put("page", page);
                map.put("pageCount", pageCount);
                map.put("thread", thread);
                map.put("messages", board.getMessagesIn(threadId, begin, end));
            } catch (NumberFormatException | SQLException e) {
                res.redirect("/");
            }

            return new ModelAndView(map, "thread");
        }, templateEngine);

        post("/thread", (req, res) -> {
            try {
                int threadId = Integer.parseInt(req.queryParams("id"));
                String username = req.queryParams("name");
                String comment = req.queryParams("comment");

                if (username.isEmpty() || comment.isEmpty()) {
                    res.redirect("/thread?id=" + threadId); //alert "Please fill username" etc
                    return null;
                }

                int userId = board.getUserId(username);
                if (userId == -1) {
                    userId = board.addUser(username);
                }

                board.addMessage(threadId, userId, comment);
                res.redirect("/thread?id=" + threadId);
            } catch (NumberFormatException | SQLException e) {
                res.redirect("/");
            }

            return null;
        });

        get("/addthread", (req, res) -> {   // /addthread
            HashMap map = new HashMap<>();

            try {
                int forumId = Integer.parseInt(req.queryParams("id"));
                Subforum forum = board.getSubforum(forumId);

                if (forum == null) {
                    throw new SQLException();
                }

                map.put("subforum", forum);
            } catch (NumberFormatException | SQLException e) {
                res.redirect("/");;
            }

            return new ModelAndView(map, "addthread");
        }, templateEngine);

        post("/addthread", (req, res) -> {
            try {
                int forumId = Integer.parseInt(req.queryParams("id"));
                String title = req.queryParams("title");
                String username = req.queryParams("username");
                String message = req.queryParams("message");

                if (title.isEmpty() || username.isEmpty() || message.isEmpty()) {
                    res.redirect("/subforum?id=" + forumId); //alert "Please fill username" etc
                    return null;
                }

                int userId = board.getUserId(username);
                if (userId == -1) {
                    userId = board.addUser(username);
                }

                int threadId = board.addThread(forumId, userId, title);
                board.addMessage(threadId, userId, message);
                res.redirect("/thread?id=" + threadId);
            } catch (NumberFormatException | SQLException e) {
                res.redirect("/");
            }

            return null;
        });

        get("/user", (req, res) -> {   // /user?id=1
            HashMap map = new HashMap<>();

            try {
                int userId = Integer.parseInt(req.queryParams("id"));
                User user = board.getUser(userId);

                if (user == null) {
                    throw new SQLException();
                }

                map.put("user", board.getUser(userId));
            } catch (NumberFormatException | SQLException e) {
                res.redirect("/");
            }

            return new ModelAndView(map, "user");
        }, templateEngine);

        get("/reset", (req, res) -> {   //resets db
            HashMap map = new HashMap<>();

            //if wanna reset db here...
            res.redirect("/");
            return new ModelAndView(map, "index");
        }, templateEngine);
    }
}
