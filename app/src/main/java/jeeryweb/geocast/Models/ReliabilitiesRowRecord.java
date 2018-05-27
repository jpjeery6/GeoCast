package jeeryweb.geocast.Models;

import android.util.Log;

public class ReliabilitiesRowRecord {


    public String sender,picture;
    public int userID, type;

    public ReliabilitiesRowRecord(String sender, String picture ,int userID , int type) {
        this.sender=sender;
        this.picture=picture;
        this.userID = userID;
        this.type = type; //1=confirm , 2=cancel , 0 = pending
        Log.e("contacts ::::: = ", sender);
    }
}
