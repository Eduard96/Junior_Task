package com.junior_task.reg_login.server_client_interaction.request;


import javax.validation.constraints.NotBlank;

public class LoginRequestInfo {
    @NotBlank
    private String userName;

    @NotBlank
    private String password;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        String temp = password;
        password = null;
        return temp;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
