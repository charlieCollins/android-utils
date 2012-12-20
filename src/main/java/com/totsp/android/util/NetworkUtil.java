package com.totsp.android.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

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


   // also see UrlImageHelper and such projects
   public Bitmap retrieveBitmap(String urlString) {
      Log.d(Constants.LOG_TAG, "making HTTP trip for image:" + urlString);
      Bitmap bitmap = null;
      try {
         URL url = new URL(urlString);
         // NOTE, be careful about just doing "url.openStream()"  
         // it's a shortcut for openConnection().getInputStream() and doesn't set timeouts
         // (the defaults are "infinite" so it will wait forever if endpoint server is down)
         // do it properly with a few more lines of code . . .         
         URLConnection conn = url.openConnection();
         conn.setConnectTimeout(3000);
         conn.setReadTimeout(5000);
         bitmap = BitmapFactory.decodeStream(conn.getInputStream());
      } catch (MalformedURLException e) {
         Log.e(Constants.LOG_TAG, "Exception loading image, malformed URL", e);
      } catch (IOException e) {
         Log.e(Constants.LOG_TAG, "Exception loading image, IO error", e);
      }
      return bitmap;
   }

   // use with retrieve image task or similar off UI thread approach
   /*
   class RetrieveImageTask extends AsyncTask<String, Void, Bitmap> {
      private ImageView imageView;

      public RetrieveImageTask(ImageView imageView) {
         this.imageView = imageView;
      }

      @Override
      protected Bitmap doInBackground(String... args) {
         Bitmap bitmap = app.retrieveBitmap(args[0]);
         return bitmap;
      }

      @Override
      protected void onPostExecute(Bitmap bitmap) {
         if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            app.getImageCache().put((Long) imageView.getTag(), bitmap);
            imageView.setTag(null);
         }
      }
   }
   */

}
