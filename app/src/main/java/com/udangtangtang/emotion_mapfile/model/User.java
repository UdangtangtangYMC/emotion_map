package com.udangtangtang.emotion_mapfile.model;

import java.io.Serializable;

public class User implements Serializable {
    private String ID;
    private String name;
    private String city;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        ID = ID.replaceAll("@gmail.com", "");
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
