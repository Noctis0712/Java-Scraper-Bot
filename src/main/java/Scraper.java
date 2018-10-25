import io.github.cdimascio.dotenv.Dotenv;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import java.math.BigInteger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.text.SimpleDateFormat;
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
        String PUBLIC_API = dotenv.get("PUBLIC_API_KEY");
        String PRVT_API = dotenv.get(("PRIVATE_API_KEY"));
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        String hash = Scraper.getMd5(timeStamp + PRVT_API + PUBLIC_API);

        String charname2 = charname.replaceAll("\\s+","%20");

        JSONObject json = JsonReader.readJsonFromUrl("https://gateway.marvel.com:443/v1/public/characters?name=" +charname2 + "&ts=" + timeStamp + "&apikey=" + PUBLIC_API + "&hash=" + hash);

        String status = json.optString("code");
        int stat = Integer.parseInt(status);
        System.out.println(stat);
        try {
            JSONArray array = json.getJSONObject("data").getJSONArray("results");

            JSONObject comics = null;
            JSONObject series = null;
            JSONObject stories = null;
            JSONArray urls = null;
            String id = null;
            String name = null;
            try {
                JSONArray jsonArray = array;

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    id = jsonObject1.optString("id");
                    name = jsonObject1.optString("name");
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
            String wiki = "Wiki Link: " + urls.getJSONObject(1).optString("url");
            String toReturn = "ID: " + id + "\nName: " + name + "\n" +AvailableComics + "\n" + AvailableSeries + "\n" + AvailableStories + "\n" + wiki;

            return toReturn;
        }
        catch(NullPointerException e) {
            return "Sorry, you may have typed an incorrect name.";
        }
    }
}
