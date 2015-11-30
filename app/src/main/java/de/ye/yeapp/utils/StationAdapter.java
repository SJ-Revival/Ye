package de.ye.yeapp.utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import de.ye.yeapp.R;
//import com.qualcomm.vuforia.samples.VuforiaSamples.R;

import java.util.List;

import de.ye.yeapp.data.Station;

/**
 * Created by bianca on 30.11.15.
 */
public class StationAdapter extends ArrayAdapter {
    private static final String TAG = StationAdapter.class.getSimpleName();

    public StationAdapter(Context context, int resource, int textViewResourceId, List<Station> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public StationAdapter(Context context, int resource, List<Station> objects) {
        super(context, resource, objects);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater = LayoutInflater.from(getContext());
        convertView  = mInflater.inflate(R.layout.station_row, parent, false);

        Station station = (Station)getItem(position);

        TextView title = (TextView)(convertView.findViewById(R.id.title));
        title.setText(station.getName());



        return convertView;


    }

    /*@Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.
        super.onListItemClick(l, v, position, id);
        //do something here using the position in the array
        Station station = (Station)getItem(position);
        System.out.println("BIANCA" + ""+station.getName() );
    }*/





}
