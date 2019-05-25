package project.weatherforecast;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpHelper {

    private static final int SUCCESS = 200;

    public JSONObject getJSONObjectFromURL(String urlString) throws IOException, JSONException {
        HttpURLConnection httpURLConnection;
        URL url = new URL(urlString);
        httpURLConnection = (HttpURLConnection) url.openConnection();

        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.setRequestProperty("Accept", "application/json");
        httpURLConnection.setReadTimeout(10000 /* mill        Log.d("URL: ", "REQUEST METHOD SENT");iseconds */ );
        httpURLConnection.setConnectTimeout(15000 /* milliseconds */ );

        try {
            httpURLConnection.connect();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        String new_line, jsonString;
        StringBuilder sb = new StringBuilder();

        while((new_line = br.readLine()) != null) {
            sb.append(new_line);
            sb.append("\n");
        }
        br.close();

        jsonString = sb.toString();
        Log.d("HTTP GET", "JSON obj-" + jsonString);

        int responseCode = httpURLConnection.getResponseCode();
        httpURLConnection.disconnect();
        return responseCode == SUCCESS  ? new JSONObject(jsonString) : null;

    }
}
