package com.example.clientsmanager.model;

public class User {

    private String userId, userName, password, clientsListId;

    private static User currentUser; //for a static method that defines a current user for attaching
    // the user with its clients list

    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public User() {
    }

    public String getClientsListId() {
        return clientsListId;
    }

    public void setClientsListId(String clientsListId) {
        this.clientsListId = clientsListId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static void setCurrentUser(User currentUser) {
        User.currentUser = currentUser;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId:" + userId + '\'' +
                 "userName:'" + userName + '\'' +
                ", password:'" + password + '\'' +
                '}';
    }
}