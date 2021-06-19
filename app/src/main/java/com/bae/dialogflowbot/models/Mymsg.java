package com.bae.dialogflowbot.models;

public class Mymsg {
    private String mymessage;

    public Mymsg(Class<Mymsg> mymsgClass) {
    }

    public String getMymessage() {
        return mymessage;
    }

    public void setMymessage(String mymessage) {
        this.mymessage = mymessage;
    }

    public Mymsg(String mymessage) {
        this.mymessage = mymessage;
    }
}
