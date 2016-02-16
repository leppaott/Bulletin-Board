
package Domain;

import BulletinBoard.Database;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class SubforumDao implements Dao<Subforum, Integer> {
    private Database database;
    
    public SubforumDao(Database database) {
        this.database = database;
    }
    
    @Override
    public Subforum findOne(Integer key) throws SQLException {
        List<String> rows =  database.queryAndCollect("SELECT * FROM Alue WHERE alueId = ?;", 
                rs -> rs.getString("nimi"), key);
        
        if (!rows.isEmpty()) {
            System.out.println("Alue " + rows.get(0));
        }
        return new Subforum();
    }

    @Override
    public List<Subforum> findAll() throws SQLException {  
        return database.queryAndCollect("SELECT * FROM Alue;", rs -> new Subforum());
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
