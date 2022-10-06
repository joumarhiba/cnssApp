import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Agent extends DbConnection {
    Scanner sc = new Scanner(System.in);
    HashMap<String, String> loginInfo = new HashMap<>();
    
    String password, email;
    int idAgent;
    void agentLogin() throws SQLException, NoSuchAlgorithmException{
       System.out.print("Veuillez entrer votre email : ");
        String dbemail = sc.nextLine();
        System.out.print("Entrez votre mot de passe : ");
        String pwd = sc.nextLine(); 
        String query = "SELECT * FROM agent";
        PreparedStatement st = connection.prepareStatement(query);
        ResultSet row = st.executeQuery();
         while(row.next()){
             idAgent = row.getInt("idAgent");
             email = row.getString("email");
             password = row.getString("password");
             loginInfo.put(email,password);
             
                String encryptedPassword = loginInfo.get(email);
         
       if(encryptStringAgent(pwd).equals(encryptedPassword) ){
           System.out.println();
            System.out.println("successfully login!");
             System.out.println("****  Bienvenue dans votre espace , votre id est : "+idAgent+" ,veuillez le mémoriser  ****");
             System.out.println("Choisir une opération :");
             System.out.println();
            
        } else {
            System.out.println("erooor from agent Login");
        }
        }
                  
       

    }
    static String owner, dateV;
    void setPatientFolder() throws SQLException {

        System.out.println("************* La saisie des informations d'un patient (dossier) *****************");
        System.out.println("entrer votre id fournit lors d'authentification : ");
        int id = sc.nextInt();
        System.out.print("Entrer le Matricule du patient : ");
        int matricule = sc.nextInt();
        sc.nextLine();
        System.out.println("Le dossier est relatif à l'assurant / conjoint / enfant )");
        owner = sc.nextLine();
        // check if the patient exists in DB
        String query = "SELECT * FROM patient WHERE matricule="+matricule;
        Statement stmt = connection.createStatement();
        ResultSet res = stmt.executeQuery(query);
        if(res.next()){
            System.out.println("le patient "+matricule +" existe dans le système :) ");
        }else {
            System.out.println("le patient "+matricule +" n'existe pas dans le système :( ");
            System.out.println("On peut pas enregisté votre dossier ...");
        }
        //insert the folder informations
         System.out.println("Enter la date de la visite : ");
         dateV = sc.nextLine();
         sc.nextLine();
        String query2 = "INSERT INTO dossier (owner,matPatient,dateVisite, idAgent) VALUES (?,?,?,?)";
        PreparedStatement statement = connection.prepareStatement(query2);
        statement.setString(1, owner);
        statement.setInt(2, matricule);
        statement.setString(3, dateV);
        statement.setInt(4, id);

        int rows = statement.executeUpdate();
        if(rows > 0) {
            System.out.println("le dossier du "+ owner + " est ajouté avec succés par "+id);
        }
        
    }
    
    int remboursement, prix, dossierNm;
    String type, title;
    
    void setPatientFile() throws SQLException{
        System.out.println("************* La saisie des informations d'un patient (documents) *****************");
        String q = "SELECT * From dossier where owner= \""+owner+"\" and dateVisite= \" "+dateV+"\"";
        Statement s = connection.createStatement();
        ResultSet r = s.executeQuery(q);
        if(r.next()){
           dossierNm =r.getInt("idDossier");
           
        }
             System.out.println("Quel est le type du document ?");
             type = sc.nextLine();
             System.out.println("le titre du médecin (specialiste / generaliste)");
            title = sc.nextLine();
             System.out.println("Quel est le montant total du "+ type );
             prix = sc.nextInt();
             sc.nextLine();
             if(title.equalsIgnoreCase("specialiste")){
                 remboursement = 120;
             }
             else if(title.equalsIgnoreCase("generaliste")){
                 remboursement = 90;
             }
             
             
         
         String query3 = "INSERT INTO document (prix, title, remboursement, type, idDossier) Values (?,?,?,?,?)";
         PreparedStatement st = connection.prepareStatement(query3);
         st.setInt(1, prix);
         st.setString(2, title);
         st.setInt(3, remboursement);
         st.setString(4, type);
         st.setInt(5, dossierNm);
        int rows = st.executeUpdate();
                if(rows > 0) {
                    System.out.println(" Un document est bien enregistré dans le dossier "+dossierNm);
                    System.out.println();
                 }
       
    }
    
    
    
    int num, rembr, somme=0;
    String typeDc;
    HashMap<String, Integer> doctype = new HashMap<>();
    
    public void checkFolder() throws SQLException, ParseException{
        System.out.println("-----------------------------------------");
        System.out.println("Entrer le numéro du dossier à vérifier : ");
        num = sc.nextInt();
        //check if the folder exists or not
        String req = "SELECT idDossier FROM dossier WHERE idDossier="+num;
        Statement s = connection.createStatement();
        ResultSet res = s.executeQuery(req);
        if(res.next()){
            System.out.println("Le dossier existant , vous pouvez le vérifier ... ");
            String query = "SELECT * FROM document WHERE idDossier="+num;
            Statement s2 = connection.createStatement();
            ResultSet res2 = s2.executeQuery(query);
            while(res2.next()){
               typeDc = res2.getString("type");
               rembr = res2.getInt("remboursement");
               doctype.put(typeDc, rembr);
            }
            //check if type (ordonnance) exists in this folder or not 
            String q2 = query+" and type = 'ordonnance'";
            Statement s3 = connection.createStatement();
            ResultSet res3 = s3.executeQuery(q2);
            if(res3.next()){
                //VALIDE si le dossier contient l'ordonnace
                System.out.println("ce dossier contient une ordonnance");
                String updateQ = "UPDATE dossier SET status = ?" +"WHERE idDossier="+num;
                PreparedStatement ps = connection.prepareStatement(updateQ);
                ps.setString(1, "validé");
                int rows = ps.executeUpdate();
                if(rows > 0) {
                    // calculate the sum of rebrsm
                    String sumQ = "Select remboursement from document where idDossier="+num;
                    Statement sum = connection.createStatement();
                    ResultSet rslt = sum.executeQuery(sumQ);
                    while(rslt.next()){
                        int rbs = rslt.getInt("remboursement");
                        somme = somme+rbs;
                    }
                    System.out.println("Le document est validé et le montant rembourssé sera "+somme +" MAD");
                 }
            }else{
                //refuse si le dossier depasse 60 jrs
                String dateQ = "SELECT dateVisite FROM dossier WHERE idDossier="+num;
                Statement sm = connection.createStatement();
                ResultSet rs = sm.executeQuery(dateQ);
                while(rs.next()){
                    Timestamp dv = rs.getTimestamp("dateVisite");
                    LocalDate formatteddv= dv.toLocalDateTime().toLocalDate();
                    long daysBetween = ChronoUnit.DAYS.between(formatteddv, LocalDate.now());
                    if(daysBetween > 60){
                        String updateQ = "UPDATE dossier SET status = ?" +"WHERE idDossier="+num;
                        PreparedStatement ps = connection.prepareStatement(updateQ);
                        ps.setString(1, "refusé");
                        int rows = ps.executeUpdate();
                        if(rows > 0) {
                        // raison of the refuse
                        System.out.println("Le document est refusé car vous avez dépasser 60 jours dès la visite");
                 }
                    }else{
                        //REFUSE si le dossier manque l'ordonnance 
                String updateQ = "UPDATE dossier SET status = ?" +"WHERE idDossier="+num;
                PreparedStatement ps = connection.prepareStatement(updateQ);
                ps.setString(1, "refusé");
                int rows = ps.executeUpdate();
                if(rows > 0) {
                    // raison of the refuse
                    System.out.println("Le document est refusé à cause de manque d'une ordonnance , veuillez la rejoindre c'est obligatoire !!");
                 }
                    }
                }
                
            }
        }
    }
   
    
    
    
    public String encryptStringAgent(String input) throws NoSuchAlgorithmException{
     
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] messageDigest = md.digest(input.getBytes());
        BigInteger bigInt = new BigInteger(1, messageDigest);
        
        return bigInt.toString(16);
    }
    
    
    public void checking() throws SQLException, ParseException{
        String allQ = "SELECT idDossier, status FROM dossier where status='en-cours'"; 
        Statement sm2 = connection.createStatement();
        ResultSet rs2 = sm2.executeQuery(allQ);
        System.out.println("---- Voici les dossiers non-traités : -----");
        while(rs2.next()){
            int idD = rs2.getInt("idDossier");
            String status = rs2.getString("status");
            System.out.println("l'id du dossier : "+idD+" , son status : "+status);
        }
         System.out.println("choisir le numéro du dossier à vérifier : ");
        num = sc.nextInt();
        //check if the folder exists or not
        String req = "SELECT idDossier FROM dossier WHERE idDossier="+num;
        Statement s = connection.createStatement();
        ResultSet res = s.executeQuery(req);
        //if the folder exists.....
        if(res.next()){
            System.out.println("Le dossier existant , vous pouvez le vérifier ... ");
            String query = "SELECT * FROM document WHERE idDossier="+num;
            Statement s2 = connection.createStatement();
            ResultSet res2 = s2.executeQuery(query);
            while(res2.next()){
               typeDc = res2.getString("type");
               rembr = res2.getInt("remboursement");
               doctype.put(typeDc, rembr);
            }

                //refuse si le dossier depasse 60 jrs

               String dateQ = "SELECT dateVisite FROM dossier WHERE idDossier="+num;
                Statement sm = connection.createStatement();
                ResultSet rs = sm.executeQuery(dateQ);
                while(rs.next()){
                    Timestamp dv = rs.getTimestamp("dateVisite");
                    LocalDate formatteddv= dv.toLocalDateTime().toLocalDate();
                    long daysBetween = ChronoUnit.DAYS.between(formatteddv, LocalDate.now());
                    if(daysBetween > 60){
                        String updateQ = "UPDATE dossier SET status = ?" +"WHERE idDossier="+num;
                        PreparedStatement ps = connection.prepareStatement(updateQ);
                        ps.setString(1, "refusé");
                        int rows = ps.executeUpdate();
                        if(rows > 0) {
                        // raison of the refuse
                        System.out.println("Le document est refusé car vous avez dépasser 60 jours dès la visite");
                 }
                    }else {
                           //check if type (ordonnance) exists in this folder or not 
            String q2 = query+" and type = 'ordonnance'";
            Statement s3 = connection.createStatement();
            ResultSet res3 = s3.executeQuery(q2);
            if(res3.next()){
                //VALIDE si le dossier contient l'ordonnace
                System.out.println("ce dossier contient une ordonnance et non-expiré");
                String updateQ = "UPDATE dossier SET status = ?" +"WHERE idDossier="+num;
                PreparedStatement ps = connection.prepareStatement(updateQ);
                ps.setString(1, "validé");
                int rows = ps.executeUpdate();
                if(rows > 0) {
                    // calculate the sum of rebrsm
                    String sumQ = "Select remboursement from document where idDossier="+num;
                    Statement sum = connection.createStatement();
                    ResultSet rslt = sum.executeQuery(sumQ);
                    while(rslt.next()){
                        int rbs = rslt.getInt("remboursement");
                        somme = somme+rbs;
                    }
                    System.out.println("Le document est validé et le montant rembourssé sera "+somme +" MAD");
                 }
            }else {
                 //REFUSE si le dossier manque l'ordonnance 
                String updateQ = "UPDATE dossier SET status = ?" +"WHERE idDossier="+num;
                PreparedStatement ps = connection.prepareStatement(updateQ);
                ps.setString(1, "refusé");
                int rows = ps.executeUpdate();
                if(rows > 0) {
                    // raison of the refuse
                    System.out.println("Le document est refusé à cause de manque d'une ordonnance , veuillez la rejoindre c'est obligatoire !!");
                 }
            }
                    }
                }
                
                
                
             
    }
}
}
