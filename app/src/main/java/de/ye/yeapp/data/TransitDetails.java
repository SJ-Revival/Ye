package de.ye.yeapp.data;

/**
 * Created by bianca on 30.11.15.
 */
public class TransitDetails {

    private Stop arrivalStop;
    private String arrivalTime;

    private Stop departureStop;
    private String departureTime;

    private String headsign;

    private String lineShortName;
    private String lineType;



    public String getHeadsign() {
        return headsign;
    }

    public void setHeadsign(String headsign) {
        this.headsign = headsign;
    }

    public Stop getArrivalStop() {
        return arrivalStop;
    }

    public void setArrivalStop(Stop arrivalStop) {
        this.arrivalStop = arrivalStop;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public Stop getDepartureStop() {
        return departureStop;
    }

    public void setDepartureStop(Stop departureStop) {
        this.departureStop = departureStop;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getLineShortName() {
        return lineShortName;
    }

    public void setLineShortName(String lineShortName) {
        this.lineShortName = lineShortName;
    }

    public String getLineType() {
        return lineType;
    }

    public void setLineType(String lineType) {
        this.lineType = lineType;
    }
}
