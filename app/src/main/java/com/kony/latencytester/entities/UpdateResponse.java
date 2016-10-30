package com.kony.latencytester.entities;

/**
 * Created by dnorvell on 10/30/16.
 */
public class UpdateResponse extends BaseEntity {

    public int totalRecords;
    public int updatedRecords;
    public String guid;
    public int opstatus;
    public int httpStatusCode;

}
