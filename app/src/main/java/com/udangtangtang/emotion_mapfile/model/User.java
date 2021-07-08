package com.udangtangtang.emotion_mapfile.model;

import java.io.Serializable;

public class User implements Serializable {
    private String ID;
    private String name;
    private String city;
    private String login_method;

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

    public String getLogin_method() {
        return login_method;
    }

    public void setLogin_method(String login_method) {
        this.login_method = login_method;
    }
}
