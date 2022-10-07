package Interfaces;

import java.io.IOException;
import java.net.URISyntaxException;

public interface PatientInterface {
    String validateEmail(String email);
    String getPassword(String password);
    String hashPassword(String password);
    void patientRegister();
    String passwordDecoder(String password);
    void loginMenu() throws URISyntaxException, IOException, InterruptedException;

    void trackPatientFILEStatus();
    String depositFile() throws URISyntaxException, IOException, InterruptedException;

}
