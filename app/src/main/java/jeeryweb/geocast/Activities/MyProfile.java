package jeeryweb.geocast.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import jeeryweb.geocast.Constants.APIEndPoint;
import jeeryweb.geocast.R;
import jeeryweb.geocast.Utility.PPUpload;
import jeeryweb.geocast.Utility.SharedPrefHandler;

public class MyProfile extends AppCompatActivity {


    private ImageButton selectProfilePic;
    private ImageView profilePic;
    private TextView usernamePP,occupationPP,agePP,genderPP,phonePP;
    private static final int RESULT_SELECT_IMAGE = 1;
    private ProgressDialog progressDialogUplaod;
    private String usernameName;

    APIEndPoint apiEndPoint;
    Context c;

//    private final String url_uplaod = "https://jeeryweb.000webhostapp.com/ProjectLoc/saveProPic.php";
    private String pathPP;
    private Bitmap bitmapPP;
    private  SharedPrefHandler sharedPrefHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        sharedPrefHandler=new SharedPrefHandler(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        c=this;
        sharedPrefHandler= new SharedPrefHandler(this);

        selectProfilePic = (ImageButton)findViewById(R.id.activity_myprofile_addimagebutton);
        profilePic = (ImageView)findViewById(R.id.activity_myprofile_pp);
        usernamePP = (TextView) findViewById(R.id.activity_myprofile_username);
        occupationPP = (TextView) findViewById(R.id.activity_myprofile_profession);
        agePP = (TextView) findViewById(R.id.activity_myprofile_age);
        genderPP = (TextView) findViewById(R.id.activity_myprofile_gender);
        phonePP = (TextView) findViewById(R.id.activity_myprofile_phno);


        selectProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call the function to select image from album
                selectImage();
            }
        });

        usernameName = sharedPrefHandler.getUsername();

        if(sharedPrefHandler.getPPpath()!= null)
            profilePic.setImageBitmap(getSavedProfilePicture(sharedPrefHandler.getPPpath()));

        usernamePP.setText(usernameName);
        occupationPP.setText(sharedPrefHandler.getOccupation());
        agePP.setText(sharedPrefHandler.getAge());
        genderPP.setText(sharedPrefHandler.getGender());
        phonePP.setText(sharedPrefHandler.getPhoneNo());

    }

    //to handle upload image************************************************************************
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

            //get the current timeStamp and strore that in the time Variable
            Long tsLong = System.currentTimeMillis() / 1000;
            String timestamp = tsLong.toString();

            progressDialogUplaod = new ProgressDialog(MyProfile.this);
            progressDialogUplaod.setIndeterminate(true);
            progressDialogUplaod.setMessage("Uploading Image");
            progressDialogUplaod.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialogUplaod.show();

            //get image in bitmap format
            try {
                bitmapPP = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image);
            } catch (IOException e) {
                e.printStackTrace();
                bitmapPP = null;
            }
            //Bitmap image2 = ((BitmapDrawable) image.getBitmap();
            //execute the async task and upload the image to server
            String picname = "IMG_"+timestamp;
            if(bitmapPP!=null){
                pathPP = saveProfilePicture(bitmapPP);
                new MyProfile.Upload(bitmapPP,picname).execute();
            }
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
            detail.put("user",usernameName);

            try{
                //convert this HashMap to encodedUrl to send to php file
                String dataToSend = hashMapToUrl(detail);
                //make a Http request and send data to saveImage.php file
                String response = PPUpload.post(apiEndPoint.url_uplaod,dataToSend);

                //return the response
                return response;

            }catch (Exception e){
                e.printStackTrace();
                Log.e("my profile pp upload","ERROR  "+e);
                return null;
            }
        }



        protected void onPostExecute(String s){
            //show image uploaded
            progressDialogUplaod.dismiss();

            if(s!=null) {
                Toast.makeText(getApplicationContext(), "Image Uploaded " + s, Toast.LENGTH_SHORT).show();
                Log.e("Myprofile", s);
                profilePic.setImageBitmap(bitmapPP);
            }
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

    public String saveProfilePicture(Bitmap bitmap) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"profile.jpg");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        sharedPrefHandler.savePPpath(directory.getAbsolutePath());
        return directory.getAbsolutePath();

    }

    public Bitmap getSavedProfilePicture(String path)
    {
        try {
            File f=new File(path, "profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            return b;
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
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
