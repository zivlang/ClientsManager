package com.example.clientsmanager.utils;

import android.content.Context;
import android.content.SharedPreferences;


public class SharedPrefsManager {

    private static SharedPrefsManager sharedPrefs;
    private SharedPreferences reader;
    private SharedPreferences.Editor writer;
    private static final String SP_FILE = "loginData";
    private static final String USER_NAME = "userName";
    private static final String PASSWORD = "password";

    public static SharedPrefsManager getInstance(Context context) { // singleton

        if (sharedPrefs == null) {
            sharedPrefs = new SharedPrefsManager(context);
        }
        return sharedPrefs;
    }
    // a constructor for a reader for saving and a writer for loading the saved data
    private SharedPrefsManager(Context context) {
        reader = context.getSharedPreferences(SP_FILE, Context.MODE_PRIVATE);
        writer = reader.edit();
    }
    //structure for saving a new username and password: (key name, value)
    public void saveUserName(String userName) {
        writer.putString(USER_NAME, userName); // (key,value)
        writer.commit();
    }

    public void savePassword(String password) {
        writer.putString(PASSWORD, password); // (key,value)
        writer.commit();
    }
    // loading saved username and data
    public String loadUserName() {
        //(key,default) -> (key with which a value is saved,value to return in case none is saved)
        String userName = reader.getString(USER_NAME, null);
         return userName;
    }

    public String loadPassword() {
        //(key,default) --> (key with which a value is saved,value to return in case none is saved)
        String password = reader.getString(PASSWORD, null);
        return password;
    }
}
