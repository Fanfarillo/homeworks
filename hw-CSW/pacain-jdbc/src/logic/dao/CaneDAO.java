package logic.dao;

import logic.entity.Cane;
import logic.query.CaneQuery;
import logic.utils.Connect;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CaneDAO {
    //attributes
    Connection conn = null;
    Statement stmt = null;
    private final String driverClassname = "com.mysql.jdbc.Driver";

    public void insertDogDB(Cane dog) {
        try {
            //loading dimanico del driver nel DBMS scelto
            Class.forName(this.driverClassname);
            //apertura della connessione verso il DBMS
            this.conn = Connect.getInstance().getDBConnection();
            //creazione ed esecuzione della query di inserimento
            this.stmt = this.conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            CaneQuery.insertDogQuery(this.stmt, dog);

        } catch(Exception e) {
            e.printStackTrace();

        } finally {
            try {
                if(stmt!=null) {
                    stmt.close();
                }
            }
            catch(Exception e1) {
                e1.printStackTrace();
            }

        }

    }

    public List<Cane> selectDogDB() {
        //local variables
        String matricola;
        String nome;
        ResultSet rs;
        List<Cane> readDogs = new ArrayList<>();

        try {
            //loading dimanico del driver nel DBMS scelto
            Class.forName(this.driverClassname);
            //apertura della connessione verso il DBMS
            this.conn = Connect.getInstance().getDBConnection();
            //creazione ed esecuzione della query di inserimento
            this.stmt = this.conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = CaneQuery.selectDogQuery(this.stmt);

            //scansiono i risultati
            rs.first();
            do {
                matricola = rs.getString(1);
                nome = rs.getString(2);

                Cane dog = new Cane(matricola);
                dog.setNome(nome);
                readDogs.add(dog);

            } while(rs.next());

        } catch (SQLException se) {
            System.out.println("[WARNING] There is no dog registered in the system.");

        } catch(Exception e) {
            e.printStackTrace();

        } finally {
            try {
                if(stmt!=null) {
                    stmt.close();
                }
            }
            catch(Exception e1) {
                e1.printStackTrace();
            }

        }

        return readDogs;

    }

}
