package jeeryweb.geocast.Models;

import android.util.Log;

public class ReliabilitiesRowRecord {


    public String sender,txt,time;

    public ReliabilitiesRowRecord(String sender, String txt, String time) {
        this.sender=sender;
        this.txt=txt;
        this.time=time;
        Log.e("contacts ::::: = ", sender);
    }
}
