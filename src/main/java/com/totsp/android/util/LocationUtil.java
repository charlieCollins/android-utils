package com.totsp.android.util;

import android.location.Address;

public final class LocationUtil {

   private LocationUtil() {
   }

   public static String getAddressOneLiner(Address address) {
      StringBuilder sb = new StringBuilder();
      if ((address.getLocality() != null) && !address.getLocality().trim().equals("")) {
         sb.append(address.getLocality() + ", ");
      }
      if ((address.getAdminArea() != null) && !address.getAdminArea().trim().equals("")) {
         sb.append(address.getAdminArea());
      }
      if ((sb.length() == 0) && (address.getCountryName() != null) && !address.getCountryName().trim().equals("")) {
         sb.append(address.getCountryName());
      }
      return sb.toString();
   }

}
