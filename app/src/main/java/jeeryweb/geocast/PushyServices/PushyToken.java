package jeeryweb.geocast.PushyServices;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import jeeryweb.geocast.Activities.Home;
import jeeryweb.geocast.Constants.APIEndPoint;
import jeeryweb.geocast.Utility.Network;
import jeeryweb.geocast.Utility.SharedPrefHandler;
import me.pushy.sdk.Pushy;

public class PushyToken {

//    private final String updatePushy = "https://jeeryweb.000webhostapp.com/ProjectLoc/updatePushy.php";

    private String TAG = "PushyTokenClass";
    private SharedPrefHandler sharedPrefHandler;
    APIEndPoint apiEndPoint;
    private String pushyToken;
    private Context context;
    private Network network;
    private String result;

    public void getPushyDeviceToken(Context context)
    {
        this.context = context;
        new RegisterForPushNotificationsAsync().execute();

    }

    //class for uploading pushydevice token to server *********************************
    private class RegisterForPushNotificationsAsync extends AsyncTask<Void, Void, Exception> {
        protected Exception doInBackground(Void... params) {
            try {

                // Assign a unique token to this device
                String deviceToken = Pushy.register(context);
                pushyToken = deviceToken;

                // Log it for debugging purposes
                Log.e("MyApp", "Pushy device token: " + deviceToken);
                // Send the token to your backend server via an HTTP GET request
                //new URL("https://{YOUR_API_HOSTNAME}/register/device?token=" + deviceToken).openConnection();
            }
            catch (Exception exc) {
                // Return exc to onPostExecute
                return exc;
            }

            // Success
            return null;
        }

        @Override
        protected void onPostExecute(Exception exc) {
            // Failed?
            if (exc != null) {
                // Show error as toast message
                Toast.makeText(context, exc.toString(), Toast.LENGTH_LONG).show();
                return;
            }

            // Succeeded, do something to alert the user
            Log.e(TAG,"pushy token upload server successful");
            sharedPrefHandler = new SharedPrefHandler(context);
            sharedPrefHandler.savePushyToken(pushyToken);

            if (Home.username != null && Home.password != null & pushyToken != null) {
                new Thread(new Runnable() {
                    public void run() {                                                 //THREAD 1.................
                        // a potentially  time consuming task
                        Log.e("new thread", "starting upload of Pushy token to app server");
                        network = new Network(apiEndPoint.updatePushy, Home.username, Home.password, "dummy", "00.00", "00.00", pushyToken, null, null, null, null);
                        result = network.DoWork();
                        if (result != null) {
                            Log.e(TAG, " Pushy " + result);
                        }
                    }
                }).start();
            }


        }
    }

}
