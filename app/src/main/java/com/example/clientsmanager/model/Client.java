package com.example.clientsmanager.model;

public class Client {

    private String clientName, mobileNumber, email, eventsListId;
    private int eventsCount = 0;
    private static Client currentClient; // for a static method for attaching clients
    //with their respective events list

    public Client(int eventsCount, String clientName, String mobileNumber, String email) {

        this.eventsCount = eventsCount;
        this.clientName = clientName;
        this.mobileNumber = mobileNumber;
        this.email = email;
    }

    public Client(){}

    public Client(String newName, String newMobile, String newEmail) {
        this.clientName = newName;
        this.mobileNumber = newMobile;
        this.email = newEmail;
    }

    public static void setCurrentClient(Client currentClient) {
        Client.currentClient = currentClient;
    }

    public static Client getCurrentClient() {
        return currentClient;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

//    public int getEventsCount() {
//        return eventsCount;
//    }
//      public void setEventsCount(int eventsCount) {
//        this.eventsCount = eventsCount;
//    }

    public String getEventsListId() {
        return eventsListId;
    }

    public void setEventsListId(String eventsListId) {
        this.eventsListId = eventsListId;
    }

    @Override
    public String toString() {
        return "Client{" +
                "clientName='" + clientName + '\'' +
                ", mobileNumber='" + mobileNumber + '\'' +
                ", email='" + email + '\'' +
                ", eventsCount=" + eventsCount +
                ", eventsListId='" + eventsListId + '\'' +
                '}';
    }
}