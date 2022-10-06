  import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
  
  
public class DbConnection {
  
 static Connection connection;
 
    public void connection() throws ClassNotFoundException, SQLException {
        String jdbcURL = "jdbc:mysql://localhost:3306/macnss" ;
        String username = "root" ;
        String dbPassword = "" ;
        try {
          connection = DriverManager.getConnection(jdbcURL, username, dbPassword) ;
            
           if ( connection != null ) {
               System.out.println("bIENVENUE DANS NOTRE SYSTEME DE LA CNSS ") ;
               // connection.close() ;
           }
        } catch(SQLException ex) {
           ex.printStackTrace() ;
            }
        
        
    }
}




