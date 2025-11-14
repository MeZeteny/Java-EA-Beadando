// src/main/java/hu/nje/beadando/model/LoginRequest.java
package hu.nje.beadando.model;

public class LoginRequest {
    private String email;
    private String password;

    // Üres konstruktor (Thymeleaf-hoz kell)
    public LoginRequest() {}

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}