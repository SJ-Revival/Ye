package de.ye.yeapp;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.qualcomm.vuforia.samples.VuforiaSamples.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.ye.yeapp.data.Station;
import de.ye.yeapp.utils.JsonParser;
import de.ye.yeapp.utils.StationFactory;

/**
 * Created by bianca on 20.11.15.
 */
public class ScanActivity extends Activity{

    private TextView txtOutput;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_layout);

        txtOutput = (TextView) findViewById(R.id.txtOutput);

        new AsyncTaskParseLocation().execute();

    }


    // you can make this class as another java file so it will be separated from your main activity.
    public class AsyncTaskParseLocation extends AsyncTask<String, String, String> {

        final String TAG = AsyncTaskParseLocation.class.getSimpleName();
        final String JSON_URI = "http://demo.hafas.de/openapi/vbb-proxy/";
        Station station;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected String doInBackground(String... arg0) {
            Log.d(TAG, "doInBackground");

            JsonParser jParser = new JsonParser();
            Map<String,String> map = new HashMap<String,String>();

            String stringFilter = "location.name/";
            stringFilter += "?accessId=" + getResources().getString(R.string.vbb_api);
            //stringFilter += "&originCoordLat=0.12";
            //stringFilter += "&originCoordLong=0.12";
            stringFilter += "&input=Hauptbahnhof&type=S";
            stringFilter += "&format=json";

            JSONObject json = jParser.getJSONObjectFromUrl(JSON_URI + stringFilter, map, "GET");
            //Log.d(TAG, "json: " + json.toString());

            try {
                JSONArray jsonArray = json.getJSONArray("StopLocation");

                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonObjectLocation = jsonArray.getJSONObject(i);
                    station = StationFactory.createStation(jsonObjectLocation);

                    //txtOutput.setText(txtOutput.getText()+" "+station.getName());
                }
                //txtOutput.setText(""+jsonArray.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String strFromDoInBg) {
            super.onPostExecute(strFromDoInBg);

        }
    }
}


