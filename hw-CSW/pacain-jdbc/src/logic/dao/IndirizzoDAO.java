package logic.dao;

import logic.entity.Indirizzo;
import logic.query.IndirizzoQuery;
import logic.utils.Connect;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class IndirizzoDAO {
    //attributes
    Connection conn = null;
    Statement stmt = null;
    private final String driverClassname = "com.mysql.jdbc.Driver";

    public void insertAddressDB(Indirizzo addr) {
        try {
            //loading dimanico del driver nel DBMS scelto
            Class.forName(this.driverClassname);
            //apertura della connessione verso il DBMS
            this.conn = Connect.getInstance().getDBConnection();
            //creazione ed esecuzione della query di inserimento
            this.stmt = this.conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            IndirizzoQuery.insertAddressQuery(this.stmt, addr);

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

    public List<Indirizzo> selectAddressDB() {
        //local variables
        String nome;
        String citta;
        ResultSet rs;
        List<Indirizzo> readAddresses = new ArrayList<>();

        try {
            //loading dimanico del driver nel DBMS scelto
            Class.forName(this.driverClassname);
            //apertura della connessione verso il DBMS
            this.conn = Connect.getInstance().getDBConnection();
            //creazione ed esecuzione della query di inserimento
            this.stmt = this.conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = IndirizzoQuery.selectAddressQuery(this.stmt);

            //scansiono i risultati
            rs.first();
            do {
                nome = rs.getString(1);
                citta = rs.getString(2);

                Indirizzo address = new Indirizzo(nome);
                address.setCitta(citta);
                readAddresses.add(address);

            } while(rs.next());

        } catch (SQLException se) {
            System.out.println("[WARNING] There is no address registered in the system.");

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

        return readAddresses;

    }

}
