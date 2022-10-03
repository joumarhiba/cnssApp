package org.example;

import Database.MysqlConnection;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

import java.sql.ResultSet;
import java.util.Scanner;
import java.util.UUID;

public class Main extends PatientInfo {

    static String uniqueID = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    static MysqlConnection db = new MysqlConnection("root", "");
    static Scanner scanner = new Scanner(System.in);
    static Argon2PasswordEncoder encoder = new Argon2PasswordEncoder(32, 16, 1, 15 * 1024, 2);

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


        try {
            db.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (welcomeMenu()) {
            case 1 -> login();
            case 2 -> patientRegister();

//
        }


    }

    public static String validateEmail(String email) {
        boolean valid = EmailValidator.getInstance(true).isValid(email);
        return valid ? "Valid" : "Not Valid";
    }

    public static String hashPassword(String password) {
        return encoder.encode(password);
    }


    public static String getPassword(String password) {
        if (password != null && password.length() > 6) {
            return "Valid";
        } else {
            System.out.println("Please enter a valid password");
        }
        return "invalid";
    }

    public static void patientRegister() {

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

    public static void login() {
        String email;
        String password;
        String codeToSend;

        System.out.println("Welcome Again");
        System.out.println("Enter your email address: ");
        email = scanner.nextLine();
        System.out.println("Enter your password ");
        password = scanner.nextLine();

        try {
            ResultSet rs = db.getStatement().executeQuery("SELECT email, password FROM patient WHERE (email = '" + email + "')");
            if (!rs.next()) {
                System.out.println("no account found with email " + email);
            } else {
                boolean validPassword = encoder.matches(password, rs.getString("password"));
                if (!validPassword) {
                    System.out.println("Password does not match");
                } else {
                    SendEmail mail = new SendEmail();
                    String msg = mail.emailSender("za.tajer@gmail.com");

                    if (msg == null) {
                        System.out.println("error sending email");
                    } else {
                        System.out.println("Enter the code Received");
                        String codeReceived = scanner.nextLine();

                        if (msg.equals(codeReceived)) {
                            System.out.println("Welcome back " + email);
                        } else {
                            System.out.println("Code is incorrect");
                        }

                    }
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}