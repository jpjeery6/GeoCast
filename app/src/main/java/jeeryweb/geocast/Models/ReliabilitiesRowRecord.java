package jeeryweb.geocast.Models;

import android.util.Log;

public class ReliabilitiesRowRecord {


    public String sender,picture;
    public int userID, type;
    public boolean relUpDown;

    public ReliabilitiesRowRecord(String sender, String picture ,int userID ,boolean relUpDown, int type) {
        this.sender=sender;
        this.relUpDown=relUpDown;
        this.picture=picture;
        this.userID = userID;
        this.type = type; //1=confirm , 2=cancel , 0 = pending
        Log.e("contacts ::::: = ", sender);
    }
}
