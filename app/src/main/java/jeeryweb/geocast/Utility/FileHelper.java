package jeeryweb.geocast.Utility;

/**
 * Created by Jeery on 21-02-2018.
 */

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class FileHelper {

    String filename = "GeoCastInbox";
    File file;

    public void createFile(Context context)
    {

        file = new File(context.getFilesDir(), filename);       //create file in internal storage
        Log.e("File Helper" ,"file created :" + filename);
    }


    public void writeFile(Context context,String sender, String body, String time, String latt,String longi)
    {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_APPEND | Context.MODE_PRIVATE));
            outputStreamWriter.write(sender + "% " + body + "% " + time + "% " + latt + " %" + longi +  "% \n");
            Log.e("File Helper" ,sender + "% " + body + "% " + time + "% " + latt + " %" + longi +  "% \n");
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("File Helper", "File write failed: " + e.toString());
        }
    }

    //overloaded function for pulling messages and writing in the file
    public void writeFile(Context context,String data)
    {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_APPEND | Context.MODE_PRIVATE));
            outputStreamWriter.write(data+ " \n");
            Log.e("File Helper" ,data + " \n");
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("File Helper", "File write failed: " + e.toString());
        }
    }

    public Boolean emptyFile(Context context)
    {
        if (readFile(context)=="") {
            // empty or doesn't exist
            return Boolean.TRUE;
        } else
            // exists and is not empty
            return Boolean.FALSE;
    }


    public String readFile(Context context)
    {
        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(filename);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString+"\n");
                    //here i am getting each line

                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("File Helper", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("File Helper", "Can not read file: " + e.toString());
        }

        return ret;
    }

    public void deleteFile(Context context)
    {
        context.deleteFile(filename);
    }

}
