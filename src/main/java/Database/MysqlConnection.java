package Database;

import java.sql.*;


public class MysqlConnection {

    final String DB = "jdbc:mysql://localhost:3306/cnss";
    String username = null;
    String password = null;
    Connection conn = null;
    Statement statement = null;

    public MysqlConnection(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void connect() throws SQLException, Exception {
        System.out.println("Connecting....");
        conn = DriverManager.getConnection(DB, username, password);
        statement = conn.createStatement();
    }

    public void closeConnection() throws SQLException, Exception {
        statement.close();
        conn.close();
    }

    public Statement getStatement () throws SQLException, Exception {
        return statement;
    }

    public Connection getConnection () throws SQLException, Exception {
        return conn;
    }


}
