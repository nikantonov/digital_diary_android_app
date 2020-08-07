package metadata;

import android.graphics.Bitmap;
import android.media.FaceDetector;
import android.util.Log;

import androidx.exifinterface.media.ExifInterface;


import java.io.IOException;
import java.io.InputStream;

public class PhotoManager {
    private ExifInterface exifInterface;

    /**
     * Constructor, creates EXIF Interface for a specific InputStream
     * @param in InputStream
     */
    public PhotoManager(InputStream in){
        try {
            exifInterface = new ExifInterface(in);
            Log.i("PhotoManager", "EXIF Interface is created!");
        } catch (IOException e) {
            Log.e("PhotoManager", "IO Exception!");
            e.printStackTrace();
        }
    }

    /**
     * This is a function for obtaining latitude and longitude
     * @return double[] with latitude and longitude
     */
    public double[] getLatLng(){
        Log.i("PhotoManager", "Lat lng were returned!");
        return exifInterface.getLatLong();
    }

    /**
     * This is a facial search function in the picture
     * @param bitmap bitmap on which the user wants to find faces
     * @return true, if at least one face is found, otherwise false
     */
    public boolean proofFaces(Bitmap bitmap){
        Bitmap new_bitmap = bitmap.copy(Bitmap.Config.RGB_565, false);
        FaceDetector detector = new FaceDetector(new_bitmap.getWidth(), new_bitmap.getHeight(), 5);
        FaceDetector.Face[] faces = new FaceDetector.Face[5];
        int hom_many_faces = detector.findFaces(new_bitmap, faces);
        Log.i("PhotoManager", "Faces were analyzed!");
        return hom_many_faces > 0;
    }

    /**
     * This is a function for obtaining date
     * @return date
     */
    public String getDate(){
        Log.i("PhotoManager", "Date was returned!");
        return exifInterface.getAttribute(ExifInterface.TAG_GPS_DATESTAMP);
    }

    /**
     * This is a function for obtaining area information
     * @return area information
     */
    public String getAreaInformation() {
        Log.i("PhotoManager", "Area information was returned!");
        return exifInterface.getAttribute(ExifInterface.TAG_GPS_AREA_INFORMATION);
    }

    /**
     * This is a function for obtaining device information
     * @return device information
     */
    public String getDeviceInformation() {
        Log.i("PhotoManager", "Device information was returned!");
        return exifInterface.getAttribute(ExifInterface.TAG_DEVICE_SETTING_DESCRIPTION);
    }

    /**
     * This is a function for obtaining latitude
     * @return latitude
     */
    public String getLatitude() {
        Log.i("PhotoManager", "Latitude was returned!");
        return exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
    }

    /**
     * This is a function for obtaining longitude
     * @return longitude
     */
    public String getLongitude() {
        Log.i("PhotoManager", "Longitude was returned!");
        return exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
    }


}
