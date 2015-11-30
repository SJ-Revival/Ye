package de.ye.yeapp.data;

import java.util.List;

/**
 * Created by bianca on 30.11.15.
 */
public class Route {

    private double northeastLat;
    private double northeastLon;

    private double southwestLat;
    private double southwestLon;

    private String arrivalTime;
    private String departureTime;

    private String distance;
    private String duration;

    private String endAddress;
    private Position endLocation;

    private String startAddress;
    private Position startLocation;

    private List<RouteStep> steps;

    public double getNortheastLat() {
        return northeastLat;
    }

    public void setNortheastLat(double northeastLat) {
        this.northeastLat = northeastLat;
    }

    public double getNortheastLon() {
        return northeastLon;
    }

    public void setNortheastLon(double northeastLon) {
        this.northeastLon = northeastLon;
    }

    public double getSouthwestLat() {
        return southwestLat;
    }

    public void setSouthwestLat(double southwestLat) {
        this.southwestLat = southwestLat;
    }

    public double getSouthwestLon() {
        return southwestLon;
    }

    public void setSouthwestLon(double southwestLon) {
        this.southwestLon = southwestLon;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    public Position getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(Position endLocation) {
        this.endLocation = endLocation;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public Position getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(Position startLocation) {
        this.startLocation = startLocation;
    }

    public List<RouteStep> getSteps() {
        return steps;
    }

    public void setSteps(List<RouteStep> steps) {
        this.steps = steps;
    }
}
