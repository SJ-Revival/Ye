package de.ye.app.utils;

import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.Map;

/**
 * Some JSON utility functions
 */
public class JsonParser {
    static InputStream is = null;
    static JSONObject jsonObject = null;
    static JSONArray jsonArray = null;
    static String jsonString = "";
    private static final String LOGTAG = JsonParser.class.getSimpleName();

    public JSONArray getJSONArrayFromUrl(String request_url, Map<String, String> map, String type) {
        Log.d(LOGTAG, "getJSONArrayFromUrl() " + request_url);
        String json = readJsonString(request_url, map, type);

        try {
            jsonArray = new JSONArray(json);
        } catch (JSONException e) {
            Log.e(LOGTAG, "Error parsing data " + e.toString());
        }
        return jsonArray;
    }

    public JSONArray getJSONArrayFromUrl(String request_url) {
        Log.d(LOGTAG, "getJSONArrayFromUrl() " + request_url);
        String json = readJsonString(request_url);

        // try to parse the string into a JSONArray
        try {
            jsonArray = new JSONArray(json);
        } catch (JSONException e) {
            Log.e(LOGTAG, "Error parsing data " + e.toString());
        }
        return jsonArray;
    }

    public JSONObject getJSONObjectFromUrl(String request_url) {
        Log.d(LOGTAG, "getJSONObjectFromUrl() " + request_url);
        String json = readJsonString(request_url);

        // try parse the string to a JSON object
        try {
            jsonObject = new JSONObject(json);
        } catch (JSONException e) {
            Log.e(LOGTAG, "Error parsing data " + e.toString());
        }

        return jsonObject;
    }

    public JSONObject getJSONObjectFromUrl(String request_url, Map<String, String> map, String type) {
        return getJSONObjectFromUrl(request_url);
    }

    private String readJsonString(String request_url, Map<String, String> map, String type) {
        return readJsonString(request_url);
    }

    private String readJsonString(String request_url) {
        try {
            //Log.d(LOGTAG, "readJsonString() TRY");
            DefaultHttpClient httpClient = new DefaultHttpClient();
            //Log.d(LOGTAG, "readJsonString() TRY httpClient ");

            HttpResponse httpResponse;

            HttpGet httpRequest = new HttpGet(request_url);
            httpResponse = httpClient.execute(httpRequest);

            int code = httpResponse.getStatusLine().getStatusCode();
            //Log.d(LOGTAG, "getJSONFromUrl() TRY Int: "+code);

            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                line += "\n";
                sb.append(line);
            }
            is.close();
            jsonString = sb.toString();
            //Log.d(LOGTAG, "JSON: "+jsonString);

        } catch (Exception e) {
            Log.e(LOGTAG, "Error converting result " + e.toString());
        }

        return jsonString;
    }

    public JSONObject readJsonObject(InputStream is) {
        String json;
        try {
            int size = is.available();
            byte[] buffer = new byte[size];
            int chars = is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            Log.d(LOGTAG, "Read " + chars + " characters of JSON.");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        try {
            return new JSONObject(json);
        } catch (JSONException e) {
            Log.e(LOGTAG, "ERROR creating JSON");
            e.printStackTrace();
            return null;
        }
    }

    public JSONArray readJsonArray(InputStream is) {
        String json;
        try {
            int size = is.available();
            byte[] buffer = new byte[size];
            int chars = is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            Log.d(LOGTAG, "Read " + chars + " characters of JSON.");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        try {
            return new JSONArray(json);
        } catch (JSONException e) {
            Log.e(LOGTAG, "ERROR creating JSON");
            e.printStackTrace();
            return null;
        }
    }
}
