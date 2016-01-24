package de.ye.app.data;

import java.util.List;

/**
 * Created by bianca on 30.11.15.
 */
public class RouteStep {

    private String distance;
    private String duration;
    private String direction;
    private String name;
    private String number;

    private Position endLocation;
    private Position startLocation;

    private String instruction;

    private List<RouteStep> steps;

    private String travelMode;

    private TransitDetails transitDetails;

    //Zwischenstopps
    private List<Stop> stops;

    private Stop origin;
    private Stop destination;


    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }


    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public List<RouteStep> getSteps() {
        return steps;
    }

    public void setSteps(List<RouteStep> steps) {
        this.steps = steps;
    }


    public Position getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(Position endLocation) {
        this.endLocation = endLocation;
    }

    public Position getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(Position startLocation) {
        this.startLocation = startLocation;
    }

    public String getTravelMode() {
        return travelMode;
    }

    public void setTravelMode(String travelMode) {
        this.travelMode = travelMode;
    }

    public TransitDetails getTransitDetails() {
        return transitDetails;
    }

    public void setTransitDetails(TransitDetails transitDetails) {
        this.transitDetails = transitDetails;
    }

    public List<Stop> getStops() {
        return stops;
    }

    public void setStops(List<Stop> stops) {
        this.stops = stops;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Stop getOrigin() {
        return origin;
    }

    public void setOrigin(Stop origin) {
        this.origin = origin;
    }

    public Stop getDestination() {
        return destination;
    }

    public void setDestination(Stop destination) {
        this.destination = destination;
    }
}
