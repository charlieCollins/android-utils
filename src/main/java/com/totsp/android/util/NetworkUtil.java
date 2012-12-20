package com.totsp.android.util;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

public class NetworkUtil {

   public static boolean connectionPresent(ConnectivityManager mgr) {
      if (mgr == null) {
         throw new UnsupportedOperationException("Error, ConnectivityManager cannot be null");
      }
      NetworkInfo netInfo = mgr.getActiveNetworkInfo();
      if ((netInfo != null) && (netInfo.getState() != null)) {
         return netInfo.getState().equals(State.CONNECTED);
      }
      return false;
   }

   public static boolean wifiConnectionPresent(ConnectivityManager mgr) {
      if (mgr == null) {
         throw new UnsupportedOperationException("Error, ConnectivityManager cannot be null");
      }
      NetworkInfo netInfo = mgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
      if ((netInfo != null) && (netInfo.getState() != null)) {
         return netInfo.getState().equals(State.CONNECTED);
      }
      return false;
   }

}
