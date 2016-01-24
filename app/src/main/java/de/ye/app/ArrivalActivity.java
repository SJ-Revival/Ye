package de.ye.app;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import de.ye.app.data.Route;
import de.ye.app.utils.JsonParser;
import de.ye.app.utils.MyApplication;
import de.ye.app.utils.TripFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bianca on 20.11.15.
 */
public class ArrivalActivity extends Activity {

    private List<Route> listRoute;
    private Context context;
    private MyApplication myApplication;
    private TextView txtOutput;
    private static final String TAG = ArrivalActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_layout);

        listRoute = new ArrayList<Route>();
        context = this.getApplicationContext();

        new AsyncTaskParseLocation().execute();

    }

    ;


    // you can make this class as another java file so it will be separated from your main activity.
    public class AsyncTaskParseLocation extends AsyncTask<String, String, String> {

        final String TAG = AsyncTaskParseLocation.class.getSimpleName();
        final String JSON_URI = "http://demo.hafas.de/openapi/vbb-proxy/";


        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected String doInBackground(String... arg0) {
            Log.d(TAG, "doInBackground");

            JsonParser jParser = new JsonParser();
            Map<String, String> map = new HashMap<String, String>();

            String stringFilter = "location.name/";
            stringFilter += "?accessId=" + getResources().getString(R.string.vbb_api);
            //stringFilter += "&originCoordLat=0.12";
            //stringFilter += "&originCoordLong=0.12";
            stringFilter += "&input=Hauptbahnhof&type=S";
            stringFilter += "&format=json";

            JSONObject json = jParser.getJSONObjectFromUrl(JSON_URI + stringFilter, map, "GET");
            Log.d(TAG, "json: " + json.toString());

            try {
                JSONArray jsonArray = json.getJSONArray("StopLocation");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObjectLocation = jsonArray.getJSONObject(i);
                    Route route = TripFactory.createTrip(jsonObjectLocation);
                    listRoute.add(route);
                    // txtOutput.setText(txtOutput.getText() +" "+route.getTripId());
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
            myApplication.setRoute(listRoute);

            String str = "";

            for (Route route : listRoute
                    ) {

                str += route.getTripId() + "\r\n";

            }

            //stationAdapter = new StationAdapter(getApplicationContext(), R.layout.station_row, listStation);

            //listView.setAdapter(stationAdapter);

            //TextView txt = (TextView) findViewById(R.id.);
            txtOutput.setText(str);

        }
    }

}


