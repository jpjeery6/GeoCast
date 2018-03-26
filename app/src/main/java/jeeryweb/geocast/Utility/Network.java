package jeeryweb.geocast.Utility;

/**
 * Created by Jeery on 21-02-2018.
 */

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Network {

    private String addr;

    private String user,pass=null;
    private String msg=null;
    private String latt,longi,fcmToken;
    private String IMEI = null,bio=null;
    private String postData;
    private String phone;
    private String image;

    private final String TAG=getClass().getSimpleName();


    public Network(String addr, String user, String pass, String msg, String latt, String longi,String fcmToken, String IMEI, String bio, String phone,String image){
        String f = "NA";

        this.addr = addr;
        this.user = (user!=null)?user:f;
        this.pass = pass!=null?pass:f;
        this.msg = msg!=null?msg:f;
        this.latt = latt!=null?latt:f;
        this.longi = longi!=null?longi:f;
        this.fcmToken=  fcmToken!=null?fcmToken:f;
        this.IMEI = IMEI!=null?IMEI:f;
        this.bio = bio!=null?bio:f;
        this.phone = phone!=null?phone:f;
        this.image = image!=null?image:f;
    }

    //Network job
    public String DoWork()
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
        String format = simpleDateFormat.format(new Date());
        String result=null;

        try {
            URL url = new URL(addr);

            HttpURLConnection conn = null;
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            OutputStream oStream = conn.getOutputStream();
            BufferedWriter bufW = new BufferedWriter(new OutputStreamWriter(oStream, "UTF-8"));

            //DEBUGGING............................
            //check which field is null
            if(latt==null)
                Log.e(TAG +" null= ","latt is null");
            if(longi==null)
                Log.e(TAG +" null= ","longi is null");
            if(format==null)
                Log.e(TAG +" null= ","format is null");
            if(user==null)
                Log.e(TAG +" null= ","user is null");
            if(pass==null)
                Log.e(TAG +" null= ","pass is null");
            if(msg==null)
                Log.e(TAG +" null= ","msg is null");
            if(fcmToken==null)
                Log.e(TAG +" null= ","fcmToken is null");
            if(IMEI==null)
                Log.e(TAG +" null= ","IMEI is null");
            if(phone==null)
                Log.e(TAG +" null= ","Phone is null");
            if(image==null)
                Log.e(TAG +" null= ","image is null");

            Log.e(addr,user + " "+pass );
            postData = URLEncoder.encode("Latitude", "UTF-8") + "=" + URLEncoder.encode(latt, "UTF-8") + "&"
                    + URLEncoder.encode("Longitude", "UTF-8") + "=" + URLEncoder.encode(longi, "UTF-8") +
                    "&" + URLEncoder.encode("Timestamp", "UTF-8") + "=" + URLEncoder.encode(format, "UTF-8") +
                    "&" + URLEncoder.encode("Username", "UTF-8") + "=" + URLEncoder.encode(user, "UTF-8") +
                    "&" + URLEncoder.encode("Password", "UTF-8") + "=" + URLEncoder.encode(pass, "UTF-8") +
                    "&" + URLEncoder.encode("Message", "UTF-8") + "=" + URLEncoder.encode(msg, "UTF-8") +
                    "&" + URLEncoder.encode("FCMtoken", "UTF-8") + "=" + URLEncoder.encode(fcmToken, "UTF-8")+
                    "&" + URLEncoder.encode("Bio", "UTF-8") + "=" + URLEncoder.encode(bio, "UTF-8")+
                    "&" + URLEncoder.encode("IMEI", "UTF-8") + "=" + URLEncoder.encode(IMEI, "UTF-8")+
                    "&" + URLEncoder.encode("Phone", "UTF-8") + "=" + URLEncoder.encode(phone, "UTF-8")+
                    "&" + URLEncoder.encode("Image", "UTF-8") + "=" + URLEncoder.encode(image, "UTF-8");

            bufW.write(postData);
            bufW.flush();
            bufW.close();
            InputStream is;
            String line = null;
            is = new BufferedInputStream(conn.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuffer sb = new StringBuffer();
            if (br != null) {
                while ((line = br.readLine()) != null) {
                    sb.append(line+"\n");
                }
            } else {
                //it is null
            }
            conn.disconnect();
            result = sb.toString();
            Log.e(TAG, result);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
