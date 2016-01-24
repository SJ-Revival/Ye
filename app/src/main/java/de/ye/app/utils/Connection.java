package de.ye.app.utils;

import java.net.InetAddress;

/**
 * Created by bianca on 18.01.16.
 */
public class Connection {

    public static boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");

            if (ipAddr.equals("")) {
                return false;
            } else {
                return true;
            }

        } catch (Exception e) {
            return false;
        }

    }
}
