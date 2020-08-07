package com.example.digitaldiary.activities;

import androidx.annotation.RequiresApi;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.digitaldiary.R;

import internet.InternetCommunication;

public class CountryInformationPage extends Activity {
    private String country, city;
    private String information;
    private InternetCommunication internet;
    private SQLiteDatabase db;
    private int image_count = 0, video_count = 0, audio_count = 0;

    /**
     * This is a method to create the page with information about country and the number of media files from a particular city in that country.
     * It gets this information through the appropriate API.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_information_page);
        country = getIntent().getStringExtra("country");
        city = getIntent().getStringExtra("city");
        final TextView actual_text = findViewById(R.id.text);
        Log.i("CountryInformationPage", "This country information page for journey " + country + ", " + city +" is started!");
        internet = new InternetCommunication();
        final String[] text = {country + "\n" + "\n"};

        //This is the AsyncTask, which retrieves country information through the API
        AsyncTask.execute(new Runnable(){
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                information = internet.getCountryInformation(country);
                if(information == null){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.w("CountryInformationPage", "Problems with internet!");
                            Toast.makeText(CountryInformationPage.this, "Problems with internet!", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            db = getBaseContext().openOrCreateDatabase("DigitalDiary.db", MODE_PRIVATE, null);
                            Cursor imageCursor = db.rawQuery("SELECT * FROM images", null);
                            if (imageCursor.moveToFirst()) {
                                do {
                                    if(imageCursor.getString(1).equals(city)){
                                        image_count = image_count + 1;
                                    }
                                } while (imageCursor.moveToNext());
                            }
                            Cursor videoCursor = db.rawQuery("SELECT * FROM videos", null);
                            if (videoCursor.moveToFirst()) {
                                do {
                                    if(videoCursor.getString(1).equals(city)){
                                        video_count++;
                                    }
                                } while (videoCursor.moveToNext());
                            }
                            Cursor audioCursor = db.rawQuery("SELECT * FROM audios", null);
                            if (audioCursor.moveToFirst()) {
                                do {
                                    if(audioCursor.getString(1).equals(city)){
                                        audio_count++;
                                    }
                                } while (audioCursor.moveToNext());
                            }
                            audioCursor.close();
                            videoCursor.close();
                            imageCursor.close();
                            db.close();
                            text[0] = text[0] + information + "\n\n\n" + "Current city: " + city + "\n\n";
                            text[0] = text[0] + "You have " + image_count + " images from this city " + "\n\n";
                            text[0] = text[0] + "You have " + video_count + " videos from this city " + "\n\n";
                            text[0] = text[0] + "You have " + audio_count + " audios from this city " + "\n\n";
                            actual_text.setText(text[0]);
                            Log.i("CountryInformationPage", "Country information is shown!");
                        }
                    });
                }
            }
        });
    }
}
