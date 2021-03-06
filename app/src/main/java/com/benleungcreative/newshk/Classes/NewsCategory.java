package com.benleungcreative.newshk.Classes;

/**
 * Created by BenLeung on 17/3/2018.
 */

public enum NewsCategory {

    OFFLINE_NEWS, TOP_CATEGORY, BUSINESS, ENTERTAINMENT, HEALTH, SCIENCE, SPORTS, TECHNOLOGY;


    public String toAPIKey(){
        switch (this){
            case OFFLINE_NEWS:
                return "";
            case BUSINESS:
                return "business";
            case ENTERTAINMENT:
                return "entertainment";
            case HEALTH:
                return "health";
            case SCIENCE:
                return "science";
            case SPORTS:
                return "sports";
            case TECHNOLOGY:
                return "technology";
            default:
                return "";
        }
    }


}
