package Dao;

import BulletinBoard.Database;
import Domain.Subforum;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SubforumDao {

    private final Database database;

    public SubforumDao(Database database) {
        this.database = database;
    }

    public Subforum findOne(int forumId) throws SQLException {
        List<Subforum> row = database.queryAndCollect(
                "SELECT * FROM Subforum WHERE forumId = ?;", rs -> {
                    return new Subforum(
                            rs.getInt("forumId"),
                            rs.getString("name"),
                            rs.getInt("postcount")
                    );
                }, forumId);

        return !row.isEmpty() ? row.get(0) : null;
    }

    public List<Subforum> findAll() throws SQLException {
        return database.queryAndCollect("SELECT * FROM Subforum;", rs -> {
            return new Subforum(
                    rs.getInt("forumId"),
                    rs.getString("name"),
                    rs.getInt("postcount"));
        });
    }

    public List<Subforum> findAllIn(Collection<Integer> keys) throws SQLException {
        List<Subforum> forums = new ArrayList<>();

        StringBuilder params = new StringBuilder("?");
        for (int i = 1; i < keys.size(); i++) {
            params.append(", ?");
        }

        try (ResultSet rs = database.query("SELECT * FROM Subforum WHERE forumId IN ("
                + params + ");", keys)) {
            while (rs.next()) {
                forums.add(new Subforum(rs.getInt("forumId"),
                        rs.getString("name"),
                        rs.getInt("postcount")));
            }
        }
        return forums;
    }
}
