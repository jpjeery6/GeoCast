package jeeryweb.geocast.Models;

import android.util.Log;

import jeeryweb.geocast.Activities.Inbox;

/**
 * Created by Jeery on 16-03-2018.
 */

public class InboxRowRecord {

    public String sender,txt,time;

    public InboxRowRecord(String sender, String txt, String time) {
        this.sender=sender;
        this.txt=txt;
        this.time=time;
        Log.e("contacts ::::: = ", txt);
    }
}
