package Dao;

import BulletinBoard.Database;
import Domain.Subforum;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class SubforumDao {

    private final Database database;

    public SubforumDao(Database database) {
        this.database = database;
    }
    
    public boolean addSubforum(String name) throws SQLException {
        int changes = database.update("INSERT INTO Subforum (name, postcount) VALUES(?, 0);", name);
        
        return changes != 0;
    }

    public Subforum findOne(int forumId) throws SQLException {
        List<Subforum> row = database.queryAndCollect(
                "SELECT * FROM Subforum WHERE forumId=?;", rs -> {
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
        List<Subforum> forums = database.queryAndCollect("SELECT * FROM Subforum WHERE forumId IN ("
                + database.getListPlaceholder(keys.size()) + ");", rs -> {
                    return new Subforum(
                            rs.getInt("forumId"),
                            rs.getString("name"),
                            rs.getInt("postcount")
                    );
                }, keys);

        return forums;
    }
}
