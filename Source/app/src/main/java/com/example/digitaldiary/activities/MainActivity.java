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
import android.widget.Toast;
import com.example.digitaldiary.R;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MainActivity extends Activity {
    private HashSet<String> countries = new HashSet<>();
    private Spinner spinner;

    /**
     * This is a method to create the main menu. It creates new database with tables, if necessary. And it creates spinner with available
     * countries
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SQLiteDatabase db = getBaseContext().openOrCreateDatabase("DigitalDiary.db", MODE_PRIVATE, null);

        //SQLite tables creation
        db.execSQL("CREATE TABLE IF NOT EXISTS countries (_id INTEGER PRIMARY KEY AUTOINCREMENT, country TEXT, city TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS lasttrip (country TEXT, city TEXT)"); //in lasttrip we have always only one trip
        db.execSQL("CREATE TABLE IF NOT EXISTS date (_id INTEGER PRIMARY KEY AUTOINCREMENT, city TEXT, date TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS videos (_id INTEGER PRIMARY KEY AUTOINCREMENT, city TEXT, path TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS audios (_id INTEGER PRIMARY KEY AUTOINCREMENT, city TEXT, path TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS images (_id INTEGER PRIMARY KEY AUTOINCREMENT, city TEXT, path TEXT)");

        spinner = findViewById(R.id.spinner2);
        Cursor countryCursor = db.rawQuery("SELECT * FROM COUNTRIES",null);
        if(countryCursor.moveToFirst()){
            do{
                countries.add(countryCursor.getString(1));
            }while(countryCursor.moveToNext());
        }
        List<String> countries_to_adapter = new ArrayList<>(countries);
        java.util.Collections.sort(countries_to_adapter);
        ArrayAdapter<String> spinnerCountries = new ArrayAdapter<>(this,  android.R.layout.simple_spinner_item, countries_to_adapter);
        if(spinnerCountries.getCount() != 0) {
            spinnerCountries.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(spinnerCountries);
        }
        Log.i("MainActivity", "The main activity is created!");
        countryCursor.close();
        db.close();
    }

    /**
     * This is a method to select country from spinner and to go to the next activity to select the city
     * @param v
     */
    public void onClickGo(View v){
        boolean proof = this.proofIfCountriesPresent();
        if(proof){
            String result = spinner.getSelectedItem().toString();
            Intent intent = new Intent(MainActivity.this, SelectCity.class);
            intent.putExtra("country", result);
            Log.i("MainActivity", "The country " + result + " was chosen!");
            startActivity(intent);
        }
    }

    /**
     * This is a method to go to the add country activity
     * @param v
     */
    public void onClickAddCountry(View v){
        Log.i("MainActivity", "The add country page was chosen!");
        Intent intent = new Intent(MainActivity.this, AddCountryActivity.class);
        startActivity(intent);
    }

    /**
     * This is a method to go to the photo gallery with faces
     * @param v
     */
    public void onClickFaces(View v){
        Log.i("MainActivity", "The faces photo gallery was chosen!");
        Intent intent = new Intent(MainActivity.this, PhotoActivity.class);
        intent.putExtra("city","faces");
        startActivity(intent);
    }

    /**
     * This is a method to go to the "Try automatically" page
     * @param v
     */
    public void onClickAuto(View v){
        Log.i("MainActivity", "The 'Try automatically!' page was chosen!");
        Intent intent = new Intent(MainActivity.this, TryAutomatically.class);
        startActivity(intent);
    }

    /**
     * This is a method to go to the photo menu
     * @param v
     */
    public void onClickPhoto(View v){
        boolean proof = this.proofIfCountriesPresent();
        if(proof){
            Log.i("MainActivity", "The photo menu was chosen!");
            Intent intent = new Intent(MainActivity.this, LastOrNewJourney.class);
            intent.putExtra("type", "photo");
            startActivity(intent);
        }
    }

    /**
     * This is a method to go to the video menu
     * @param v
     */
    public void onClickVideo(View v){
        boolean proof = this.proofIfCountriesPresent();
        if(proof){
            Log.i("MainActivity", "The video menu was chosen!");
            Intent intent = new Intent(MainActivity.this, LastOrNewJourney.class);
            intent.putExtra("type", "video");
            startActivity(intent);
        }
    }

    /**
     * This is a method to go to the audio menu
     * @param v
     */
    public void onClickAudio(View v){
        boolean proof = this.proofIfCountriesPresent();
        if(proof){
            Log.i("MainActivity", "The audio menu was chosen!");
            Intent intent = new Intent(MainActivity.this, LastOrNewJourney.class);
            intent.putExtra("type", "audio");
            startActivity(intent);
        }

    }

    /**
     * This is a method for checking if countries exist in the system or not yet.
     * @return true - if we have at least 1 country, else false
     */
    public boolean proofIfCountriesPresent(){
        SQLiteDatabase db = getBaseContext().openOrCreateDatabase("DigitalDiary.db", MODE_PRIVATE, null);
        Cursor proof_if_countries_present = db.rawQuery("SELECT COUNT(*) FROM countries", null);
        proof_if_countries_present.moveToFirst();
        if (proof_if_countries_present.getInt(0) == 0) {
            Log.i("MainActivity", "There is no countries in this system!");
            Toast.makeText(MainActivity.this, "You don't have travels yet!", Toast.LENGTH_LONG).show();
            return false;
        }
        proof_if_countries_present.close();
        db.close();
        Log.i("MainActivity", "The are countries in this system!");
        return true;
    }
}

