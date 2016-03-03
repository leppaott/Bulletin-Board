package BulletinBoard;

import Domain.Subforum;
import java.util.HashMap;
import spark.ModelAndView;
import static spark.Spark.get;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

public class SparkInterface {
    private BulletinBoard board;

    public SparkInterface(BulletinBoard board) {
        this.board = board;
    }

    public void start() throws Exception {
        get("/sivu", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("subforums", board.getSubforums());

            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());
    }
}
