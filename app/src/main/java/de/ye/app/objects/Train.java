package de.ye.app.objects;


import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// TODO maybe Train should extend TrainLine?
public class Train {

    private static final String LOGTAG = Train.class.getSimpleName();

    public Quad quad;
    long timeInMillisOnCreation;
    private String trainLineName;
    private String trainID;
    private String previousStopName;
    private String nextStopName;
    private int previousStopID;
    private int nextStopID;
    private JSONArray futureProgress;
    private double progress;

    public Train(String trainLineName,
                 String trainID,
                 int previousStopID,
                 int nextStopID,
                 JSONArray futureProgress,
                 double progress) {
        this.quad = new Quad();
        this.trainLineName = trainLineName;
        this.trainID = trainID;
        this.previousStopID = previousStopID;
        this.nextStopID = nextStopID;
        this.futureProgress = futureProgress;
        this.progress = progress;
        this.timeInMillisOnCreation = System.currentTimeMillis();
    }

    public Train(JSONObject object) {
        this.quad = new Quad();
        this.timeInMillisOnCreation = System.currentTimeMillis();

        try {
            // this.trainLineName = object.getString("trainLine");
            this.trainID = object.getString("trainID");
            this.previousStopID = object.getInt("startID");
            this.previousStopName = object.getString("startStation");
            this.nextStopName = object.getString("stopStation");
            this.nextStopID = object.getInt("stopID");
            this.futureProgress = object.getJSONArray("p");
            this.progress = object.getDouble("percentage") / 100;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.trainLineName = "S42";

        // make sure progress is not higher than 1
        if (this.progress > 1) {
            this.progress = 1;
        }

        Log.i(LOGTAG, this.toString());
    }

    public double getCurrentProgress() {
        long currentTimeInMillis = System.currentTimeMillis();
        long futureShift = currentTimeInMillis - this.timeInMillisOnCreation;
//        Log.i(LOGTAG, "getCurrentProgress: futureShift = " + futureShift);
        double result = 0;

        // search in the futureProgress Array the current progress
        for (int i = 0; i < futureProgress.length(); i++) { // TODO optimize the backend JSON: progress value as key
//            Log.i(LOGTAG, "getCurrentProgress: i = " + i);
            try {
                JSONObject jsonObject = this.futureProgress.getJSONObject(i);
                int progressInMillis = jsonObject.getInt("time");
                long diffMillis = futureShift - progressInMillis;
//                Log.i(LOGTAG, "getCurrentProgress: diffMillis = " + diffMillis);
                if (diffMillis < 2000 && diffMillis > 0) {
                    double progress = jsonObject.getDouble("percentage") / 100;
//                    Log.i(LOGTAG, "progress: " + progress);
//                    Log.i(LOGTAG, "~~~~");

//                    if (progress > 1) { // ensure that the progress is not higher than 1 to prevent unexpected behaviour
//                        progress = 1;
//                        Log.e(LOGTAG, "getCurrentProgress: progress was set to 1");
//                    }

                    double lastProgress = 0;

                    if (i != 0) {
                        JSONObject lastJsonObject = this.futureProgress.getJSONObject(i - 1);
                        lastProgress = lastJsonObject.getDouble("percentage") / 100;
                    }

                    result = getDetailedProgress(lastProgress, progress, 2000, (int) diffMillis);

//                    Log.i(LOGTAG, "getCurrentProgress: result = " + result);
                    if (result < 0) { // TODO do something clever about the negative values
                        Log.e(LOGTAG, "getCurrentProgress: result was set to 0");
                        result = 0;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

//        Log.e(LOGTAG, "getCurrentProgress: Could not get the current Progress");
        return result;
    }

    private double getDetailedProgress(double lastProgress, double progress, int totalMillisInStep, int diffMillis) {
        double diffProgress = progress - lastProgress;
        double multiplier = (double) diffMillis / totalMillisInStep;
//        Log.e(LOGTAG, "getCurrentProgress: progress = " + progress + " | lastProgress = " + lastProgress + " | multiplier = " + multiplier);

        return lastProgress + (diffProgress * multiplier);
    }

    public long getTimeOnCreation() {
        return timeInMillisOnCreation;
    }

    public JSONArray getFutureProgress() {
        return futureProgress;
    }

    public void setFutureProgress(JSONArray futureProgress) {
        this.futureProgress = futureProgress;
    }

    public String getTrainLineName() {
        return trainLineName;
    }

    public String getTrainID() {
        return trainID;
    }

    public int getPreviousStopID() {
        return previousStopID;
    }

    public void setPreviousStopID(int previousStopID) {
        this.previousStopID = previousStopID;
    }

    public int getNextStopID() {
        return nextStopID;
    }

    public void setNextStopID(int nextStopID) {
        this.nextStopID = nextStopID;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    @Override
    public String toString() {
        return "Train{" +
                "trainLineName='" + trainLineName + '\'' +
                ", trainID='" + trainID + '\'' +
                ", previousStop=" + previousStopName +
                ", nextStop=" + nextStopName +
                ", futureProgress=" + futureProgress +
                ", progress=" + progress +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Train train = (Train) o;

        if (previousStopID != train.previousStopID) return false;
        if (nextStopID != train.nextStopID) return false;
        if (Double.compare(train.progress, progress) != 0) return false;
        if (trainLineName != null ? !trainLineName.equals(train.trainLineName) : train.trainLineName != null)
            return false;
        if (trainID != null ? !trainID.equals(train.trainID) : train.trainID != null) return false;
        return futureProgress != null ? futureProgress.equals(train.futureProgress) : train.futureProgress == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = trainLineName != null ? trainLineName.hashCode() : 0;
        result = 31 * result + (trainID != null ? trainID.hashCode() : 0);
        result = 31 * result + previousStopID;
        result = 31 * result + nextStopID;
        result = 31 * result + (futureProgress != null ? futureProgress.hashCode() : 0);
        temp = Double.doubleToLongBits(progress);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
