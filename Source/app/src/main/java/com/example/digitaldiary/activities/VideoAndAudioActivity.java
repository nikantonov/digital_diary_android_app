package com.example.digitaldiary.activities;

import androidx.annotation.RequiresApi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import com.example.digitaldiary.R;

import java.util.ArrayList;
import java.util.List;
import metadata.MultimediaMetaDataManager;

import static android.view.View.GONE;

public class VideoAndAudioActivity extends Activity {
    private String type;
    private String country, city;
    private SQLiteDatabase db;
    private VideoView videoView;
    private int uri_actual_position;
    private Uri last_uri;
    private List<Uri> uris = new ArrayList<>();
    private TextView audio_name;

    /**
     * This is a method to create the multimedia activity. It takes out audios/videos suitable for this journey
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        country = getIntent().getStringExtra("country");
        city = getIntent().getStringExtra("city");
        TextView actual_country = findViewById(R.id.textView2);
        audio_name = findViewById(R.id.textView4);
        type = getIntent().getStringExtra("type");
        String text = country+", "+city;
        actual_country.setText(text);
        db = getBaseContext().openOrCreateDatabase("DigitalDiary.db", MODE_PRIVATE, null);
        videoView = findViewById(R.id.videoView);
        if(type.equals("audio")){
            videoView.setBackgroundColor(Color.WHITE);
        }
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener(){
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                if(type.equals("video")){
                    Toast.makeText(VideoAndAudioActivity.this, "This video was deleted or removed!", Toast.LENGTH_LONG).show();
                    db.delete("videos", " city = ? AND path = ? ",
                            new String[] {city, last_uri.toString()+""});
                } else{
                    Toast.makeText(VideoAndAudioActivity.this, "This audio was deleted or removed!", Toast.LENGTH_LONG).show();
                    db.delete("audios", " city = ? AND path = ? ",
                            new String[] {city, last_uri.toString()+""});
                }
                uris.remove(last_uri);
                if (uris.size() != 0){
                    setVideoView(uris.get(0));
                }
                return false;
            }
        });
        MediaController media = new MediaController(this);
        media.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextVideo(v);
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousVideo(v);
            }
        });
        videoView.setMediaController(media);
        media.setMediaPlayer(videoView);
        media.show(0);
        Cursor videoCursor;
        if(type.equals("video")) {
            videoCursor = db.rawQuery("SELECT * FROM videos", null);
        } else {
            videoCursor = db.rawQuery("SELECT * FROM audios", null);
        }
        if (videoCursor.moveToFirst()) {
            do {
                if(videoCursor.getString(1).equals(city)){
                    uris.add(Uri.parse(videoCursor.getString(2)));
                }
            } while (videoCursor.moveToNext());
        }

        if(uris.size() > 0){
            uri_actual_position = 0;
            setVideoView(uris.get(0));
        }
        Log.i("VideoAndAudioActivity", "The multimedia activity is created!");
        videoCursor.close();
    }

    /**
     * This is a function to move to the next media.
     * @param v
     */
    public void nextVideo(View v) {
        if(uris.size() == 0){
            if(type.equals("video")) {
                Toast.makeText(VideoAndAudioActivity.this, "You don't have videos in this journey!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(VideoAndAudioActivity.this, "You don't have audios in this journey!", Toast.LENGTH_LONG).show();
            }
            return ;
        }
        if((uri_actual_position + 1) < uris.size()){
            uri_actual_position = uri_actual_position + 1;
            setVideoView(uris.get(uri_actual_position));
        } else {
            uri_actual_position = 0;
            setVideoView(uris.get(uri_actual_position));
        }
    }

    /**
     * This is a function to call up a page with information about the media object.
     * @param v
     */
    public void onClickInfo(View v){
        if(last_uri != null) {
            Intent intent = new Intent(VideoAndAudioActivity.this, InformationActivity.class);
            intent.putExtra("uri", last_uri.toString());
            intent.putExtra("type", "multimedia");
            Log.i("VideoAndAudioActivity", "The information page for " + type + " " + last_uri.toString() + " was called!");
            startActivity(intent);
        }
        else
            Toast.makeText(VideoAndAudioActivity.this, "You don't have multimedia to analyze!", Toast.LENGTH_LONG).show();
    }

    /**
     * This is a function to move to the previous media.
     * @param v
     */
    public void previousVideo(View v) {
        if(uris.size() == 0){
            if(type.equals("video")) {
                Toast.makeText(VideoAndAudioActivity.this, "You don't have videos in this journey!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(VideoAndAudioActivity.this, "You don't have audios in this journey!", Toast.LENGTH_LONG).show();
            }
            return ;
        }
        if((uri_actual_position - 1) < 0){
            uri_actual_position = uris.size() - 1;
            setVideoView(uris.get(uri_actual_position));
        } else {
            uri_actual_position = uri_actual_position - 1;
            setVideoView(uris.get(uri_actual_position));
        }
    }

    /**
     * This is a function to delete the multimedia
     * @param v
     */
    public void onClickDelete(View v){
        if(uris.size() == 0 || last_uri == null){
            if(type.equals("video")) {
                Toast.makeText(VideoAndAudioActivity.this, "You don't have videos in this journey!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(VideoAndAudioActivity.this, "You don't have audios in this journey!", Toast.LENGTH_LONG).show();
            }
            return ;
        }

        AlertDialog alert = new AlertDialog.Builder(this).setTitle("Delete")
                .setMessage("Are you sure you want to delete this media?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog_interface, int i) {
                        uris.remove(last_uri);
                        db = getBaseContext().openOrCreateDatabase("DigitalDiary.db", MODE_PRIVATE, null);
                        if(type.equals("video")) {
                            db.delete("videos", " city = ? AND path = ? ",
                                    new String[]{city, last_uri.toString() + ""});
                            Log.i("VideoAndAudioActivity", "The video " + last_uri.toString() + " was deleted!");
                        } else {
                            db.delete("audios", " city = ? AND path = ? ",
                                    new String[]{city, last_uri.toString() + ""});
                            Log.i("VideoAndAudioActivity", "The audio " + last_uri.toString() + " was deleted!");
                        }
                        db.close();
                        if(uris.size() != 0){
                            last_uri = uris.get(0);
                            setVideoView(uris.get(0));
                        } else {
                            videoView.setVisibility(GONE);
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
     * This is a method that calls for a media saving function.
     * @param v
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onClickAdd(View v){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        if(type.equals("video")) {
            intent.setType("video/*");
        } else {
            intent.setType("audio/*");
        }
        startActivityForResult(intent, 1);
    }

    /**
     * This is a function for activity, that adds the multimedia to database
     * @param requestCode Override from onActivityResult()
     * @param resultCode Override from onActivityResult()
     * @param intent Override from onActivityResult()
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(requestCode == 1){
            if(resultCode == Activity.RESULT_OK){
                Uri video = intent.getData();
                if(video == null){
                    Log.e("VideoAndAudioActivity", "The uri is null!");
                    return;
                }
                getContentResolver().takePersistableUriPermission(video, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                db = getBaseContext().openOrCreateDatabase("DigitalDiary.db", MODE_PRIVATE, null);
                MultimediaMetaDataManager media = new MultimediaMetaDataManager();
                String date = media.takeMultimediaDate(getBaseContext(), video);
                if(type.equals("video")) {
                    String proof = proofMultimedia(video);
                    if(proof != null){
                        Toast.makeText(VideoAndAudioActivity.this, "You already have this video in " + proof, Toast.LENGTH_LONG).show();
                        return ;
                    }
                    db.execSQL("INSERT INTO videos (city, path) VALUES ('" + city + "', '" + video.toString() + "');");
                    Log.i("VideoAndAudioActivity", "The video " + video.toString() + " was saved to " + city);
                    if(date != null){
                        db.execSQL("INSERT INTO date (city, date) VALUES ('" + city + "', '" + date + "');");
                    }
                } else {
                    String proof = proofMultimedia(video);
                    if(proof != null){
                        Toast.makeText(VideoAndAudioActivity.this, "You already have this audio in " + proof, Toast.LENGTH_LONG).show();
                        return ;
                    }
                    db.execSQL("INSERT INTO audios (city, path) VALUES ('" + city + "', '" + video.toString() + "');");
                    Log.i("VideoAndAudioActivity", "The audio " + video.toString() + " was saved to " + city);
                    if(date != null){
                        db.execSQL("INSERT INTO date (city, date) VALUES ('" + city + "', '" + date + "');");
                    }
                }
                uris.add(video);
                uri_actual_position = uris.size() - 1;
                setVideoView(video);
                db.close();
            } else {
                Log.e("VideoAndAudioActivity", "Problems with access to memory!");
                Toast.makeText(VideoAndAudioActivity.this, "Problems with access to memory!", Toast.LENGTH_LONG).show();
            }
        } else {
            Log.e("VideoAndAudioActivity", "Problems with request!");
            Toast.makeText(VideoAndAudioActivity.this, "Problems with request!", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * This is a function for displaying media in the player.
     * @param uri This is the URI of the media that the user wants to play.
     */
    public void setVideoView(Uri uri) {
        if(type.equals("audio")) {
            String text = city + " Audio " + (uri_actual_position + 1);
            audio_name.setText(text);
        }
        last_uri = uri;
        Log.i("VideoAndAudioActivity", "Uri " + last_uri.toString() + " is playing!");
        videoView.setVideoURI(last_uri);
    }

    /**
     * It is a method to determine whether or not this multimedia was previously stored in the system.
     * @param uri The URI of the multimedia that the user wants to check
     * @return If the multimedia was found, the name of the city where it was saved, otherwise null
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
                    return multimediaCursor.getString(1);
                }
            } while (multimediaCursor.moveToNext());
        }
        multimediaCursor.close();
        return null;
    }

    /**
     * This is a method to go to a country page
     * @param v
     */
    public void clickCityPage(View v){
        Intent intent;
        intent = new Intent(VideoAndAudioActivity.this, CountryPage.class);
        intent.putExtra("country", country);
        intent.putExtra("city",city);
        Log.i("VideoAndAudioActivity", "The country page were called!");
        startActivity(intent);
    }

    /**
     * The method to close the database, when user leaves this gallery
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
        Log.i("VideoAndAudioActivity", "Database was closed!");
    }
}
