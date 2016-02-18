package Domain;

import BulletinBoard.Database;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class SubforumDao implements Dao<Subforum, Integer> {

    private final Database database;

    public SubforumDao(Database database) {
        this.database = database;
    }

    @Override
    public Subforum findOne(Integer key) throws SQLException {
        List<Subforum> row = database.queryAndCollect(
                "SELECT * FROM Subforum WHERE forumId = ?;", rs -> {
                    return new Subforum(
                            rs.getInt("forumId"),
                            rs.getString("name"),
                            rs.getInt("postcount")
                    );
                }, key);

        return row.get(0);
    }

    @Override
    public List<Subforum> findAll() throws SQLException {
        return database.queryAndCollect("SELECT * FROM Subforum;", rs -> {
            return new Subforum(
                    rs.getInt("forumId"),
                    rs.getString("name"),
                    rs.getInt("postcount"));
        });
    }

    @Override
    public List<Subforum> findAllIn(Collection<Integer> keys) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(Integer key) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
