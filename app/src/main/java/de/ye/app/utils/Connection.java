package de.ye.app.utils;

import java.net.InetAddress;

/**
 * Created by bianca on 18.01.16.
 */
public class Connection {

    public static boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");

            return !ipAddr.toString().equals("");

        } catch (Exception e) {
            return false;
        }

    }
}
