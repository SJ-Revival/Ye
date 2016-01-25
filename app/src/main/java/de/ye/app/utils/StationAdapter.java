package de.ye.app.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import de.ye.app.R;
import de.ye.app.data.Station;

import java.util.List;

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
        LayoutInflater vi = LayoutInflater.from(getContext());

        if(convertView == null) {
            convertView = vi.inflate(R.layout.station_row, parent, false);

            Station station = (Station) getItem(position);

            TextView title = (TextView) (convertView.findViewById(R.id.title));
            title.setText(station.getName());

        }
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
