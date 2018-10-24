import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
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

    private static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }

    public static void main(String[] args) throws IOException {
        Dotenv dotenv = Dotenv.load();
        String TOKEN = dotenv.get("TOKEN");
        String Username = dotenv.get("BOT_USRNAME");
        String PUBLIC_API = dotenv.get("PUBLIC_API_KEY");
        String PRVT_API = dotenv.get(("PRIVATE_API_KEY"));
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        String hash = getMd5(timeStamp+PRVT_API+PUBLIC_API);

        try {
            String json = readUrl("http://gateway.marvel.com/v1/public/comics?ts="+timeStamp+"&apikey="+PUBLIC_API+"&hash="+hash);
            System.out.println(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            String json2 = readUrl("https://gateway.marvel.com:443/v1/public/characters?name=iron%20man&ts="+timeStamp+"&apikey="+PUBLIC_API+"&hash="+hash);
            System.out.println(json2);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
