package com.example.digitaldiary.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.digitaldiary.R;

public class LastOrNewJourney extends Activity {
    private String type; //multimedia type

    /**
     * This is a method to create this page
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_or_new_journey);
        Log.i("LastOrNewJourney", "The last or new journey page is created!");
        type = getIntent().getStringExtra("type");
    }

    /**
     * This is a method to go to country selection page
     * @param v
     */
    public void onClickAnother(View v){
        Log.i("LastOrNewJourney", "The country selected page was called from 'last or new journey' for multimedia: " + type);
        Intent intent = new Intent(LastOrNewJourney.this, SelectCountry.class);
        intent.putExtra("type", type);
        startActivity(intent);
    }

    /**
     * This is a method, which finds the last journey
     * @param v
     */
    public void onClickLast(View v){
        boolean proof = proofIfLastJourneyPresent();
        if(proof) {
            SQLiteDatabase db = getBaseContext().openOrCreateDatabase("DigitalDiary.db", MODE_PRIVATE, null);
            Cursor journeyCursor = db.rawQuery("SELECT * FROM LASTTRIP", null);
            if (journeyCursor.moveToFirst()) {
                String country = journeyCursor.getString(0);
                String city = journeyCursor.getString(1);
                journeyCursor.close();
                db.close();
                Intent intent = null;
                switch (type) {
                    case "photo":
                        Log.i("LastOrNewJourney", "The last journey " + city + " ," + country + " photo gallery is called!");
                        intent = new Intent(LastOrNewJourney.this, PhotoActivity.class);
                        break;
                    case "video":
                        Log.i("LastOrNewJourney", "The last journey " + city + " ," + country + " video gallery is called!");
                        intent = new Intent(LastOrNewJourney.this, VideoAndAudioActivity.class);
                        break;
                    case "audio":
                        Log.i("LastOrNewJourney", "The last journey " + city + " ," + country + " audio gallery is called!");
                        intent = new Intent(LastOrNewJourney.this, VideoAndAudioActivity.class);
                        break;
                }

                if(intent != null) {
                    intent.putExtra("country", country);
                    intent.putExtra("city", city);
                    if(type.equals("video")){
                        intent.putExtra("type", "video");
                    } else if(type.equals("audio")){
                        intent.putExtra("type", "audio");
                    }
                    startActivity(intent);
                } else {
                    Log.e("LastOrNewJourney", "Error with type!");
                    Toast.makeText(LastOrNewJourney.this, "Error!", Toast.LENGTH_LONG).show();
                }
            }
            journeyCursor.close();
            db.close();
        } 
    }

    /**
     * This is a method to proof, do we have last journey in other system, or not
     * @return true - if we have last journey, else false
     */
    public boolean proofIfLastJourneyPresent() {
        SQLiteDatabase db = getBaseContext().openOrCreateDatabase("DigitalDiary.db", MODE_PRIVATE, null);
        Cursor proof_if_countries_present = db.rawQuery("SELECT COUNT(*) FROM lasttrip", null);
        proof_if_countries_present.moveToFirst();
        if (proof_if_countries_present.getInt(0) == 0) {
            Log.i("LastOrNewJourney", "There is no last journey in this App!");
            Toast.makeText(LastOrNewJourney.this, "You don't have last travel!", Toast.LENGTH_LONG).show();
            return false;
        }
        proof_if_countries_present.close();
        db.close();
        Log.i("LastOrNewJourney", "There is last journey in this App!");
        return true;
    }
}
