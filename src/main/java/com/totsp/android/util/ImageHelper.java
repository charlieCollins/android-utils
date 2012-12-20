package com.totsp.android.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class ImageHelper {

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

   public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int radius) {
      try {
         Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
         Canvas canvas = new Canvas(output);

         final int color = 0xff424242;
         final Paint paint = new Paint();
         final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
         final RectF rectF = new RectF(rect);
         final float roundPx = radius;

         paint.setAntiAlias(true);
         canvas.drawARGB(0, 0, 0, 0);
         paint.setColor(color);
         canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

         paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
         canvas.drawBitmap(bitmap, rect, rect, paint);

         bitmap.recycle();

         return output;
      } catch (OutOfMemoryError e) {
         Log.e(Constants.LOG_TAG, "OOME trying to round bitmap corners in ImageHelper:" + e.getMessage());
         return bitmap;
      }
   }
}
