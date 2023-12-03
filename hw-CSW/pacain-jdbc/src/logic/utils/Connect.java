package logic.utils;

import java.sql.Connection;
import java.sql.DriverManager;

public class Connect {  //singleton instance
    //attributes
    private static Connect instance = null;
    private Connection conn = null;

    //protected constructor
    protected Connect() {}

    public static synchronized Connect getInstance() {
        if(Connect.instance == null) {
            Connect.instance = new Connect();
        }
        return Connect.instance;

    }

    public synchronized Connection getDBConnection()  {

        if(this.conn == null) {
            try {
                String connectionString = "jdbc:mysql://localhost:3306/pacainDB?user=root&password=Kp*d.!>3&serverTimezone=UTC";
                this.conn = DriverManager.getConnection(connectionString);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return this.conn;

    }

}
