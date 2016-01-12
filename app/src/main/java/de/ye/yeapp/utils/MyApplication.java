package de.ye.yeapp.utils;

import android.app.Application;

import java.util.List;

import de.ye.yeapp.data.Route;
import de.ye.yeapp.data.User;

/**
 * Created by bianca on 12.01.16.
 */
public class MyApplication extends Application {


    private User user;
    private List<Route> route;


    public List<Route> getRoute() {
        return route;
    }

    public void setRoute(List<Route> route) {
        this.route = route;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
