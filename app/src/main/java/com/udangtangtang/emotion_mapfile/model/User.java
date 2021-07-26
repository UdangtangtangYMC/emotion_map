package com.udangtangtang.emotion_mapfile.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User implements Serializable {
    private static User singletonUser;
    private final String TAG = "User";
    private String ID;
    private String email;
    private String name;
    private String city;
    private String login_method;

    private User() {
    }

    public static User getInstance() {
        if (singletonUser == null) {
            singletonUser = new User();
        }
        return singletonUser;
    }

    public void setID(String ID) {
        this.ID = ID.replaceFirst("@.*", "");
    }
}
