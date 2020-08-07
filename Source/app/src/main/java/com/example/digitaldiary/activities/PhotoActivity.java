package com.example.digitaldiary.activities;

import androidx.annotation.RequiresApi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.digitaldiary.R;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import metadata.PhotoManager;


public class PhotoActivity extends Activity {
    private String country, city;
    private SQLiteDatabase db;
    private ImageView imageView;
    private List<Uri> uris = new ArrayList<>();
    private int uri_actual_position;
    private TextView empty_country;
    private Uri last_uri;
    private PhotoManager meta;

    /**
     * This is a method to create the photo activity. It takes out images suitable for this journey
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        db = getBaseContext().openOrCreateDatabase("DigitalDiary.db", MODE_PRIVATE, null);
        country = getIntent().getStringExtra("country");
        city = getIntent().getStringExtra("city");
        String text = country+", "+city;
        TextView actual_country = findViewById(R.id.textView2);
        if(country == null){
            actual_country.setText(city.toUpperCase());
            Button cityButton = findViewById(R.id.button14);
            cityButton.setVisibility(View.GONE);
        } else
            actual_country.setText(text);
        imageView = findViewById(R.id.imageView3);
        Cursor imageCursor = db.rawQuery("SELECT * FROM images", null);
        if (imageCursor.moveToFirst()) {
            do {
                if(imageCursor.getString(1).equals(city)){
                    uris.add(Uri.parse(imageCursor.getString(2)));
                }
            } while (imageCursor.moveToNext());
        }
        if(uris.size() > 0){
            uri_actual_position = 0;
            try {
                this.setImageView(uris.get(0));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            empty_country = findViewById(R.id.textView3);
            empty_country.setText("You don't have photos yet!");
        }
        Log.i("PhotoActivity", "The photo activity is created!");
        imageCursor.close();
    }

    /**
     * It is a method to add the new image from the phone memory to this app
     * @param v
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onClickAdd(View v){
        Log.i("PhotoActivity", "The add function was called!");
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }


    /**
     * This is a method to open the information page for a current photo
     * @param v
     */
    public void onClickInfo(View v){
        if(last_uri != null) {
            Intent intent = new Intent(PhotoActivity.this, InformationActivity.class);
            intent.putExtra("uri", last_uri.toString());
            intent.putExtra("type", "photo");
            Log.i("PhotoActivity", "The information page for a photo " + last_uri.toString() + " was called!");
            startActivity(intent);
        }
        else {
            Log.i("PhotoActivity", "There is no photo to analyze!");
            Toast.makeText(PhotoActivity.this, "You don't have photo to analyze!", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * This is a method for the gallery, it shows the previous photo
     * @param v
     */
    public void onClickPrevious(View v){
        if(uris.size() == 0){
            Toast.makeText(PhotoActivity.this, "You don't have photos in this journey!", Toast.LENGTH_LONG).show();
            return ;
        }
        if((uri_actual_position - 1) < 0){
            uri_actual_position = uris.size() - 1;
            try {
                Log.i("PhotoActivity", "The photo on position " + uri_actual_position + " was shown in the gallery");
                this.setImageView(uris.get(uri_actual_position));
            } catch (IOException e) {
                Log.e("PhotoActivity", "Problems with IO in ImageView!");
                e.printStackTrace();
            }
        } else {
            uri_actual_position = uri_actual_position - 1;
            try {
                Log.i("PhotoActivity", "The photo on position " + uri_actual_position + " was shown in the gallery");
                this.setImageView(uris.get(uri_actual_position));
            } catch (IOException e) {
                Log.e("PhotoActivity", "Problems with IO in ImageView!");
                e.printStackTrace();
            }
        }
    }

    /**
     * This is a method for the gallery, it shows the next photo
     * @param v
     */
    public void onClickNext(View v){
        if(uris.size() == 0){
            Toast.makeText(PhotoActivity.this, "You don't have photos in this journey!", Toast.LENGTH_LONG).show();
            return ;
        }
        if((uri_actual_position + 1) < uris.size()){
            uri_actual_position = uri_actual_position + 1;
            try {
                Log.i("PhotoActivity", "The photo on position " + uri_actual_position + " was shown in the gallery");
                this.setImageView(uris.get(uri_actual_position));
            } catch (IOException e) {
                Log.e("PhotoActivity", "Problems with IO in ImageView!");
                e.printStackTrace();
            }
        } else {
            uri_actual_position = 0;
            try {
                Log.i("PhotoActivity", "The photo on position " + uri_actual_position + " was shown in the gallery");
                this.setImageView(uris.get(uri_actual_position));
            } catch (IOException e) {
                Log.e("PhotoActivity", "Problems with IO in ImageView!");
                e.printStackTrace();
            }
        }
    }

    /**
     * This is a method to delete photo
     * @param v
     */
    public void onClickDelete(View v){
        if(uris.size() == 0 || last_uri == null){
            Toast.makeText(PhotoActivity.this, "You don't have photos in this journey!", Toast.LENGTH_LONG).show();
            return ;
        }

        AlertDialog alert = new AlertDialog.Builder(this).setTitle("Delete")
                .setMessage("Are you sure you want to delete this picture?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog_interface, int i) {
                        uris.remove(last_uri);
                        db = getBaseContext().openOrCreateDatabase("DigitalDiary.db", MODE_PRIVATE, null);
                        db.delete("images", " city = ? AND path = ? ",
                                new String[] {city, last_uri.toString()+""});
                        Log.i("PhotoActivity", "The photo  " + last_uri.toString() + " was deleted");
                        db.close();
                        if (uris.size() == 0){
                            imageView.setImageResource(R.drawable.white);
                        } else{
                            try {
                                Log.i("PhotoActivity", "The photo  " + last_uri.toString() + " was deleted");
                                setImageView(uris.get(0));
                            } catch (IOException e) {
                                Log.e("PhotoActivity", "Problems with IO in ImageView!");
                                e.printStackTrace();
                            }
                        }
                        dialog_interface.cancel();
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
     * This is a function for activity, that adds the photo to database
     * @param requestCode Override from onActivityResult()
     * @param resultCode Override from onActivityResult()
     * @param intent Override from onActivityResult()
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (empty_country != null) {
            empty_country.setVisibility(View.GONE);
        }
        if(requestCode == 1){
            if(resultCode == Activity.RESULT_OK){
                Uri image = intent.getData();
                InputStream in = null;
                if(image == null){
                    Log.e("PhotoActivity", "The URI is null!");
                    return ;
                }
                try {
                    in = getContentResolver().openInputStream(image);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                meta = new PhotoManager(in);
                String proof = proofPhoto(image);
                if(proof != null){
                    Log.i("PhotoActivity", "The photo  " + last_uri.toString() + " is already saved!");
                    Toast.makeText(PhotoActivity.this, "You already have this photo in " + proof, Toast.LENGTH_LONG).show();
                    return ;
                }
                getContentResolver().takePersistableUriPermission(image, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                try {
                    if(proofBitmapFaces(image)){
                        String faces = "faces";
                        db.execSQL("INSERT INTO images (city, path) VALUES ('" + faces + "', '" + image.toString() + "');");
                        Log.i("PhotoActivity", "The photo is saved to faces!");
                    }
                } catch (IOException e) {
                    Log.e("PhotoActivity", "The problems with IO!");
                    e.printStackTrace();
                }
                db.execSQL("INSERT INTO images (city, path) VALUES ('" + city + "', '" + image.toString() + "');");
                Log.i("PhotoActivity", "The photo is saved!");
                db.execSQL("INSERT INTO date (city, date) VALUES ('" + city + "', '" + meta.getDate() + "');");
                Log.i("PhotoActivity", "The date of the photo is saved!");
                uris.add(image);
                uri_actual_position = uris.size() - 1;
                try {
                    Log.i("PhotoActivity", "The photo on position " + uri_actual_position + " was shown in the gallery!");
                    this.setImageView(image);
                } catch (IOException e) {
                    Log.e("PhotoActivity", "Problems with IO in ImageView!");
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(PhotoActivity.this, "Problems with access to memory!", Toast.LENGTH_LONG).show();
                Log.e("PhotoActivity", "Problems with access to memory!");
            }
        } else {
            Toast.makeText(PhotoActivity.this, "Problems with request!", Toast.LENGTH_LONG).show();
            Log.e("PhotoActivity", "Problems with request!");
        }
    }

    /**
     * This is a method to set image to image view from its URI
     * @param uri This is a uri of an image, which user wants to show in image view
     * @throws IOException
     */
    public void setImageView(Uri uri) throws IOException{
        last_uri = uri;
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            imageView.setImageBitmap(bitmap);
        } catch(Exception e){
            imageView.setImageResource(R.drawable.white);
            Toast.makeText(PhotoActivity.this, "This photo was deleted or removed!", Toast.LENGTH_LONG).show();
            Log.i("PhotoActivity", "This photo was deleted or removed!");
            db.delete("images", " city = ? AND path = ? ",
                    new String[] {city, last_uri.toString()+""});
            uris.remove(last_uri);
        }
    }

    /**
     * This is a method to proof faces on the photo
     * @param uri This is a uri of an image, on which user wants to search faces
     * @return true - if faces were found, else false
     * @throws IOException
     */
    public boolean proofBitmapFaces(Uri uri) throws IOException {
        Log.i("PhotoActivity", "The faces on the photo" + uri.toString() + " were proofed!");
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        return meta.proofFaces(bitmap);
    }

    /**
     * This is a method to go to a country page
     * @param v
     */
    public void clickCityPage(View v){
        Intent intent;
        intent = new Intent(PhotoActivity.this, CountryPage.class);
        intent.putExtra("country", country);
        intent.putExtra("city",city);
        Log.i("PhotoActivity", "The country page were called!");
        startActivity(intent);
    }

    /**
     * It is a method to determine whether or not this photo was previously stored in the system.
     * @param uri The URI of the photo that the user wants to check
     * @return If the photo was found, the name of the city where it was saved, otherwise null
     */
    public String proofPhoto(Uri uri){
        Cursor photoCursor;
        photoCursor = db.rawQuery("SELECT * FROM images", null);
        if (photoCursor.moveToFirst()) {
            do {
                if(photoCursor.getString(2).equals(uri.toString()) && !photoCursor.getString(1).equals("faces")){
                    return photoCursor.getString(1);
                }
            } while (photoCursor.moveToNext());
        }
        photoCursor.close();
        return null;
    }

    /**
     * The method to close the database, when user leaves this gallery
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
        Log.i("PhotoActivity", "Database was closed!");
    }
}





