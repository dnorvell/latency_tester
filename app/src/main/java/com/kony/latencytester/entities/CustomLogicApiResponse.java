package com.kony.latencytester.entities;

/**
 * Created by dnorvell on 10/30/16.
 */
public class CustomLogicApiResponse extends BaseEntity {

//    public String[] data; //TODO ignoring for now because of serialization issues
    public int opstatus;
    public int httpStatusCode;

}
