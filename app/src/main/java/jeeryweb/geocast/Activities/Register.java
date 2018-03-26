package jeeryweb.geocast.Activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import jeeryweb.geocast.Utility.Network;

import jeeryweb.geocast.R;
import jeeryweb.geocast.Utility.PPUpload;
import jeeryweb.geocast.Utility.SharedPrefHandler;

public class Register extends AppCompatActivity {


//Attributes*************************************************************

    private final String reg ="https://jeeryweb.000webhostapp.com/ProjectLoc/register.php";
    private final String url_uplaod = "http://fcmapi.000webhostapp.com/savePropic.php";
    private final String TAG=getClass().getSimpleName()+" LoginActivity";

    //objects
    Network network;
    Context c;
    SharedPrefHandler session;
    Handler handler;

    //widgets
    EditText reguser , regpass1,regpass2 , bioAge , bioOccupation , phone;
    RadioGroup bioGender;
    Button register ;
    TextView loginLink;
    ProgressDialog progressDialog , progressDialogUplaod;

    String bio,fcmtoken;
    TextView iemiNumberSlot;
    String imeiNumber;
    ImageView imageView;
    ImageButton selectImage;

    private static final int RESULT_SELECT_IMAGE = 1;

    public String uss,pas,result, phonevalue=null , timestamp ,picname=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //setting up status bar
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        session = new SharedPrefHandler(this);
        c = this;

        //setting up widgets
        reguser=(EditText)findViewById(R.id.activity_register_user);
        regpass1=(EditText)findViewById(R.id.activity_register_pass1);
        regpass2=(EditText)findViewById(R.id.activity_register_pass2);
        register=(Button)findViewById(R.id.activity_register_regbtn) ;
        loginLink=(TextView)findViewById(R.id.activity_register_link_login);

        bioAge = (EditText)findViewById(R.id.activity_register_bioage);
        bioOccupation = (EditText)findViewById(R.id.activity_register_biooccupation);
        bioGender = (RadioGroup)findViewById(R.id.activity_register_biogender);

        phone = (EditText)findViewById(R.id.activity_register_phonenumber);
        iemiNumberSlot = (TextView)findViewById(R.id.activity_register_ieminumber);

        imageView = (ImageView) findViewById(R.id.activity_register_pp_imageView);
        selectImage = (ImageButton) findViewById(R.id.activity_register_ppselectImage);

        //ask for permissions
        //getImei_askForPermission(Manifest.permission.READ_PHONE_STATE, 0x1);

        imeiNumber = getPhoneIEMINumber();
        iemiNumberSlot.setText(imeiNumber);
        session.saveIMEI(imeiNumber);


        // Toast.makeText(c, getPhoneIEMINumber(), Toast.LENGTH_LONG).show();
        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                signup();
            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call the function to select image from album
                selectImage();
            }
        });

        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                result = msg.getData().get("result").toString();



                // Starting nextActivity
                if (result.contains("valid")) {

                    session.createLoginSession(uss, pas);
                    Intent i = new Intent(c, Home.class);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("firstTime",true);
                    i.putExtras(bundle);
                    startActivity(i);
                    finish();
                }
                else{
                    Toast.makeText(c, result, Toast.LENGTH_LONG).show();
                }

            }
        };

    }


    public void signup() {
        Log.e(TAG, "Signup");

        if (!validate()) {
            return;
        }

        progressDialog = new ProgressDialog(Register.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        //Toast.makeText(this, iemiNumber, Toast.LENGTH_SHORT).show();

        uss = reguser.getText().toString();
        pas = regpass1.getText().toString();

        int selectedId = bioGender.getCheckedRadioButtonId();
        RadioButton bioGendervalue = (RadioButton) findViewById(selectedId);


        bio = bioAge.getText().toString() + '|' + bioOccupation.getText().toString() + '|' + bioGendervalue.getText().toString();
        String reEnterPassword = regpass2.getText().toString();
        fcmtoken = session.getFcmToken();
        phonevalue = phone.getText().toString();

        Log.d(TAG, "Data "+uss+pas+bio+imeiNumber);
        new Thread(new Runnable() {
            public void run() {
                // a potentially  time consuming task
                network =new Network(reg,uss,pas,"dummymsg","00.00","00.00",fcmtoken, imeiNumber, bio, phonevalue, picname);
                result=network.DoWork();
                Log.e("result",result);
                progressDialog.dismiss();
                if(result!=null) {
                    //pass this result to UI thread by writing a message to the UI's handler
                    Message m=Message.obtain();
                    Bundle bundle=new Bundle();
                    bundle.putString("result",result);
                    m.setData(bundle);
                    handler.sendMessage(m);
                }

            }
        }).start();

    }

    private void getImei_askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(Register.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should show an explanation
            if (ActivityCompat.shouldShowRequestPermissionRationale(Register.this, permission)) {

                ActivityCompat.requestPermissions(Register.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(Register.this, new String[]{permission}, requestCode);
            }
        } else {
            imeiNumber = getPhoneIEMINumber();
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    imeiNumber = getPhoneIEMINumber();
                    iemiNumberSlot.setText(imeiNumber);

                } else {

                    Toast.makeText(Register.this, "You have Denied the Permission", Toast.LENGTH_SHORT).show();
                    imeiNumber = "permission denied";

                }
                return;
            }
        }
    }

    public String getPhoneIEMINumber() {
        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//
//            return telephonyManager.getImei();
//        }
//        else {
//            return telephonyManager.getDeviceId();
//        }
        return telephonyManager.getDeviceId();
    }

    public boolean validate() {
        boolean valid = true;

        String email = reguser.getText().toString();
        String password = regpass1.getText().toString();
        String passwordre= regpass2.getText().toString();
        String p = phone.getText().toString();

        if(imeiNumber=="permission denied"){
            Toast.makeText(Register.this, "Please grant permission", Toast.LENGTH_SHORT).show();
            getImei_askForPermission(Manifest.permission.READ_PHONE_STATE, 0x1);
            return false;
        }

        if (email.isEmpty() || email.length() < 4 || email.length() > 20) {
            reguser.setError("enter a valid username between 4 to 20 characters");
            valid = false;
        } else {
            reguser.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            regpass1.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            regpass1.setError(null);
        }

        if (passwordre.isEmpty() || passwordre.length() < 4 || passwordre.length() > 10 || !(passwordre.equals(password))) {
            regpass2.setError("Password Do not match");
            valid = false;
        } else {
            regpass2.setError(null);
        }
        if (p.length()!=10 && p.length()!=0) {
            phone.setError("Invalid phone number");
            valid = false;
        } else {
            phone.setError(null);
        }

        return valid;
    }

    public void selectImage(){
        //open album to select image
        Intent gallaryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallaryIntent, RESULT_SELECT_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_SELECT_IMAGE && resultCode == RESULT_OK && data != null){
            //set the selected image to image variable
            Uri image = data.getData();
            imageView.setImageURI(image);

            //get the current timeStamp and strore that in the time Variable
            Long tsLong = System.currentTimeMillis() / 1000;
            timestamp = tsLong.toString();

            progressDialogUplaod = new ProgressDialog(Register.this);
            progressDialogUplaod.setIndeterminate(true);
            progressDialogUplaod.setMessage("Uploading Image");
            progressDialogUplaod.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialogUplaod.show();

            //get image in bitmap format
            Bitmap image2 = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            //execute the async task and upload the image to server
            picname = "IMG_"+timestamp;
            new Upload(image2,picname).execute();
        }
    }

    private class Upload extends AsyncTask<Void,Void,String> {
        private Bitmap image;
        private String name;

        public Upload(Bitmap image,String name){
            this.image = image;
            this.name = name;
        }

        @Override
        protected String doInBackground(Void... params) {

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            //compress the image to jpg format
            image.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
            /*
            * encode image to base64 so that it can be picked by saveImage.php file
            * */
            String encodeImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

            //generate hashMap to store encodedImage and the name
            HashMap<String,String> detail = new HashMap<>();
            detail.put("name", name);
            detail.put("image", encodeImage);

            try{
                //convert this HashMap to encodedUrl to send to php file
                String dataToSend = hashMapToUrl(detail);
                //make a Http request and send data to saveImage.php file
                String response = PPUpload.post(url_uplaod,dataToSend);

                //return the response
                return response;

            }catch (Exception e){
                e.printStackTrace();
                Log.e(TAG,"ERROR  "+e);
                return null;
            }
        }



        protected void onPostExecute(String s){
            //show image uploaded
            progressDialogUplaod.dismiss();
            if(s!=null)
                Toast.makeText(getApplicationContext(),"Image Uploaded "+s,Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getApplicationContext(),"Image Uploaded Failed",Toast.LENGTH_SHORT).show();
        }
    }

    private String hashMapToUrl(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}
