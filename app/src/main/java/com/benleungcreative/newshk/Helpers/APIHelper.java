package com.benleungcreative.newshk.Helpers;

/**
 * Created by BenLeung on 17/3/2018.
 */

public class APIHelper {

    private static APIHelper instance;

    private APIHelper(){

    }

    public synchronized static APIHelper getInstance(){
        if(instance == null){
            instance = new APIHelper();
        }
        return instance;
    }

}
