package logic.query;

import logic.entity.Indirizzo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class IndirizzoQuery {
    //costruttore di default privato perché si tratta di una classe con soli metodi statici
    private IndirizzoQuery(){}

    public static void insertAddressQuery(Statement stmt, Indirizzo addr) throws SQLException {
        String sql = "INSERT INTO indirizzo VALUES('" + addr.getNome() + "', '" + addr.getCitta() + "')";
        stmt.executeUpdate(sql);

    }

    public static ResultSet selectAddressQuery(Statement stmt) throws SQLException {
        String sql = "SELECT * FROM indirizzo";
        return stmt.executeQuery(sql);

    }

}
