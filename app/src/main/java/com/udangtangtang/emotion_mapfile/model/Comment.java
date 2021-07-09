package com.udangtangtang.emotion_mapfile.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Comment implements Serializable {
    private String comment;
    private Long create_at;
    private String district;
    private String status;

}
