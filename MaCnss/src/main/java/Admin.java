import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.sql.SQLException; 
import java.sql.Statement;


public class Admin extends DbConnection {
    Scanner sc = new Scanner(System.in);
    Random rd = new Random();
    static String email, password, encryptedPwd;
    static int checkcode;
    
    void addAgent() throws SQLException, NoSuchAlgorithmException {
        System.out.println("************ L'ajout d'un nouveau agent *****************");
        System.out.println("entrer l'email : ");
        email = sc.nextLine();
        System.out.println("entrer le mot de passe : ");
        password = sc.nextLine();
        encryptedPwd = encryptString(password);
        checkcode = (rd.nextInt(98)+1)*100;
        
        String query = "INSERT INTO agent (email, password, checkingCode) VALUES (?,?,?)";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, email);
        statement.setString(2, encryptedPwd);
        statement.setInt(3, checkcode);

        int rows = statement.executeUpdate();
        if(rows > 0) {
            System.out.println("l'agent est ajouté avec succés");
        }
    }
    
    void deleteAgent() throws SQLException {
        System.out.println("************ La supression d'agent *****************");
        System.out.println("Voici les agents existants dans notre système : ");
        //get all agent 
        String q = "SELECT idAgent,email FROM agent";
        Statement stmt = connection.createStatement();
        ResultSet result = stmt.executeQuery(q);
        
        while(result.next()){
            int id = result.getInt("idAgent");
            String agent = result.getString("email");
            System.out.println(id + " : "+agent);
        }
        //delete an agent
        System.out.print("Entrer l'email d'agent que vous voulez supprimer : ");
        email = sc.nextLine();
        String query = "DELETE FROM agent WHERE email=?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1,email);
        int row = statement.executeUpdate();
        if(row > 0) {
            System.out.println("l'agent ("+email+") est supprimé avec succés !!");
        }
    }
    
    
    public void adminLogin() throws SQLException {
        System.out.print("Veuillez entrer votre username : ");
        String username = sc.nextLine();
        sc.nextLine();
        System.out.print("Entrez votre mot de passe : ");
        String pwd = sc.nextLine();
        String query = "SELECT * FROM admin where username=? and password=?";
        PreparedStatement st = connection.prepareStatement(query);
        st.setString(1,username);
        st.setString(2, pwd);
        ResultSet row = st.executeQuery();
        if(row.next()) {
            System.out.println("****  Bienvenue dans votre espace d'admin ****");
        }else {
            System.out.println("les cordonnés sont incorrectes ");
        }
               
    }
    
    public String encryptString(String input) throws NoSuchAlgorithmException{
     
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] messageDigest = md.digest(input.getBytes());
        BigInteger bigInt = new BigInteger(1, messageDigest);
        
        return bigInt.toString(16);
    }
}
