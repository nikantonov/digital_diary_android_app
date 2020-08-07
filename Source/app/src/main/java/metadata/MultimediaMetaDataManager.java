package metadata;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;

import java.util.concurrent.TimeUnit;

public class MultimediaMetaDataManager {
    private MediaMetadataRetriever media;

    /**
     * Constructor, creates metadata retriever
     */
    public MultimediaMetaDataManager(){
        media = new MediaMetadataRetriever();
        Log.i("MultimediaMetaManager", "Multimedia meta data manager was created");
    }

    /**
     * This is a function for determining the date of creation of multimedia
     * @param context context of an app
     * @param uri uri of the multimedia whose date user wants to get
     * @return the date, if it's found, otherwise null
     */
    public String takeMultimediaDate(Context context, Uri uri){
        media.setDataSource(context, uri);
        String date = media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE);
        if(date != null) {
            Log.i("MultimediaMetaManager", "Date for " + uri.toString() + " was found!");
            return date.substring(0, 4)+":"+date.substring(4, 6)+":"+date.substring(6, 8);
        }
        Log.i("MultimediaMetaManager", "Date for " + uri.toString() + " was not found!");
        return null;
    }

    /**
     * This is a function for determining the information about the specific multimedia
     * @param context context of an app
     * @param uri  uri of the multimedia whose information user wants to get
     * @return the information, if it's found, otherwise ""
     */
    public String takeMultimediaInformation(Context context, Uri uri){
        media.setDataSource(context, uri);
        String text = "";
        if(media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE) != null) {
            String date = media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE);
            text = "Date: " + date.substring(0, 4)+":"+date.substring(4, 6)+":"+date.substring(6, 8) + "\n\n";
        }
        if(media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_AUTHOR) != null) {
            text = text + "Author: " + media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_AUTHOR) + "\n\n";
        }
        if(media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) != null) {
            text = text + "Artist: " + media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) + "\n\n";
        }
        if(media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION) != null) {
            long minutes = TimeUnit.MILLISECONDS.toMinutes(Long.parseLong(media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)));
            long seconds = TimeUnit.MILLISECONDS.toSeconds(Long.parseLong(media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)));
            text = text + "Duration: " + minutes + " minutes " + seconds + " seconds " + "\n\n";
        }
        if(media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_LOCATION) != null) {
            text = text + "Location: " + media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_LOCATION) + "\n\n";
        }
        Log.i("MultimediaMetaManager", "The information about " + uri.toString() + " was found!");
        return text;
    }

}
