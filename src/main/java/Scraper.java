import io.github.cdimascio.dotenv.Dotenv; // To import environment variables from .env file

import org.json.JSONArray;  // To parse the JSON received from the API
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import java.math.BigInteger;  // Required for creating the MD5 hash

import java.security.MessageDigest;  // Required for creating the MD5 hash
import java.security.NoSuchAlgorithmException;

import java.text.SimpleDateFormat;  // To get the time-stamp
import java.util.Date;


public class Scraper {

    public static String getMd5(String input)
    {
        try {

            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // digest() method is called to calculate message digest
            //  of an input digest() return array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String JSONParsing(String charname) throws IOException {
        Dotenv dotenv = Dotenv.load();
        String PUBLIC_API = dotenv.get("PUBLIC_API_KEY");  // gets your Public API Key from the .env file
        String PRVT_API = dotenv.get(("PRIVATE_API_KEY"));  // gets your Private API Key from the .env file
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()); // returns the timestamp
        String hash = Scraper.getMd5(timeStamp + PRVT_API + PUBLIC_API);  // returns the hash string
        charname = charname.replaceAll("\\s+","%20");

        JSONObject json = JsonReader.readJsonFromUrl("https://gateway.marvel.com:443/v1/public/characters?name="
                +charname
                + "&ts=" + timeStamp
                + "&apikey=" + PUBLIC_API
                + "&hash=" + hash);

        try {
            JSONArray array = json.getJSONObject("data").getJSONArray("results");

            JSONObject comics = null;
            JSONObject series = null;
            JSONObject stories = null;
            JSONArray urls = null;
            String id = null;
            String name = null;
            String description = null;

            try {
                JSONArray jsonArray = array;

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    id = jsonObject1.optString("id");
                    name = jsonObject1.optString("name");
                    description = jsonObject1.optString("description");
                    comics = jsonObject1.optJSONObject("comics");
                    series = jsonObject1.optJSONObject("series");
                    stories = jsonObject1.optJSONObject(("stories"));
                    urls = jsonObject1.optJSONArray("urls");

                }
            } catch (
                    JSONException e) {
                e.printStackTrace();
            }

            String AvailableComics = "Comics: " + comics.optString("available");
            String AvailableSeries = "Series: " + series.optString("available");
            String AvailableStories = "Stories: " + stories.optString("available");
            String Wiki = "Wiki Link:\n" + urls.getJSONObject(1).optString("url");

            String toReturn = "ID: " + id
                    + "\n\nName: " + name + "\n\n"
                    + "Description: " + description + "\n\n"
                    + AvailableComics + "\n\n"
                    + AvailableSeries + "\n\n"
                    + AvailableStories + "\n\n"
                    + Wiki;

            return toReturn;
        }

        // NullPointerException may occur if the user passes a name not found in Marvel's database
        catch(NullPointerException e) {
            return "Sorry, you may have typed an incorrect name.";
        }
    }
}
