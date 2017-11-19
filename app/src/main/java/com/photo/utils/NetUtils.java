package com.photo.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.photo.AppConfig;

/**
 * Created by Administrator on 2017/11/18.
 */

public class NetUtils {
    private static final boolean DEBUG = AppConfig.DEBUG;
    private static final String TAG = "NetUtils";

    public static void logPhoneIp(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        if (DEBUG) {
            Log.d(TAG, "ipAddress " + ipAddress + " ip " + intToIp(ipAddress));
        }
    }

    private static String intToIp(int i) {
        return (i & 0xFF ) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF) + "." +
                ( i >> 24 & 0xFF) ;
    }
}
