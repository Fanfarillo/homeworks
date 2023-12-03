package logic.query;

import logic.entity.Cane;
import logic.entity.Padrone;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

public class PadroneQuery {
    //costruttore di default privato perché si tratta di una classe con soli metodi statici
    private PadroneQuery(){}

    public static void insertPersonQuery(Statement stmt, Padrone person) throws SQLException {
        String sql;
        if(person.getAddr() != null) {
            sql = "INSERT INTO padrone VALUES('" + person.getCf() + "', '" + person.getAddr().getNome() + "')";
        }
        else {
            sql = "INSERT INTO padrone VALUES('" + person.getCf() + "', NULL)";
        }
        stmt.executeUpdate(sql);

    }

    public static void insertPossessionQuery(Statement stmt, Padrone person) throws SQLException {
        //definizione delle variabili necessarie
        Cane dog;
        String sql;

        if(person.getCani() != null) {  //se il padrone non ha cani, non bisogna inserire nulla nella tabella padronanza.
            Iterator<Cane> iter = person.getCani().iterator();

            //inserimento delle coppie <padrone, cane> nella tabella padronanza
            while (iter.hasNext()) {
                dog = iter.next();
                sql = "INSERT INTO padronanza VALUES('" + person.getCf() + "', '" + dog.getMatricola() + "')";
                stmt.executeUpdate(sql);

            }

        }

    }

    public static ResultSet selectPersonQuery(Statement stmt) throws SQLException {
        //restituzione di tutte le coppie <padrone, cane> con le relative informazioni
        String sql = "SELECT P.cf, I.nome, I.città, C.matricola, C.nome FROM padrone P LEFT JOIN indirizzo I ON P.indirizzo = I.nome LEFT JOIN padronanza PC ON P.cf = PC.padrone LEFT JOIN cane C ON PC.cane = C.matricola ORDER BY P.cf";
        return stmt.executeQuery(sql);

    }

}
