package com.udangtangtang.emotion_mapfile.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Comment {
    private String comment;
    private String create_at;
    private String district;
    private Double latitude;
    private Double longitude;
    private String status;
}
