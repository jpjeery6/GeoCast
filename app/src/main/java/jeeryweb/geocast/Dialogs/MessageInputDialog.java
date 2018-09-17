package jeeryweb.geocast.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.clans.fab.FloatingActionMenu;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jeeryweb.geocast.Activities.Home;
import jeeryweb.geocast.Activities.Settings;
import jeeryweb.geocast.Constants.APIEndPoint;
import jeeryweb.geocast.R;
import jeeryweb.geocast.Utility.HomeActivityUtil;
import jeeryweb.geocast.Utility.SharedPrefHandler;

/**
 * Created by Jeery on 18-03-2018.
 */

public class MessageInputDialog extends DialogFragment {


    FloatingActionMenu floatingActionMenu;
    EditText customMessageBody;
    public static String message_body;
    private RequestQueue requestQueue;
    private Settings geoCastSettings;
    Context con;

    SharedPrefHandler sharedPrefHandler;
    HomeActivityUtil homeActivityUtil;   //conatins methods for managing live users

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
    public void passFloatMenu(FloatingActionMenu floatingActionMenu)
    {
        this.floatingActionMenu= floatingActionMenu;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        geoCastSettings = new Settings();
        LayoutInflater inflater = getActivity().getLayoutInflater();
        con = getActivity().getApplicationContext();
        sharedPrefHandler = new SharedPrefHandler(con);
        homeActivityUtil = new HomeActivityUtil(con);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View view = inflater.inflate(R.layout.custom_message_input_dialog, null);

        requestQueue = Volley.newRequestQueue(getActivity());

        builder.setView(view)
                // Add action buttons
                .setPositiveButton("send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // get the message and send to server
                        customMessageBody = view.findViewById(R.id.custom_message_body);
                        message_body = customMessageBody.getText().toString();

                        Log.e("Custom message = ", message_body);

                        Home.dialog = new ProgressDialog(getActivity());
                        Home.dialog.setMessage("Sending Your Message");
                        Home.dialog.show();

                        //send message using volley
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, APIEndPoint.sendMsg,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Home.dialog.dismiss();
                                        homeActivityUtil.resetHelpingUsersInfo();
                                        Toast.makeText(Home.con, "Message Sent Successfully", Toast.LENGTH_LONG).show();
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Home.dialog.dismiss();
                                        Toast.makeText(Home.con, error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }){
                            @Override
                            protected Map<String, String> getParams()
                            {
                                Map<String, String>  params = new HashMap<String, String>();
                                params.put("Username", Home.username);
                                params.put("Password", Home.password);
                                params.put("Latitude", Double.toString(Home.locationObj.getLatitude()));
                                params.put("Longitude", Double.toString(Home.locationObj.getLongitude()));
                                params.put("Message", message_body);
                                params.put("Timestamp", HomeActivityUtil.fixDateFormat(dateFormat.format(new Date())));
                                params.put("Radius", geoCastSettings.getRadiusSetting(con));
                                params.put("Reliable", geoCastSettings.getReliableSetting(con));

                                return params;
                            }
                        };

                        requestQueue.add(stringRequest);



                        floatingActionMenu.close(true);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MessageInputDialog.this.getDialog().cancel();

                    }
                });
        builder.setCancelable(false);
        Dialog dialog= builder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;

    }



}
