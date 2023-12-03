package logic.query;

import logic.entity.Cane;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CaneQuery {
    //costruttore di default privato perché si tratta di una classe con soli metodi statici
    private CaneQuery(){}

    public static void insertDogQuery(Statement stmt, Cane dog) throws SQLException {
        String sql = "INSERT INTO cane VALUES('" + dog.getMatricola() + "', '" + dog.getNome() + "')";
        stmt.executeUpdate(sql);

    }

    public static ResultSet selectDogQuery(Statement stmt) throws SQLException {
        String sql = "SELECT * FROM cane";
        return stmt.executeQuery(sql);

    }

}
