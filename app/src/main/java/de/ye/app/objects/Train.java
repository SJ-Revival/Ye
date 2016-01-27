package de.ye.app.objects;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Train {

    private static final String LOGTAG = Train.class.getSimpleName();

    ArrayList<double[]> trainCorners;
    int[] trainStationIndices;
    ArrayList<String> trainStationNames;
    String trainLineName;
    Boolean ring;

    public Train(String trainLineName, ArrayList<double[]> trainCorners, int[] trainStationIndices, ArrayList<String> trainStationNames, Boolean ring) {
        this.trainLineName = trainLineName;
        this.trainCorners = trainCorners;
        this.trainStationIndices = trainStationIndices;
        this.trainStationNames = trainStationNames;
        this.ring = ring;
    }

    public Train(JSONObject object) {
        try {
            this.trainLineName = object.getString("name");
            this.trainCorners = parseLinePoints(object.getJSONArray("linePoints"));
            this.trainStationIndices = parseJSONArrayToIntArray(object.getJSONArray("stationIndices"));
            this.trainStationNames = parseJSONArrayToStringArray(object.getJSONArray("stationNames"));
            this.ring = object.getBoolean("ring");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Train() { // TODO remove... only for testing
        this.trainLineName = "S42";
        this.trainCorners = new ArrayList<>();
        this.trainStationIndices = new int[]{1, 3, 4, 5};
        this.trainStationNames = new ArrayList<>();

        // x and y on the Vuforia target
        this.trainCorners.add(new double[]{0, 0});
        this.trainCorners.add(new double[]{0, 50});
        this.trainCorners.add(new double[]{50, 50});
        this.trainCorners.add(new double[]{100, 100});
        this.trainCorners.add(new double[]{100, 0});
        this.trainCorners.add(new double[]{0, -100});

        this.trainStationNames.add("A");
        this.trainStationNames.add("B");
        this.trainStationNames.add("C");
        this.trainStationNames.add("D");

        this.ring = true;
    }

    private ArrayList<double[]> parseLinePoints(JSONArray array) {
        ArrayList<double[]> corners = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject o = array.getJSONObject(i);
                corners.add(new double[]{(double) o.get("x"), (double) o.get("y")});
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return corners;
    }

    private int[] parseJSONArrayToIntArray(JSONArray array) {
        int[] numbers = new int[array.length()];

        for (int i = 0; i < array.length(); ++i) {
            numbers[i] = array.optInt(i);
        }

        return numbers;
    }

    private ArrayList<String> parseJSONArrayToStringArray(JSONArray array) {
        ArrayList<String> str = new ArrayList<>();

        for (int i = 0; i < array.length(); ++i) {
            try {
                str.add(array.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return str;
    }

    /**
     * Returns the x- and y-position on the Vuforia target for the given data.
     *
     * @param prevStation the name of the previous train station
     * @param nextStation the name of the next train station
     * @param progress    the progress of the train between the two stations (between 0 and 1)
     * @return the x- and y-position on the Vuforia target
     */
    public double[] getTargetCoords(String prevStation, String nextStation, double progress) {
        int prevStationIndex = searchStationName(prevStation);
        int nextStationIndex = searchStationName(nextStation);

        if (prevStationIndex == -1 || nextStationIndex == -1) {
            Log.d(LOGTAG, "Could not get the target coordinates for invalid station index");
            return new double[]{0, 0};
        }

        double x = 0;
        double y = 0;

        // TODO check the loop for Ring Bahn
        int prevStationCornerIndex = getTrainStationIndex(prevStation);
        int nextStationCornerIndex = getTrainStationIndex(nextStation);

        double[] distances = new double[nextStationCornerIndex - prevStationCornerIndex];

        for (int i = prevStationCornerIndex; i <= nextStationCornerIndex; i++) {
            int distanceIndex = i - prevStationCornerIndex - 1;

            if (i != prevStationCornerIndex) {
                double[] p = this.trainCorners.get(i - 1);
                double[] n = this.trainCorners.get(i);

                double x1 = p[0];
                double y1 = p[1];

                double x2 = n[0];
                double y2 = n[1];
                Log.d(LOGTAG, "x1: " + x1 + " y1: " + y1 + " | x2: " + x2 + " y2: " + y2);

                distances[distanceIndex] = getDistanceBetweenTwoPoints(x1, x2, y1, y2);
                Log.d(LOGTAG, "The Distance between x and y is " + distances[distanceIndex]);
            }
        }

        // sum all corner distances
        double totalDistance = 0;
        for (double i : distances) {
            totalDistance += i;
        }
        Log.d(LOGTAG, "Total distance: " + totalDistance);

        // get the section index between tow corners, of the train position
        double currentTrainDistance = totalDistance * progress;
        int sectionIndex = distances.length - 1;
        for (int i = sectionIndex; currentTrainDistance < totalDistance; i--) {
            Log.d(LOGTAG, "Current total distance: " + totalDistance + " -> - " + distances[i]);
            sectionIndex = i;
            Log.d(LOGTAG, "Current section index: " + sectionIndex);
            totalDistance -= distances[i];
        }

        // TODO actually calculate the position for the Vuforia target image
        double p0x = this.trainCorners.get(sectionIndex)[0];
        double p1x = this.trainCorners.get(sectionIndex + 1)[0];
        double p0y = this.trainCorners.get(sectionIndex)[1];
        double p1y = this.trainCorners.get(sectionIndex + 1)[1];

        x = p0x + (p1x - p0x) * progress; // TODO remember to adapt the progress to the section
        y = p0y + (p1y - p0y) * progress;

        return new double[]{x, y};
    }

    private double getDistanceBetweenTwoPoints(double x1, double x2, double y1, double y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    private int searchStationName(String stationName) {
        if (this.trainStationNames.contains(stationName)) {
            return this.trainStationNames.indexOf(stationName);
        } else {
            Log.d(LOGTAG, "unknown station name: " + stationName);
            return -1;
        }
    }

    public int getTrainStationIndex(String station) {
        int stationIndex = this.trainStationNames.indexOf(station);
        return this.trainStationIndices[stationIndex];
    }

    public String getTrainStationName(int index) {
        return this.trainStationNames.get(index);
    }

    public ArrayList<double[]> getTrainCorners() {
        return trainCorners;
    }

    public void setTrainCorners(ArrayList<double[]> trainCorners) {
        this.trainCorners = trainCorners;
    }

    public int[] getTrainStationIndices() {
        return trainStationIndices;
    }

    public void setTrainStationIndices(int[] trainStationIndices) {
        this.trainStationIndices = trainStationIndices;
    }

    public ArrayList<String> getTrainStationNames() {
        return trainStationNames;
    }

    public void setTrainStationNames(ArrayList<String> trainStationNames) {
        this.trainStationNames = trainStationNames;
    }

    public String getTrainLineName() {
        return trainLineName;
    }

    public void setTrainLineName(String trainLineName) {
        this.trainLineName = trainLineName;
    }
}
