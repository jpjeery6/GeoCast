package jeeryweb.geocast.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import jeeryweb.geocast.Constants.APIEndPoint;
import jeeryweb.geocast.R;
import jeeryweb.geocast.Utility.SharedPrefHandler;

public class Feedback extends AppCompatActivity {

    private EditText editTextMail, editTextFeedback;
    private Button butt;
    Context con;
    ProgressDialog progressDialog;
    String TAG = "Feedback";
    SharedPrefHandler sharedPrefHandler;
    String username, password;
    RequestQueue requestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        editTextFeedback = (EditText) findViewById(R.id.addr_edittext);
        editTextMail = (EditText) findViewById(R.id.addr_editmail);
        butt = (Button) findViewById(R.id.addr_buttSend);
        con  = this;
        sharedPrefHandler = new SharedPrefHandler(this);
        requestQueue = Volley.newRequestQueue(this);

        HashMap<String, String> user = sharedPrefHandler.getUserDetails();
        username = user.get("name");
        password = user.get("pass");

        butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!validateInput()){
                    Toast.makeText(con, "Invalid input", Toast.LENGTH_SHORT);
                    return;
                }

                progressDialog = new ProgressDialog(Feedback.this);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Submitting Response");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();



                StringRequest stringRequest = new StringRequest(Request.Method.POST, APIEndPoint.feedback,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                progressDialog.dismiss();
                                JSONObject obj = null;
                                Log.e(TAG, response);
                                try {
                                    obj = new JSONObject(response);
                                    if(!obj.getBoolean("success")){
                                        Log.e(TAG, "Error occurred suucess failed");
                                        Toast.makeText(con, "Error occurred", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    Toast.makeText(con, "Feedback submitted succesfully!", Toast.LENGTH_SHORT).show();

                                } catch (JSONException e) {
                                    Toast.makeText(con, "Some error occurred!", Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                progressDialog.dismiss();
                                Log.e(TAG,error.getMessage());
                                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }){
                    @Override
                    protected Map<String, String> getParams()
                    {
                        Map<String, String>  params = new HashMap<String, String>();
                        params.put("Username", username);
                        params.put("Password", password);
                        params.put("mail", String.valueOf(editTextMail.getText()));
                        params.put("feedback", String.valueOf(editTextFeedback.getText()));

                        return params;
                    }
                };
                requestQueue.add(stringRequest);
            }
        });

        editTextFeedback.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction() & MotionEvent.ACTION_MASK){
                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });
    }

    Boolean validateInput(){
        if(editTextMail.getText().toString().length()==0 || editTextFeedback.getText().toString().length()==0)
            return false;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}