package jeeryweb.geocast.Models;

import android.util.Log;

/**
 * Created by Jeery on 16-03-2018.
 */

public class InboxRowRecord {

    public String sender,txt,time;
    public String latti, longi;
    public String displayPic;

    public InboxRowRecord(String sender, String txt, String time ,String latti, String longi, String displayPic) {
        this.sender=sender;
        this.txt=txt;
        this.time=time;
        this.latti = latti;
        this.longi = longi;
        this.displayPic = displayPic;
        Log.e("contacts ::::: = ", txt);
    }
}
