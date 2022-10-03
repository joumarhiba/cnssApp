package Interfaces;

public interface PatientInterface {
    String validateEmail(String email);
    String getPassword(String password);
    String hashPassword(String password);
    void patientRegister();
    String passwordDecoder(String password);

}
