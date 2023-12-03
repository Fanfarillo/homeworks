package logic.dao;

import logic.entity.Cane;
import logic.entity.Indirizzo;
import logic.entity.Padrone;
import logic.query.PadroneQuery;
import logic.utils.Connect;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PadroneDAO {
    //attributes
    Connection conn = null;
    Statement stmt = null;
    private final String driverClassname = "com.mysql.jdbc.Driver";

    public void insertPersonDB(Padrone person) {
        try {
            //loading dimanico del driver nel DBMS scelto
            Class.forName(this.driverClassname);
            //apertura della connessione verso il DBMS
            this.conn = Connect.getInstance().getDBConnection();
            //creazione ed esecuzione della query di inserimento
            this.stmt = this.conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            PadroneQuery.insertPersonQuery(this.stmt, person);
            PadroneQuery.insertPossessionQuery(this.stmt, person);

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

    public List<Padrone> selectPersonDB() {
        //local variables
        String cf;
        String indirizzo;
        String citta;
        String matricola;
        String nomeCane;
        String oldCf = null;   //serve per verificare se è stato estratto un nuovo padrone dal result set oppure no
        ResultSet rs;
        Padrone currPerson = null;
        List<Padrone> readPeople = new ArrayList<>();

        try {
            //loading dimanico del driver nel DBMS scelto
            Class.forName(this.driverClassname);
            //apertura della connessione verso il DBMS
            this.conn = Connect.getInstance().getDBConnection();
            //creazione ed esecuzione della query di inserimento
            this.stmt = this.conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = PadroneQuery.selectPersonQuery(this.stmt);

            //scansiono i risultati
            rs.first();
            do {
                cf = rs.getString(1);

                if(!cf.equals(oldCf)) { //caso in cui stiamo processando una persona nuova
                    if(currPerson != null) {
                        readPeople.add(currPerson); //il padrone precedente è stato inizializzato del tutto: può essere aggiunto alla lista.
                    }

                    indirizzo = rs.getString(2);
                    citta = rs.getString(3);

                    currPerson = new Padrone(cf);
                    currPerson.setCani(new ArrayList<>());

                    if(indirizzo != null) { //caso in cui al padrone è associato un indirizzo
                        Indirizzo addr = new Indirizzo(indirizzo);
                        addr.setCitta(citta);
                        currPerson.setAddr(addr);

                    }

                }

                matricola = rs.getString(4);
                nomeCane = rs.getString(5);

                if(matricola != null) { //caso in cui al padrone è associato (almeno) un cane
                    Cane dog = new Cane(matricola);
                    dog.setNome(nomeCane);
                    currPerson.addCane(dog);

                }
                oldCf = cf;

            } while(rs.next());

            readPeople.add(currPerson); //l'ultimo padrone è stato inizializzato del tutto: può essere aggiunto alla lista.

        } catch (SQLException se) {
            System.out.println("[WARNING] There is no person registered in the system.");
            //se.printStackTrace();   //TODO

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

        return readPeople;

    }

}
