package com.example.digitaldiary.activities;

import androidx.annotation.RequiresApi;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.digitaldiary.R;

import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import internet.InternetCommunication;
import metadata.MultimediaMetaDataManager;
import metadata.PhotoManager;


public class InformationActivity extends Activity {
    private Uri uri;
    private TextView actual_text;
    private String text;
    private InternetCommunication internet;

    /**
     * This is a method to create the page with multimedia information
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        uri = Uri.parse(getIntent().getStringExtra("uri"));
        String type = getIntent().getStringExtra("type");
        internet = new InternetCommunication();
        actual_text = findViewById(R.id.text);
        if (type.equals("photo")){
            Log.i("InformationActivity", "The information page about the photo " + uri.toString() +" is started!");
            setPhotoText();
        } else if (type.equals("multimedia")){
            Log.i("InformationActivity", "The information page about the multimedia " + uri.toString() +" is started!");
            setMultimediaText();
        }
    }

    /**
     * This is a method to create the text about the specific photo. It takes information from EXIF and from internet via the appropriate API
     */
    public void setPhotoText(){
        InputStream in = null;
        try {
            in = getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            Log.e("InformationActivity", "The InputStream from " + uri.toString() + " can not be opened!");
            e.printStackTrace();
        }
        PhotoManager meta = new PhotoManager(in);
        if(meta.getDate() != null) {
            text = "Date: " + meta.getDate() + "\n\n";
        }
        if(meta.getAreaInformation() != null) {
            text = text + "Area information: " + meta.getAreaInformation() + "\n\n";
        }
        if(meta.getDeviceInformation() != null){
            text = text + "Device information: " + meta.getDeviceInformation() + "\n\n";
        }
        if(meta.getLatitude() != null){
            text = text + "Latitude: " + meta.getLatitude() + "\n\n";
        }
        if(meta.getLongitude() != null){
            text = text + "Longitude: " + meta.getLongitude() + "\n\n";
        }

        final double[] latlng = meta.getLatLng();
        if (latlng != null) {
            Log.i("InformationActivity", "The AsyncTask to take area information from GPS is started!");

            //This is the AsyncTask, which loads the photo area information
            AsyncTask.execute(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void run() {
                    try {
                        int counter = 4;
                        List<String> result;
                        do {
                            result = internet.getPhotoInformation(latlng[0], latlng[1]);
                            counter = counter - 1;
                        } while(result.size() == 0 && counter > 0);

                        if(result.size() > 1) {
                            text = text + "Region: " + result.get(1) + "\n\n";
                        }
                        if(result.size() > 2) {
                            text = text + "State: " + result.get(2) + "\n\n";
                        }
                        if(result.size() > 0) {
                            text = text + "Address: " + result.get(0) + "\n\n";
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.i("InformationActivity", "The information text about the photo " + uri.toString() + " is displayed!");
                                actual_text.setText(text);
                            }
                        });
                    } catch (IOException e) {
                        Log.e("InformationActivity", "Problems with input/output from the Internet!");
                        e.printStackTrace();
                    } catch (ParseException e) {
                        Log.e("InformationActivity", "Problems with parsing information from the Internet!");
                        e.printStackTrace();
                    }
                }
            });
        } else {
            Log.i("InformationActivity", "The information text about the photo " + uri.toString() + " is displayed!");
            actual_text.setText(text);
        }
    }

    /**
     * This is a method to create the text about the specific multimedia. It takes information from Multimedia
     */
    public void setMultimediaText(){
        MultimediaMetaDataManager media = new MultimediaMetaDataManager();
        String text = media.takeMultimediaInformation(getBaseContext(), uri);
        Log.i("InformationActivity", "The information text about the multimedia " + uri.toString() + " is displayed!");
        actual_text.setText(text);
    }
}
