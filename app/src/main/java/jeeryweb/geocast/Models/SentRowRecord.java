package jeeryweb.geocast.Models;

import android.util.Log;
/**
 * Created by Jeery on 16-03-2018.
 */

public class SentRowRecord{

   public String message,time;
   public int msgNo;

    public SentRowRecord(String message, String time, int msgNo) {
        this.message  = message;
        this.time = time;
        this.msgNo = msgNo;
        Log.e("sent ::::: = ", message);
    }
}