package com.kony.latencytester.entities;

/**
 * Created by dnorvell on 10/30/16.
 */
public class UpdateContactPayload {

    public String guid;
    public boolean onShip;

    public UpdateContactPayload(String _guid, boolean _onShip) {
        guid = _guid;
        onShip = _onShip;
    }

}
