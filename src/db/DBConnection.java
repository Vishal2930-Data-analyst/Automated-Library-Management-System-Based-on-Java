package db;
import java.sql.*;

public class DBConnection {

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/library_pro_system",
                    "root",
                    "root"
            );

            System.out.println("Database Connected Successfully!");
            return con;

        } catch(Exception e){
            System.out.println("Database Connection Failed!");
            e.printStackTrace();
            return null;
        }
    }
}