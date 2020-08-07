package com.example.digitaldiary.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.example.digitaldiary.R;

import java.util.ArrayList;
import java.util.List;

public class SelectCity extends Activity {
    private List<String> cities = new ArrayList<>();
    private Spinner spinner;
    private String country;
    private String type;

    /**
     * This is a method to create select city activity. It creates spinner with all available cities in the specific country
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);
        country = getIntent().getStringExtra("country");
        type = getIntent().getStringExtra("type");
        if(type == null){
            type = "default";
        }
        SQLiteDatabase db = getBaseContext().openOrCreateDatabase("DigitalDiary.db", MODE_PRIVATE, null);
        spinner = findViewById(R.id.spinner2);
        Cursor cityCursor = db.rawQuery("SELECT * FROM COUNTRIES",null);
        if(cityCursor.moveToFirst()){
            do{
                if(cityCursor.getString(1).equals(country)){
                    cities.add(cityCursor.getString(2));
                }
            }while(cityCursor.moveToNext());
        }
        java.util.Collections.sort(cities);
        ArrayAdapter<String> spinnerCities = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cities);
        if(spinnerCities.getCount() != 0) {
            spinnerCities.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(spinnerCities);
        }
        Log.i("SelectCity", "The select city activity for country " + country + " is created!");
        cityCursor.close();
        db.close();
    }

    /**
     * This ia a method to go back to the main menu
     * @param v
     */
    public void onClickBack(View v){
        Intent intent = new Intent(SelectCity.this, MainActivity.class);
        Log.i("SelectCity", "The main activity was called from select city activity");
        startActivity(intent);
    }

    /**
     * This is a method to select the city from spinner and to go to the country menu page
     * @param v
     */
    public void onClickNext(View v){
        String city = spinner.getSelectedItem().toString();
        Intent intent;
        if(type.equals("photo")) {
            Log.i("SelectCity", "The photo activity was called for " + city + " city!");
            intent = new Intent(SelectCity.this, PhotoActivity.class);
        } else if(type.equals("video")){
            Log.i("SelectCity", "The video activity was called for " + city + " city!");
            intent = new Intent(SelectCity.this, VideoAndAudioActivity.class);
            intent.putExtra("type", "video");
        } else if(type.equals("audio")){
            Log.i("SelectCity", "The audio activity was called for " + city + " city!");
            intent = new Intent(SelectCity.this, VideoAndAudioActivity.class);
            intent.putExtra("type", "audio");
        } else {
            Log.i("SelectCity", "The country page activity was called for " + city + " city!");
            intent = new Intent(SelectCity.this, CountryPage.class);
        }
        intent.putExtra("country", country);
        intent.putExtra("city",city);
        startActivity(intent);
    }
}
