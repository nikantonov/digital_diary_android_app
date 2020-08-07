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
import java.util.HashSet;
import java.util.List;

public class SelectCountry extends Activity {
    private HashSet<String> countries = new HashSet<>();
    private Spinner spinner;
    private String type = "default";

    /**
     * This ia a method to create select country activity. It makes spinner with all available countries.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_country);
        SQLiteDatabase db = getBaseContext().openOrCreateDatabase("DigitalDiary.db", MODE_PRIVATE, null);
        spinner = findViewById(R.id.spinner2);
        type = getIntent().getStringExtra("type");
        Cursor countryCursor = db.rawQuery("SELECT * FROM COUNTRIES",null);
        if(countryCursor.moveToFirst()){
            do{
                System.out.println(countryCursor.getString(1) + countryCursor.getString(2));
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
        Log.i("SelectCountry", "The select country activity is created!");
        countryCursor.close();
        db.close();
    }

    /**
     * This is a method to choose country from spinner and to got to select city activity
     * @param v
     */
    public void onClickNext(View v){
        String result = spinner.getSelectedItem().toString();
        Intent intent = new Intent(SelectCountry.this, SelectCity.class);
        intent.putExtra("country", result);
        intent.putExtra("type", type);
        Log.i("SelectCountry", "The select city activity for country " + result + " is called!");
        startActivity(intent);
    }

    /**
     * This is a method to go back to the main menu
     * @param v
     */
    public void onClickBack(View v){
        Log.i("SelectCountry", "The main activity was called from select country activity");
        Intent intent = new Intent(SelectCountry.this, MainActivity.class);
        startActivity(intent);
    }
}
