package internet;
import android.os.Build;
import android.util.Log;


import androidx.annotation.RequiresApi;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class InternetCommunication {

    /**
     * This is a function to take code of a country(for example AT for Austria)
     * @param country whose code do we need
     * @return code, if it is available, else null
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String getCountryCode(String country){
        URL REST_COUNTRIES = null;
        try {
            REST_COUNTRIES = new URL("https://restcountries.eu/rest/v2/name/" + country);
        } catch (MalformedURLException e) {
            Log.e("InternetCommunication", "URL Exception!");
            e.printStackTrace();
        }
        HttpsURLConnection connection = null;
        try {
            if(REST_COUNTRIES == null){
                Log.e("InternetCommunication", "URL is null!");
                return null;
            }
            connection = (HttpsURLConnection) REST_COUNTRIES.openConnection();
        } catch (IOException e) {
            Log.e("InternetCommunication", "IO Exception!");
            e.printStackTrace();
        }

        InputStream response = null;
        try {
            if(connection == null){
                Log.e("InternetCommunication", "HTTPS connection is null!");
                return null;
            }
            response = connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONParser jsonParser = new JSONParser();
        Object information;
        String alpha_code;
        try {
            if(response == null){
                Log.e("InternetCommunication", "Input Stream is null!");
                return null;
            }
            JSONArray jsonArray = (JSONArray) jsonParser.parse(new InputStreamReader(response, StandardCharsets.UTF_8));
            information = jsonArray.get(0);
            JSONObject jsonObject= (JSONObject) information;
            alpha_code = (String)jsonObject.get("alpha2Code");
            connection.disconnect();
            Log.i("InternetCommunication", "The alpha code for " + country + " was found!");
            return alpha_code;
        } catch (IOException e) {
            Log.e("InternetCommunication", "IO Exception!");
            e.printStackTrace();
        } catch (ParseException e) {
            Log.e("InternetCommunication", "Parse exception!");
            e.printStackTrace();
        }
        connection.disconnect();
        Log.i("InternetCommunication", "The alpha code for " + country + " was not found!");
        return null;
    }

    /**
     * This is a function to get the information about the specific country
     * @param country That's the country the user wants to get information about.
     * @return information, if it is available, else null
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String getCountryInformation(String country){
        URL REST_COUNTRIES = null;
        try {
            REST_COUNTRIES = new URL("https://restcountries.eu/rest/v2/name/" + country);
        } catch (MalformedURLException e) {
            Log.e("InternetCommunication", "URL Exception!");
            e.printStackTrace();
        }
        HttpsURLConnection connection = null;
        try {
            if(REST_COUNTRIES == null){
                Log.e("InternetCommunication", "URL is null!");
                return null;
            }
            connection = (HttpsURLConnection) REST_COUNTRIES.openConnection();
        } catch (IOException e) {
            Log.e("InternetCommunication", "IO Exception!");
            e.printStackTrace();
        }

        InputStream response = null;
        try {
            if(connection == null){
                Log.e("InternetCommunication", "HTTPS connection Exception!");
                return null;
            }
            response = connection.getInputStream();
        } catch (IOException e) {
            Log.e("InternetCommunication", "IO Exception!");
            e.printStackTrace();
        }
        JSONParser jsonParser = new JSONParser();
        Object information;
        StringBuilder answer = new StringBuilder("Capital: ");
        try {
            if(response == null){
                Log.e("InternetCommunication", "Input Stream is null!");
                return null;
            }
            JSONArray jsonArray = (JSONArray) jsonParser.parse(new InputStreamReader(response, StandardCharsets.UTF_8));
            information = jsonArray.get(0);
            JSONObject jsonObject= (JSONObject) information;
            answer.append(jsonObject.get("capital")).append("\n\n");
            answer.append("Region: ").append(jsonObject.get("region")).append("\n\n");
            answer.append("Subregion: ").append(jsonObject.get("subregion")).append("\n\n");
            answer.append("Population: ").append(jsonObject.get("population")).append("\n\n");
            answer.append("Native name: ").append(jsonObject.get("nativeName")).append("\n\n");
            JSONArray currencies = (JSONArray) jsonObject.get("currencies");
            if(currencies != null) {
                JSONObject currency = (JSONObject) currencies.get(0);
                answer.append("Currency: ").append(currency.get("name")).append("\n\n");
            }
            JSONArray languages = (JSONArray) jsonObject.get("languages");
            answer.append("Language: ");
            int i = 0;
            if(languages != null) {
                do {
                    JSONObject language = (JSONObject) languages.get(i);
                    answer.append(language.get("name")).append(" ");
                    i++;
                } while (i < languages.size());
            }
            answer.append("\n\n");
            connection.disconnect();
            Log.i("InternetCommunication", "Information about the " + country + " was found!");
            return answer.toString();
        } catch (IOException e) {
            Log.e("InternetCommunication", "IO Exception!");
            e.printStackTrace();
        } catch (ParseException e) {
            Log.e("InternetCommunication", "Parse Exception!");
            e.printStackTrace();
        }
        connection.disconnect();
        Log.i("InternetCommunication", "Information about the " + country + " was not found!");
        return null;
    }

    /**
     * This is a program to get the name of a country and city from their GPS coordinates
     * @param lat latitude (GPS)
     * @param lng longitude (GPS)
     * @return a list containing the name of a country and city, or an empty list if the names could not be found
     * @throws IOException
     * @throws ParseException
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public List<String> getCountryAndCityName(double lat, double lng) throws IOException, ParseException {
        URL REST_GPS = null;
        List<String> result = new ArrayList<>();
        try {
            REST_GPS = new URL("https://geocode.xyz/" + lat + "," + lng + "?geoit=json");
        } catch (MalformedURLException e) {
            Log.e("InternetCommunication", "URL Exception!");
            e.printStackTrace();
        }
        HttpsURLConnection connection = null;
        try {
            if(REST_GPS == null){
                Log.e("InternetCommunication", "Problems with URL!");
                return result;
            }
            connection = (HttpsURLConnection) REST_GPS.openConnection();
        } catch (IOException e) {
            Log.e("InternetCommunication", "IO Exception!");
            e.printStackTrace();
        }
        InputStream response = null;
        try {
            if(connection == null){
                Log.e("InternetCommunication", "Problems with HTTPS Connection!");
                return result;
            }
            response = connection.getInputStream();
        } catch (IOException e) {
            Log.e("InternetCommunication", "IO Exception!");
            e.printStackTrace();
        }
        JSONParser jsonParser = new JSONParser();
        if(response != null) {
            JSONObject information = (JSONObject) jsonParser.parse(new InputStreamReader(response, StandardCharsets.UTF_8));
            result.add(0, (String) information.get("country"));
            result.add(1, (String) information.get("city"));
            Log.i("InternetCommunication","The information from GPS tags is found!");
        }
        if(response != null) {
            response.close();
        }
        connection.disconnect();
        return result;
    }

    /**
     * This function makes it possible to obtain detailed information about the photo by its GPS tags (location, region, etc.)
     * @param lat latitude (GPS)
     * @param lng longitude(GPS)
     * @return a list containing the information, or an empty list if the information could not be found
     * @throws IOException
     * @throws ParseException
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public List<String> getPhotoInformation(double lat, double lng) throws IOException, ParseException {
        URL REST_GPS = null;
        List<String> result = new ArrayList<>();
        try {
            REST_GPS = new URL("https://geocode.xyz/" + lat + "," + lng + "?geoit=json");
        } catch (MalformedURLException e) {
            Log.e("InternetCommunication", "URL Exception!");
            e.printStackTrace();
        }
        HttpsURLConnection connection = null;
        try {
            if(REST_GPS == null){
                Log.e("InternetCommunication", "URL is null!");
                return result;
            }
            connection = (HttpsURLConnection) REST_GPS.openConnection();
        } catch (IOException e) {
            Log.e("InternetCommunication", "IO Exception!");
            e.printStackTrace();
        }
        InputStream response = null;
        try {
            if(connection == null){
                Log.e("InternetCommunication", "Prob;ems with HTTPS connection!");
                return result;
            }
            response = connection.getInputStream();
        } catch (IOException e) {
            Log.e("InternetCommunication", "IO Exception!");
            e.printStackTrace();
        }
        JSONParser jsonParser = new JSONParser();
        if(response != null) {
            JSONObject information = (JSONObject) jsonParser.parse(new InputStreamReader(response, StandardCharsets.UTF_8));
            result.add(0, (String) information.get("staddress"));
            result.add(1, (String) information.get("region"));
            result.add(2, (String) information.get("state"));
            Log.i("InternetCommunication","The information from GPS tags is found!");
        }
        if(response != null) {
            response.close();
        }
        connection.disconnect();
        return result;
    }

    /**
     * This method proofs existence of the country
     * @param country which we want to proof
     * @return true, if it exists, else false
     */
    public boolean proofIfCountryExists(String country) {
        URL REST_COUNTRIES = null;
        try {
            REST_COUNTRIES = new URL("https://restcountries.eu/rest/v2/name/" + country);
        } catch (MalformedURLException e) {
            Log.e("InternetCommunication","URL Exception!");
            e.printStackTrace();
        }
        HttpsURLConnection connection = null;
        try {
            if(REST_COUNTRIES == null){
                Log.e("InternetCommunication", "URL is null!");
            }
            connection = (HttpsURLConnection) REST_COUNTRIES.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (connection.getResponseCode() == 200) {
                connection.disconnect();
                Log.i("InternetCommunication", "The country " + country + " exists!");
                return true;
            } else {
                connection.disconnect();
                Log.i("InternetCommunication", "The country " + country + " not exists!");
                return false;

            }
        } catch (IOException e) {
            Log.e("InternetCommunication", "IO Exception");
            e.printStackTrace();
        }
        Log.i("InternetCommunication", "The country " + country + " not exists!");
        return false;
    }

    /**
     * This is a method to take the flag of the country
     * @param alphaCode of the country, which flag do we need
     * @return InputStream with the passing flag
     */
    public InputStream getFlag(String alphaCode){
        HttpsURLConnection connection = null;
        URL probe = null;
        try {
            probe = new URL("https://www.countryflags.io/"+alphaCode+"/shiny/64.png");
        } catch (MalformedURLException e) {
            Log.i("InternetCommunication", "URL Exception!");
            e.printStackTrace();
        }
        try {
            if(probe == null){
                Log.e("InternetCommunication", "URL is null!");
            }
            connection = (HttpsURLConnection) probe.openConnection();
        } catch (IOException e) {
            Log.e("InternetCommunication", "IO Exception!");
            e.printStackTrace();
        }
        InputStream input = null;
        try {
            if(connection == null){
                Log.e("InternetCommunication", "Problems with HTTPS connection!");
            }
            input = connection.getInputStream();
        } catch (IOException e) {
            Log.e("InternetCommunication", "IO Exception!");
            e.printStackTrace();
        }
        Log.i("InternetCommunication", "The flag for " + alphaCode + " is found!");
        return input;
    }
}
