package com.example.digitaldiary.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.digitaldiary.R;
import android.util.Log;

import internet.InternetCommunication;

public class AddCountryActivity extends Activity {
    private SQLiteDatabase db;
    private String country, city;
    private InternetCommunication internet;

    /**
     * This is a method to create this activity and open the database
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_country);
        db = getBaseContext().openOrCreateDatabase("DigitalDiary.db", MODE_PRIVATE, null);
        Log.i("AddCountryActivity", "The add country activity is successfully started");
    }

    /**
     * This is a method to add new journey. It proofs some parameters, and then saves new journey in a database, or shows the toast with the description
     * of the problem
     * @param v
     */
    public void onClickNext(View v)  {
        EditText countryEdit = findViewById(R.id.editText);
        country = countryEdit.getText().toString();
        if(country.isEmpty()){
            Toast.makeText(AddCountryActivity.this, "Country can not be null!", Toast.LENGTH_LONG).show();
            return;
        }
        if ((country.charAt(0) >= 'a') && (country.charAt(0) <= 'z')) {
            Log.i("AddCountryActivity", "Country name started with a lower case!");
            Toast.makeText(AddCountryActivity.this, "You should start with upper case!", Toast.LENGTH_LONG).show();
            return;
        }
        EditText cityEdit = findViewById(R.id.editText2);
        city = cityEdit.getText().toString();
        if(city.isEmpty()){
            Toast.makeText(AddCountryActivity.this, "City can not be null!", Toast.LENGTH_LONG).show();
            return;
        }
        if ((city.charAt(0) >= 'a') && (city.charAt(0) <= 'z')) {
            Log.i("AddCountryActivity", "City name started with a lower case!");
            Toast.makeText(AddCountryActivity.this, "You should start with upper case!", Toast.LENGTH_LONG).show();
            return;
        }
        Cursor countryCursor = db.rawQuery("SELECT * FROM COUNTRIES", null);
        if (countryCursor.moveToFirst()) {
            do {
                if(countryCursor.getString(1).equals(country) && countryCursor.getString(2).equals(city)){
                    Log.i("AddCountryActivity", "This journey already exists!");
                    Toast.makeText(AddCountryActivity.this, "You already have this journey!", Toast.LENGTH_LONG).show();
                    return ;
                }
                if(countryCursor.getString(2).equals(city)){
                    Log.i("AddCountryActivity", "This journey already exists in another country!");
                    Toast.makeText(AddCountryActivity.this, "You already have this city in another journey!", Toast.LENGTH_LONG).show();
                    return ;
                }
            } while (countryCursor.moveToNext());
        }
        countryCursor.close();

        //This is the AsyncTask, which proofs the existence of this country, and saves it
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                internet = new InternetCommunication();
                boolean proof = internet.proofIfCountryExists(country);
                if(proof){
                    db.execSQL("INSERT INTO countries (country, city) VALUES ('" + country + "', '" + city + "');");
                    db.execSQL("DROP TABLE lasttrip");
                    db.execSQL("CREATE TABLE IF NOT EXISTS lasttrip (country TEXT, city TEXT)");
                    db.execSQL("INSERT INTO lasttrip VALUES ('" + country + "', '" + city + "');");
                    db.close();
                    Log.i("AddCountryActivity", "This journey is saved!");
                    Intent intent = new Intent(AddCountryActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("AddCountryActivity", "This country doesn't exist!");
                            Toast.makeText(AddCountryActivity.this, "This country doesn't exist!", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }
}


