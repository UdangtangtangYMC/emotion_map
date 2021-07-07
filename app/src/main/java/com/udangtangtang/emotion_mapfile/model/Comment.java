package com.udangtangtang.emotion_mapfile.model;

public class Comment {
    private String comment;
    private String create_at;
    private String district;
    private Double latitude;
    private Double logitude;
    private String status;

    public String getCreate_at() {
        return create_at;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setCreate_at(String create_at) {
        this.create_at = create_at;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLogitude() {
        return logitude;
    }

    public void setLogitude(Double logitude) {
        this.logitude = logitude;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
