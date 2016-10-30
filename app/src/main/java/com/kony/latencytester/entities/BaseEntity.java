package com.kony.latencytester.entities;

import com.google.gson.Gson;

/**
 * Created by dnorvell on 10/5/16.
 */
public class BaseEntity {

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}
