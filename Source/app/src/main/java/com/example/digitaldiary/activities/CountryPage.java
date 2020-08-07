package com.example.digitaldiary.activities;

import androidx.annotation.RequiresApi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.digitaldiary.R;
import internet.InternetCommunication;

public class CountryPage extends Activity {
    private String country, city;
    private InternetCommunication internet;
    private String alphaCode;
    private ImageView user_flag;

    /**
     * This is a method to create the menu of the journey page. It downloads the passing flag to this country
     * @param savedInstanceState
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        internet = new InternetCommunication();
        setContentView(R.layout.activity_country_page);
        country = getIntent().getStringExtra("country");
        city = getIntent().getStringExtra("city");
        TextView actual_country = findViewById(R.id.textView2);
        user_flag = findViewById(R.id.imageView);
        String text = country+", "+city;
        actual_country.setText(text);
        Log.i("CountryPage", "This country page for journey " + country + ", " + city +" is started!");

        //This is the AsyncTask, which loads the country flag
        AsyncTask.execute(new Runnable(){
            @Override
            public void run() {
                alphaCode = internet.getCountryCode(country);
                if(alphaCode == null){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.w("CountryPage", "Problems with internet!");
                            Toast.makeText(CountryPage.this, "Problems with internet!", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    final Bitmap image = BitmapFactory.decodeStream(internet.getFlag(alphaCode));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            user_flag.setImageBitmap(image);
                            Log.i("CountryPage", "Flag is uploaded and placed!");
                        }
                    });
                }
            }
        });
    }

    /**
     * This is a method to delete the specific journey
     * @param v
     */
    public void onClickDelete(View v){
        AlertDialog alert = new AlertDialog.Builder(this).setTitle("Delete")
                .setMessage("Are you sure you want to delete this journey?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog_interface, int i) {
                        SQLiteDatabase db = getBaseContext().openOrCreateDatabase("DigitalDiary.db", MODE_PRIVATE, null);
                        db.delete("images", " city = ? ",
                                new String[] {city+""});
                        db.delete("audios", " city = ? ",
                                new String[] {city+""});
                        db.delete("videos", " city = ? ",
                                new String[] {city+""});
                        db.delete("countries", " country = ? AND city = ? ",
                                new String[] {country, city+""});
                        db.delete("date", " city = ? ",
                                new String[] {city+""});
                        Cursor lastTripCursor = db.rawQuery("SELECT * FROM LASTTRIP", null);
                        if(lastTripCursor.moveToFirst()){
                            if(lastTripCursor.getString(0).equals(country) && lastTripCursor.getString(1).equals(city)){
                                db.execSQL("DROP TABLE lasttrip");
                            }
                        }
                        lastTripCursor.close();
                        Log.i("CountryPage", "This country page for journey " + country + ", " + city +" was deleted!");
                        db.close();
                        Intent intent = new Intent(CountryPage.this, MainActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog_interface, int i) {
                        dialog_interface.cancel();
                    }
                })
                .create();
        alert.show();
    }

    /**
     * This is a method to go back to main activity
     * @param v
     */
    public void onClickBack(View v){
        Log.i("CountryPage", "The main page was called from country page!");
        Intent intent = new Intent(CountryPage.this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * This is a method to load the passing Wikipedia page for this city
     * @param v
     */
    public void onClickWeb(View v){
        Log.i("countryPage", "This Wikipedia page for journey " + country + ", " + city +" was started!");
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://en.wikipedia.org/wiki/"+city)));
    }

    /**
     * This is a method to take the country information page for the passing journey
     * @param v
     */
    public void onClickInfo(View v){
        Log.i("countryPage", "This country information page for journey " + country + ", " + city +" was called from country page!");
        Intent intent = new Intent(CountryPage.this, CountryInformationPage.class);
        intent.putExtra("country", country);
        intent.putExtra("city",city);
        startActivity(intent);
    }

    /**
     * This is a method to get the photo gallery page to the passing journey
     * @param v
     */
    public void onClickPhoto(View v){
        Log.i("countryPage", "This photo gallery page for journey " + country + ", " + city +" was called from country page!");
        Intent intent = new Intent(CountryPage.this, PhotoActivity.class);
        intent.putExtra("country", country);
        intent.putExtra("city",city);
        startActivity(intent);
    }

    /**
     * This is a method to get the video gallery page to the passing journey
     * @param v
     */
    public void onClickVideo(View v){
        Log.i("countryPage", "This video gallery page for journey " + country + ", " + city +" was called from country page!");
        Intent intent = new Intent(CountryPage.this, VideoAndAudioActivity.class);
        intent.putExtra("country", country);
        intent.putExtra("city",city);
        intent.putExtra("type", "video");
        startActivity(intent);
    }

    /**
     * This is a method to get the audio gallery page to the passing journey
     * @param v
     */
    public void onClickAudio(View v){
        Log.i("countryPage", "This audio gallery page for journey " + country + ", " + city +" was called from country page!");
        Intent intent = new Intent(CountryPage.this, VideoAndAudioActivity.class);
        intent.putExtra("country", country);
        intent.putExtra("city",city);
        intent.putExtra("type", "audio");
        startActivity(intent);
    }
}
