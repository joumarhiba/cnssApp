package org.example;

import Database.MysqlConnection;
import Interfaces.PatientInterface;
import api.HttpRequests;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.ResultSet;
import java.util.Scanner;
import java.util.UUID;

public class Main implements PatientInterface {

    static String uniqueID = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    static MysqlConnection db = new MysqlConnection("root", "");
    static Scanner scanner = new Scanner(System.in);
    static Argon2PasswordEncoder encoder = new Argon2PasswordEncoder(32, 16, 1, 15 * 1024, 2);
    static String[] uniqueIds = new String[1];

    public static int welcomeMenu() {
        Scanner scanner = new Scanner(System.in);

        int choice = 0;
        System.out.println("Welcome to CNSS");
        System.out.println("1: Login to you're CNSS account");
        System.out.println("2: New member create you're CNSS account");

        switch (scanner.nextInt()) {
            case 1 -> choice = 1;

            case 2 -> choice = 2;

        }
        System.out.println(choice);
        return choice;
    }


    public static void main(String[] args) {

        Main main = new Main();

        try {
            db.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (welcomeMenu()) {
            case 1 -> main.login();
            case 2 -> main.patientRegister();

//
        }


    }


    public String validateEmail(String email) {
        boolean valid = EmailValidator.getInstance(true).isValid(email);
        return valid ? "Valid" : "Not Valid";
    }

    public String hashPassword(String password) {
        return encoder.encode(password);
    }

    public String passwordDecoder(String password) {
        return "";
    }

    public String getPassword(String password) {
        if (password != null && password.length() > 6) {
            return "Valid";
        } else {
            System.out.println("Please enter a valid password");
        }
        return "invalid";
    }

    @Override
    public void patientRegister() {

        String email;
        String password;
        boolean emailExist = false;
        do {
            System.out.println("Enter you're email: ");
            email = scanner.nextLine();

            if (!validateEmail(email).equals("Valid")) {
                System.out.println("Please enter a valid email");
            } else {
                try {
                    ResultSet res = db.getStatement().executeQuery("Select email from Patient");

                    while (res.next()) {
                        if (res.getString("email").equals(email)) {
                            System.out.println("Email already exists");
                            emailExist = true;
                        } else {
                            emailExist = false;

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } while (!validateEmail(email).equals("Valid") && !emailExist);

        do {
            System.out.println("Enter you're password: ");
            password = scanner.nextLine();

        } while (!getPassword(password).equals("Valid"));

        try {
            int i = db.getStatement().executeUpdate("INSERT INTO patient (patientMatricule, email, password)" + "VALUES ('" + uniqueID + "', '" + email + "', '" + hashPassword(password) + "')");
            if (i == 1) {
                System.out.println("Welcome: " + email);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void login() {
        String email;
        String password;

        System.out.println("Welcome Again");
        do {
            System.out.println("Enter your email address: ");
            email = scanner.nextLine();

            if (!validateEmail(email).equals("Valid")) {
                System.out.println("Please enter a valid email address");
            }

        } while (!validateEmail(email).equals("Valid"));


        System.out.println("Enter your password ");
        password = scanner.nextLine();

        try {
            ResultSet rs = db.getStatement().executeQuery("SELECT id,email, password, patientMatricule FROM patient WHERE (email = '" + email + "')");
            if (!rs.next()) {
                System.out.println("no account found with email " + email);
            } else {
                boolean validPassword = encoder.matches(password, rs.getString("password"));
                if (!validPassword) {
                    System.out.println("Password does not match");
                } else {
                    uniqueIds[0] = rs.getString("id");

                    SendEmail mail = new SendEmail();
                    String msg = mail.emailSender("za.tajer@gmail.com");

                    if (msg == null) {
                        System.out.println("error sending email");
                    } else {
                        System.out.println("Enter the code Received");
                        int tryOuts = 0;
                        String codeReceived;
                        do {
                            codeReceived = scanner.nextLine();
                            if (msg.equals(codeReceived)) {
                                System.out.println("Welcome back " + email + uniqueIds[0]);
                                loginMenu();
                                return;
                            } else {
                                System.out.println("Code is incorrect try again");
                            }
                            if (tryOuts == 2) {
                                System.out.println("sorry, please try again later");
                                welcomeMenu();
                            }

                            tryOuts++;
                        } while (tryOuts != 2 || !msg.equals(codeReceived));


                    }
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void loginMenu() throws URISyntaxException, IOException, InterruptedException {
        System.out.println("1: Track the status of you're file");
        System.out.println("2: Deposit a file");
        System.out.println("1: Track the status of you're file");

        switch (scanner.nextInt()) {
            case 1 -> trackPatientFILEStatus();

            case 2 -> depositFile();


        }

    }

    public void trackPatientFILEStatus() {

        try {
            ResultSet rs = db.getStatement().executeQuery("SELECT * FROM `patient` JOIN file ON patient.patientMatricule = file.patientMatricule");
            while (rs.next()) {
                if (!rs.next()) {
                    System.out.println("No files found try to deposit a file");
                    System.out.println("1: return to menu");
                    int menuReturn = scanner.nextInt();
                    if (menuReturn == 1) {
                        loginMenu();
                    }
                    return;
                } else {
                    String patientMatricule = rs.getString("patientMatricule");
                    String status = rs.getString("status");
                    String FILEUniqueId = rs.getString("fileUniqueId");

                    System.out.println(patientMatricule + " " + status + " " + FILEUniqueId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public String depositFile() throws URISyntaxException, IOException, InterruptedException {

        Scanner scanners = new Scanner(System.in);
        String status = "onHold";
        String fileUniqueID = UUID.randomUUID().toString().replace("-", "").substring(0, 5);
        String doctor = null;
        int remboursment = 0;
        int fileId = 0;
        System.out.println("Choose the doctor type ");
        System.out.println("1: Specialists ");
        System.out.println("2: Generalists ");

        int doctorType = scanner.nextInt();
        switch (doctorType) {
            case 1 -> doctor = "Specialists";
            case 2 -> doctor = "Generalists";
        }
        System.out.println("Enter the price:");
        int price = scanner.nextInt();

        if ("Specialists".equals(doctor)) {
            remboursment = 120;
        } else {
            remboursment = 90;
        }


        System.out.println("1: if you have laboratory analyzes add them");
        System.out.println("2: if you have radios add them");
        System.out.println("3: if you have ordnance");
        System.out.println("4: if you have laboratory analyzes and radios or scanner");
        int analyzes = scanner.nextInt();
        String[] ordnanceTypes = new String[4];
        ordnanceTypes[0] = "laboratory analyzes";
        ordnanceTypes[1] = "radios";
        ordnanceTypes[2] = "ordnance";
        ordnanceTypes[3] = "laboratory analyzes and radios";


        System.out.println("Please enter medicine name: ");
        String medicine = scanner.nextLine();


        HttpRequests httpRequests = new HttpRequests();
        String getMedics = httpRequests.requests(medicine);
        int medicinePrice = 0;

//


        if (getMedics.equals("null")) {
            System.out.println("This medicine is not founded");
        } else {
            System.out.println("Please enter the price: ");
            scanners.next();
            medicinePrice = scanners.nextInt();
            if (analyzes <= 4) {
                try {
                    int query = db.getStatement().executeUpdate("INSERT INTO file (patientMatricule, status, fileUniqueId) VALUES ('" + uniqueIds[0] + "' , '" + status + "' , '" + fileUniqueID + "') ");
                    if (query == 1) System.out.println("You're file has been successfully deposited");

                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    ResultSet query = db.getStatement().executeQuery("SELECT * from `file` WHERE (patientMatricule = '" + uniqueIds[0] + "')");

                    while (query.next()) {
                        fileId = Integer.parseInt(query.getString("id"));
//                    fileId = id;
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

                String documentUniqueId = uniqueID;
                try {
                    int query = db.getStatement().executeUpdate("INSERT INTO patientdocs (documentUniqueId, price, doctorType, remboursment, ordonanceType, docId) VALUES ('" + documentUniqueId + "', '" + price + "', '" + doctor + "', '" + remboursment + "', '" + ordnanceTypes[analyzes - 1] + "', '" + fileId + "') ");
                    if (query == 1) {
                        System.out.println("Your file was successfully deposited");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                int patientdocsId = 0;
                try {
                    ResultSet query = db.getStatement().executeQuery("SELECT id from `patientdocs` WHERE (documentUniqueId = '" + documentUniqueId + "')");

                    while (query.next()) {
                        patientdocsId = Integer.parseInt(query.getString("id"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                int rembPercent = medicinePrice % 30;
                try {
                    int query = db.getStatement().executeUpdate("INSERT INTO `medicaments` (medics, remboursementMidcs, uniqueMidcsId, patientDocIds) VALUES ('" + medicine + "' , '" + rembPercent + "' , '" + uniqueID + "', '" + patientdocsId + "') ");
                    if (query == 1) System.out.println("You're file has been successfully deposited");

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

        }


        return "";
    }


}