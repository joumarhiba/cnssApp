import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.sql.SQLException;
import java.text.ParseException;

public class MaCnss extends DbConnection {
    
    Scanner sc = new Scanner(System.in);
    
    public static void main(String[] args) throws ClassNotFoundException, SQLException, NoSuchAlgorithmException, ParseException {
        DbConnection con = new DbConnection();
        con.connection();
        new MaCnss().globalMenu();
    }
    
    
    public void globalMenu() throws SQLException, NoSuchAlgorithmException, ParseException {
        System.out.println();
        System.out.println(" ---------------------------------------------------------- ");
        System.out.println("|         1 . S'authentifier au tant qu'un admin           |");
        System.out.println("|         2 . S'authentifier au tant qu'un agent           |");
        System.out.println("|         3 . S'authentifier au tant qu'un patient         |");
        System.out.println(" ---------------------------------------------------------- ");
        System.out.println();
       
        Admin admin = new Admin();
        Agent agent = new Agent();
        System.out.print("Choisir une option : ");
        int option = sc.nextInt();
        switch(option){
            case 1: 
                admin.adminLogin();
                adminMenu();
                break;
            case 2:
                agent.agentLogin();
                agentMenu();
                break;
            case 3:
                patientMenu();
                break;
        }
        
    }
       
    public void adminMenu() throws SQLException, NoSuchAlgorithmException, ParseException{
         System.out.println(" ---------------------------------------- ");
         System.out.println("|         1 . Ajouter un Agent           |");
         System.out.println("|         2 . Supprimer un agent         |");
         System.out.println(" ---------------------------------------- ");
         System.out.print("Saisir votre choix  : ");
         int option  = sc.nextInt();
         
         Admin admin = new Admin();
         switch(option){
             case 1 -> admin.addAgent(); 
             case 2 -> admin.deleteAgent();
             default -> {
            }
         }
         globalMenu();
    }
    
    
    public void agentMenu() throws SQLException, NoSuchAlgorithmException, ParseException {
         System.out.println(" ------------------------------------------ ");
         System.out.println("|    1 . Ajouter un nouveau dossier        |");
         System.out.println("|    2 . Ajouter des documents             |");
         System.out.println("|    3 . Vérifier les dossiers des patients|");
         System.out.println(" ------------------------------------------ ");
         System.out.println();
         System.out.print("Saisir votre choix  : ");
         int option  = sc.nextInt();
         
         Agent agent = new Agent();
         switch(option){
             case 1 :  agent.setPatientFolder();
                        char rep;
                        do{
                            System.out.println("vous voulez ajouter des docs ?");
                            rep = sc.next().charAt(0);
                            if(rep == 'y'){
                            agent.setPatientFile();
                            }
                        }while(rep =='y');
                        break;
                        
             case 4 : 
                        char rep2;
                        do{
                            System.out.println("vous voulez ajouter des docs ?");
                            rep2 = sc.next().charAt(0);
                            if(rep2 == 'y'){
                            agent.setPatientFile();
                            }
                        }while(rep2 =='y' );
             
                        break;
             case 3 : agent.checking();
                      break;
             default : break;
         }
         globalMenu();
         
    }
    
    public void  patientMenu(){
        System.out.println(" ------------------------------------------ ");
         System.out.println("|               Partie des patients       |");
         System.out.println("|         1 . Voir votre dossiers         |");
         System.out.println(" ----------------------------------------- ");
         System.out.println();
    }

   
}




