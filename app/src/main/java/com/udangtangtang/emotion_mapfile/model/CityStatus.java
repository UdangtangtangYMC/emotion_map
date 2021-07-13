package com.udangtangtang.emotion_mapfile.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CityStatus implements Serializable {
    private String name;
    private int angry_count;
    private int happy_count;
    private int total;
    private int ratio;
}
