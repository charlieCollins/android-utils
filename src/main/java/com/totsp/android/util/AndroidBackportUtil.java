package com.totsp.android.util;

import android.app.Activity;

public final class AndroidBackportUtil {

   private AndroidBackportUtil() {
   }

   // should only be called on DONUT (2.0) and above -- callers must check first
   // cannot check here, because once this class is loaded will get a VerifyError on older SDKs
   public static void overridePendingTransition(final Activity activity) {
      if (activity != null) {
         activity.overridePendingTransition(0, 0);
      }
   }
}
