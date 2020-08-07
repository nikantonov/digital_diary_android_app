package com.example.digitaldiary.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.digitaldiary.R;

import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import internet.InternetCommunication;
import metadata.PhotoManager;
import metadata.MultimediaMetaDataManager;

public class TryAutomatically extends Activity {
    private InternetCommunication internet;
    private String type;
    private SQLiteDatabase db;
    private PhotoManager meta;

    /**
     * This is a method to create the 'try automatically' activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("TryAutomatically", "Try automatically activity is created!");
        setContentView(R.layout.activity_try_automatically);
        internet = new InternetCommunication();
    }

    /**
     * It is a method for calling the automatic photo saving function.
     * @param v
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onClickAdd(View v){
        type = "photo";
        Log.i("TryAutomatically", "Automatic photo saving function is called!");
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    /**
     * It is a method for calling the automatic video saving function.
     * @param v
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onClickAddVideo(View v){
        type = "video";
        Log.i("TryAutomatically", "Automatic video saving function is called!");
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("video/*");
        startActivityForResult(intent, 1);
    }

    /**
     * It is a method for calling the automatic audio saving function.
     * @param v
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onClickAddAudio(View v){
        type = "audio";
        Log.i("TryAutomatically", "Automatic audio saving function is called!");
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("audio/*");
        startActivityForResult(intent, 1);
    }

    /**
     * This is a method for activity, that adds the media automatically to database
     * @param requestCode Override from onActivityResult()
     * @param resultCode Override from onActivityResult()
     * @param intent Override from onActivityResult()
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        db = getBaseContext().openOrCreateDatabase("DigitalDiary.db", MODE_PRIVATE, null);
        if(requestCode == 1){
            if(resultCode == Activity.RESULT_OK) {
                final Uri uri = intent.getData();
                if(uri == null){
                  return;
                }
                getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                if (type.equals("photo")) {
                    String proof = proofPhoto(uri);
                    if(proof != null){
                        Log.i("TryAutomatically", "The photo " + uri.toString() + " has been already saved!");
                        Toast.makeText(TryAutomatically.this, "You already have this photo in " + proof, Toast.LENGTH_LONG).show();
                        return ;
                    }
                    InputStream in = null;
                    try {
                        in = getContentResolver().openInputStream(uri);
                    } catch (FileNotFoundException e) {
                        Log.e("TryAutomatically", "File not found exception!");
                        e.printStackTrace();
                    }
                    meta = new PhotoManager(in);
                    final double[] latlng = meta.getLatLng();
                    try {
                        proofBitmapFaces(uri);
                    } catch (IOException e) {
                        Log.e("TryAutomatically", "IO exception!");
                        e.printStackTrace();
                    }
                    if (latlng != null) {
                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    List<String> result;
                                    int counter = 4;
                                    do {
                                        result = internet.getCountryAndCityName(latlng[0], latlng[1]);
                                        counter = counter - 1;
                                    } while(result.size() == 0 && counter > 0);
                                    if (result.size() != 0) {
                                        String country = result.get(0);
                                        country = country.substring(0, 1).toUpperCase() + country.substring(1).toLowerCase();
                                        String city = result.get(1);
                                        if(meta.getDate() != null){
                                            if(city != null) {
                                                db.execSQL("INSERT INTO date (city, date) VALUES ('" + city + "', '" + meta.getDate() + "');");
                                            }
                                        }
                                        if(city == null){
                                            Log.e("TryAutomatically", "City is null!");
                                            return;
                                        }
                                        city = city.substring(0, 1).toUpperCase() + city.substring(1).toLowerCase();
                                        saveByCity(uri, country, city);
                                        final String finalCity = city;
                                        final String finalCountry = country;
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(TryAutomatically.this, "The image is saved to "+ finalCountry +", " + finalCity, Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    } else if(meta.getDate() != null) {
                                        final String check = savePhotoByDate(uri, meta.getDate());
                                        if(check != null){
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(TryAutomatically.this, "According to date this image is saved to "+ check, Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        } else {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(TryAutomatically.this, "This image can not be saved automatically, you should try it manually", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    } else {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(TryAutomatically.this, "This image can not be saved automatically, you should try it manually", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                } catch (IOException e) {
                                    Log.e("TryAutomatically", "IO Exception!");
                                    e.printStackTrace();
                                } catch (ParseException e) {
                                    Log.e("TryAutomatically", "Parse Exception!");
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(TryAutomatically.this, "This image can not be saved automatical, you should try it manually", Toast.LENGTH_LONG).show();
                    }
                } else if (type.equals("video") || type.equals("audio")){
                    String proof = proofMultimedia(uri);
                    if(proof != null){
                        Toast.makeText(TryAutomatically.this, "You already have this " + type + " in " + proof, Toast.LENGTH_LONG).show();
                        return ;
                    }
                    MultimediaMetaDataManager media = new MultimediaMetaDataManager();
                    String date = media.takeMultimediaDate(getBaseContext(),uri);
                    if(date != null){
                        String city = saveMultimediaByDate(uri, date, type);
                        if(city != null){
                            Toast.makeText(TryAutomatically.this, "According to date this multimedia is saved to "+ city, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(TryAutomatically.this, "This multimedia can not be saved automatical, you should try it manually", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(TryAutomatically.this, "This multimedia can not be saved automatical, you should try it manually", Toast.LENGTH_LONG).show();
                    }
                }
            }
        } else {
            Log.e("TryAutomatically", "Problems with request");
            Toast.makeText(TryAutomatically.this, "Problems with request!", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * This is a method for saving photos by GPS tags.
     * @param uri It is the URI of the photo that the user wants to save.
     * @param country It is the country where the user wants to save the photo.
     * @param city It is the city where the user wants to save the photo.
     * @throws IOException
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void saveByCity(Uri uri, String country, String city) throws IOException {
        getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if(proofBitmapFaces(uri)){
            String faces = "faces";
            db.execSQL("INSERT INTO images (city, path) VALUES ('" + faces + "', '" + uri.toString() + "');");
            Log.i("TryAutomatically", "The photo was saved to faces!");
        }
        Cursor countryCursor = db.rawQuery("SELECT * FROM countries", null);
        if (countryCursor.moveToFirst()) {
            do {
                if(countryCursor.getString(2).equals(city)){
                    db.execSQL("INSERT INTO images (city, path) VALUES ('" + city + "', '" + uri.toString() + "');");
                    Log.i("TryAutomatically", "The photo was saved to " + country + " ," + city);
                    return;
                }
            } while (countryCursor.moveToNext());
        }
        db.execSQL("INSERT INTO countries (country, city) VALUES ('" + country + "', '" + city + "');");
        Log.i("TryAutomatically", "The page " + country + ", " + city + " was created!");
        db.execSQL("DROP TABLE lasttrip");
        db.execSQL("CREATE TABLE IF NOT EXISTS lasttrip (country TEXT, city TEXT)");
        db.execSQL("INSERT INTO lasttrip VALUES ('" + country + "', '" + city + "');");
        db.execSQL("INSERT INTO images (city, path) VALUES ('" + city + "', '" + uri.toString() + "');");
        Log.i("TryAutomatically", "The photo was saved to " + country + " ," + city);
        countryCursor.close();
    }

    /**
     * This is a method for saving photos by date.
     * @param uri It is the URI of the photo that the user wants to save.
     * @param date That's the date the photo was taken.
     * @return city City, where the photo was saved, or null if the city wasn't found
     * @throws IOException
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String savePhotoByDate(Uri uri, String date) throws IOException {
        Cursor dateCursor = db.rawQuery("SELECT * FROM date", null);
        if (dateCursor.moveToFirst()) {
            do {
                if(dateCursor.getString(2).equals(date)){
                    getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    if(proofBitmapFaces(uri)){
                        String faces = "faces";
                        db.execSQL("INSERT INTO images (city, path) VALUES ('" + faces + "', '" + uri.toString() + "');");
                        Log.i("TryAutomatically", "The photo was saved to faces!");
                    }
                    db.execSQL("INSERT INTO images (city, path) VALUES ('" + dateCursor.getString(1) + "', '" + uri.toString() + "');");
                    String city = dateCursor.getString(1);
                    Log.i("TryAutomatically", "The photo was saved to " + city);
                    dateCursor.close();
                    return city;
                }
            } while (dateCursor.moveToNext());
        }
        dateCursor.close();
        Log.i("TryAutomatically", "This date " + date + " is not in the database!");
        return null;
    }

    /**
     * This is a method for saving multimedia by date.
     * @param uri It is the URI of the multimedia that the user wants to save.
     * @param date That's the date the multimedia was taken.
     * @param type This parameter shows that it's audio or video.
     * @return City, where the multimedia was saved, or null if the city wasn't found
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String saveMultimediaByDate(Uri uri, String date, String type){
        Cursor dateCursor = db.rawQuery("SELECT * FROM date", null);
        if (dateCursor.moveToFirst()) {
            do {
                if(dateCursor.getString(2).equals(date)){
                    getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    if(type.equals("video")) {
                        db.execSQL("INSERT INTO videos (city, path) VALUES ('" + dateCursor.getString(1) + "', '" + uri.toString() + "');");
                        Log.i("TryAutomatically", "The video was saved to " + dateCursor.getString(1));
                    } else if(type.equals("audio")) {
                        db.execSQL("INSERT INTO audios (city, path) VALUES ('" + dateCursor.getString(1) + "', '" + uri.toString() + "');");
                        Log.i("TryAutomatically", "The audio was saved to " + dateCursor.getString(1));
                    }
                    String city = dateCursor.getString(1);
                    dateCursor.close();
                    return city;
                }
            } while (dateCursor.moveToNext());
        }
        dateCursor.close();
        Log.i("TryAutomatically", "This date " + date + " is not in the database!");
        return null;
    }

    /**
     * This method checks if there are faces in the photo or not.
     * @param uri This is the URI of the photo that the user wants to check for faces.
     * @return True is faces were found, else false
     * @throws IOException
     */
    public boolean proofBitmapFaces(Uri uri) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        return meta.proofFaces(bitmap);
    }

    /**
     * This is a function that allows user to go to the main activity
     * @param v
     */
    public void onClickBack(View v){
        Intent intent = new Intent(TryAutomatically.this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * This function allows the user to check whether a certain photo has been saved in the system before or not.
     * @param uri This is the URI of the photo that the user wants to check.
     * @return The city where this picture was already saved, or null
     */
    public String proofPhoto(Uri uri){
        Cursor photoCursor;
        photoCursor = db.rawQuery("SELECT * FROM images", null);
        if (photoCursor.moveToFirst()) {
            do {
                if(photoCursor.getString(2).equals(uri.toString()) && !photoCursor.getString(1).equals("faces")){
                    Log.i("TryAutomatically", "The photo " + uri.toString() + " was already saved in " + photoCursor.getString(1));
                    return photoCursor.getString(1);
                }
            } while (photoCursor.moveToNext());
        }
        Log.i("TryAutomatically", "The photo " + uri.toString() + " has not yet been saved in the system!");
        photoCursor.close();
        return null;
    }

    /**
     * This function allows the user to check whether a certain multimedia has been saved in the system before or not.
     * @param uri This is the URI of the multimedia that the user wants to check.
     * @return The city where this multimedia was already saved, or null
     */
    public String proofMultimedia(Uri uri){
        Cursor multimediaCursor;
        if(type.equals("video")) {
            multimediaCursor = db.rawQuery("SELECT * FROM videos", null);
        } else {
            multimediaCursor = db.rawQuery("SELECT * FROM audios", null);
        }
        if (multimediaCursor.moveToFirst()) {
            do {
                if(multimediaCursor.getString(2).equals(uri.toString())){
                    Log.i("TryAutomatically", "The multimedia " + uri.toString() + " was already saved in " + multimediaCursor.getString(1));
                    return multimediaCursor.getString(1);
                }
            } while (multimediaCursor.moveToNext());
        }
        multimediaCursor.close();
        Log.i("TryAutomatically", "The multimedia " + uri.toString() + " has not yet been saved in the system!");
        return null;
    }
    
}
