package com.udangtangtang.emotion_mapfile.presenter;

import com.udangtangtang.emotion_mapfile.model.City;

public class CommentListPresenter {
    private final City city;

    public CommentListPresenter() {
        this.city = City.getInstance();
    }

    public String getMyCity(){
        return city.getMyCity();
    }


}
