package com.example.registropruebaservice.JPA;

public class LoginRequest {

    private String username;
    private String passworrd;

    public LoginRequest() {

    }

    public LoginRequest(String username, String password) {
        this.username = username;
        this.passworrd = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassworrd() {
        return passworrd;
    }

    public void setPassworrd(String passworrd) {
        this.passworrd = passworrd;
    }

}
