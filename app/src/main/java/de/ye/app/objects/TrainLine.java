package de.ye.app.objects;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class TrainLine {

    private static final String LOGTAG = TrainLine.class.getSimpleName();

    private ArrayList<double[]> trainCorners;
    private int[] trainStationIndices;
    private ArrayList<String> trainStationNames;
    private int[] trainStationIDs;
    private String trainLineName;
    private Boolean ring;

    public TrainLine(String trainLineName,
                     ArrayList<double[]> trainCorners,
                     int[] trainStationIndices,
                     int[] trainStationIDs,
                     ArrayList<String> trainStationNames,
                     Boolean ring) {
        this.trainLineName = trainLineName;
        this.trainCorners = trainCorners;
        this.trainStationIndices = trainStationIndices;
        this.trainStationNames = trainStationNames;
        this.trainStationIDs = trainStationIDs;
        this.ring = ring;
    }

    public TrainLine(JSONObject object) {
        try {
            this.trainLineName = object.getString("name");
            this.trainCorners = parseLinePoints(object.getJSONArray("linePoints"));
            this.trainStationIndices = parseJSONArrayToIntArray(object.getJSONArray("stationIndices"));
            this.trainStationIDs = parseJSONArrayToIntArray(object.getJSONArray("stopIDs"));
            this.trainStationNames = parseJSONArrayToStringArray(object.getJSONArray("stationNames"));
            this.ring = object.getBoolean("ring");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<double[]> parseLinePoints(JSONArray array) {
        ArrayList<double[]> corners = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject jsonObject = array.getJSONObject(i);
                corners.add(new double[]{
                        jsonObject.getDouble("x"),
                        jsonObject.getDouble("y")
                });
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
        int prevStationIndex = getStationIndex(prevStation);
        int nextStationIndex = getStationIndex(nextStation);

        Log.i(LOGTAG, "getTargetCoords 1 = " + "prev: " + prevStation + " | next: " + nextStation + " | %: " + progress);

        if (prevStationIndex == -1 || nextStationIndex == -1) {
            Log.e(LOGTAG, "Could not get the target coordinates for invalid station index");
            return new double[]{0, 0};
        }

        double x = 0;
        double y = 0;

        int prevStationCornerIndex = getStationCornerIndex(prevStation);
        int nextStationCornerIndex = getStationCornerIndex(nextStation);

        Log.i(LOGTAG, "getTargetCoords 2 = " + "prevCIndex: " + prevStationCornerIndex + " | nextCIndex: " + nextStationCornerIndex);

        double[] distances = null;

        if (nextStationCornerIndex > prevStationCornerIndex) {
            // for the normal case
            distances = new double[nextStationCornerIndex - prevStationCornerIndex];
            int distanceIndex = 0;

            for (int i = prevStationCornerIndex; i <= nextStationCornerIndex; i++) {
                if (i != prevStationCornerIndex) {
                    distances[distanceIndex++] = getDistance(i - 1, i);
                }
            }
        } else if (nextStationCornerIndex < prevStationCornerIndex && this.ring) {
            // if its a ring train and the train is between the last and the first station of the round
            int distancesSize = (this.getTrainCorners().size() - nextStationCornerIndex)
                    + prevStationCornerIndex;

            distances = new double[distancesSize];
            int distanceIndex = 0;

            for (int i = prevStationCornerIndex; i < this.getTrainCorners().size(); i++) {
                if (i != prevStationCornerIndex) {
                    distances[distanceIndex++] = getDistance(i - 1, i);
                }
            }

            for (int i = 0; i <= nextStationCornerIndex; i++) {
                if (i != 0) {
                    distances[distanceIndex++] = getDistance(i - 1, i);
                }
            }

        } else {
            Log.e(LOGTAG, "Line " + this.trainLineName);
            Log.e(LOGTAG, "previous Station: " + this.getTrainStationNameByCorner(prevStationIndex)
                    + " | next Station: " + this.getTrainStationNameByCorner(nextStationIndex));
        }

        if (distances != null) {
            // sum all corner distances
            double totalDistance = 0;

            for (double i : distances) {
                totalDistance += i;
            }
            // Log.d(LOGTAG, "Total distance: " + totalDistance);

            // get the section index between tow corners, of the train position
            double currentTrainDistance = totalDistance * progress;
            int sectionIndex = distances.length - 1;
            for (int i = sectionIndex; currentTrainDistance < totalDistance; --i) {
                // Log.d(LOGTAG, " ~~~~~~~~~~~~~~~~~~~~~~~~ ");
                // Log.d(LOGTAG, "Current total distance: " + totalDistance + " -> - " + distances[i]);
                sectionIndex = i;
                // Log.d(LOGTAG, "Current section index: " + sectionIndex);
                totalDistance -= distances[i];
            }

            // actually calculate the position for the Vuforia target image
            if (sectionIndex < this.trainCorners.size()) {
                double p0x = this.trainCorners.get(sectionIndex)[0];
                double p1x = this.trainCorners.get(sectionIndex + 1)[0];
                double p0y = this.trainCorners.get(sectionIndex)[1];
                double p1y = this.trainCorners.get(sectionIndex + 1)[1];

                x = p0x + (p1x - p0x) * progress; // TODO remember to adapt the progress to the section
                y = p0y + (p1y - p0y) * progress;
            } else {
                Log.e(LOGTAG, "Section index " + sectionIndex + " is higher than the size "
                        + this.trainCorners.size() + " of the trainCorners");
            }

        } else {
            Log.e(LOGTAG, "distances is null");
        }

        return new double[]{x, y};
    }

    public double[] getTargetCoords(int prevStation, int nextStation, double progress) {
        String prevStationName = getTrainStationNameByStationID(prevStation);
        String nextStationName = getTrainStationNameByStationID(nextStation);

        return getTargetCoords(prevStationName, nextStationName, progress);
    }

    private double getDistance(int point_1, int point_2) {
        double[] p = this.trainCorners.get(point_1);
        double[] n = this.trainCorners.get(point_2);

        double x1 = p[0];
        double y1 = p[1];

        double x2 = n[0];
        double y2 = n[1];
        // Log.d(LOGTAG, "x1: " + x1 + " y1: " + y1 + " | x2: " + x2 + " y2: " + y2);

        double distance = getDistanceBetweenTwoPoints(x1, x2, y1, y2);
        // Log.d(LOGTAG, "The Distance between x and y is " + distance);
        return distance;
    }

    private double getDistanceBetweenTwoPoints(double x1, double x2, double y1, double y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    private int getStationIndex(String stationName) {
        if (this.trainStationNames.contains(stationName)) {
            return this.trainStationNames.indexOf(stationName);
        } else {
            Log.e(LOGTAG, "unknown station name: " + stationName);
            return -1;
        }
    }

    public int getStationCornerIndex(String station) {
        int stationIndex = this.trainStationNames.indexOf(station);
        return this.trainStationIndices[stationIndex];
    }

    public String getTrainStationNameByCorner(int index) {
        return this.trainStationNames.get(index);
    }

    public String getTrainStationNameByStationID(int id) {

        int stationIndex = -1;

        for (int i = 0; i < this.trainStationIDs.length; i++) {
            if (this.trainStationIDs[i] == id) {
                stationIndex = i;
                break;
            }
        }

        if (id >= 0) {
            if (stationIndex >= 0) {
                return this.trainStationNames.get(stationIndex);
            } else {
                Log.e(LOGTAG, "No station name found for station index " + stationIndex);
                return null;
            }
        } else {
            Log.e(LOGTAG, "No station name found for ID " + id);
            return null;
        }
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

    @Override
    public String toString() {
        return "TrainLine{" +
                "trainCorners=" + trainCorners +
                ", trainStationIndices=" + Arrays.toString(trainStationIndices) +
                ", trainStationNames=" + trainStationNames +
                ", trainStationIDs=" + Arrays.toString(trainStationIDs) +
                ", trainLineName='" + trainLineName + '\'' +
                ", ring=" + ring +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TrainLine trainLine = (TrainLine) o;

        if (trainCorners != null ? !trainCorners.equals(trainLine.trainCorners) : trainLine.trainCorners != null)
            return false;
        if (!Arrays.equals(trainStationIndices, trainLine.trainStationIndices)) return false;
        if (trainStationNames != null ? !trainStationNames.equals(trainLine.trainStationNames) : trainLine.trainStationNames != null)
            return false;
        if (!Arrays.equals(trainStationIDs, trainLine.trainStationIDs)) return false;
        if (trainLineName != null ? !trainLineName.equals(trainLine.trainLineName) : trainLine.trainLineName != null)
            return false;
        return ring != null ? ring.equals(trainLine.ring) : trainLine.ring == null;

    }

    @Override
    public int hashCode() {
        int result = trainCorners != null ? trainCorners.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(trainStationIndices);
        result = 31 * result + (trainStationNames != null ? trainStationNames.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(trainStationIDs);
        result = 31 * result + (trainLineName != null ? trainLineName.hashCode() : 0);
        result = 31 * result + (ring != null ? ring.hashCode() : 0);
        return result;
    }
}
