package com.udangtangtang.emotion_mapfile.model;

import java.io.Serializable;

public class User implements Serializable {
    private String UserID;
    private String City;

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }
}
