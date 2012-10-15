package com.totsp.android.util;

/**
 * ScreenInfo
 * 
 * MODIFIED by MOVL, taken from:
 * 
 * Copyright (c) 2011 Michael J. Portuesi (http://www.jotabout.com)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import java.lang.reflect.Method;

import android.R;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

/**
 * Screen is a model object that summarizes information about the
 * device's display.
 * 
 * It unifies information from a few disparate Android APIs (Display,
 * DisplayMetrics, Configuration), and derives some additional device
 * statistics.  It also provides helpers to format data as strings
 * for display.
 * 
 */
public class Screen {

   private Display mDisplay;
   private Configuration mConfig;

   private int mSizeClass;

   private int widthPx;
   private int heightPx;
   private int widthDp;
   private int heightDp;
   private int smallestDp;
   private int densityDpi;
   private float xdpi;
   private float ydpi;
   private double density;
   private float scaledDensity;

   private double physicalWidth;
   private double physicalHeight;
   private double diagonalSizeInches;
   private double diagonalSizeMillimeters;

   private int screenLayout;
   private int touchScreen;

   private int defaultOrientation;
   private String currentOrientation;

   private int pixelFormat;
   private float refreshRate;

   private final Context ctx;

   public Screen(final Context ctx) {
      
      this.ctx = ctx;
      
      WindowManager wm = ((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE));
      mDisplay = wm.getDefaultDisplay();
      mConfig = ctx.getResources().getConfiguration();

      // Screen Size classification
      mSizeClass = mConfig.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;

      // Screen dimensions
      try {
         // Try to get size without the Status bar, if we can (API level 13)
         Method getSizeMethod = mDisplay.getClass().getMethod("getSize", Point.class);
         Point pt = new Point();
         getSizeMethod.invoke(mDisplay, pt);
         widthPx = pt.x;
         heightPx = pt.y;
      } catch (Exception ignore) {
         // Use older APIs
         widthPx = mDisplay.getWidth();
         heightPx = mDisplay.getHeight();
      }

      // Calculate screen sizes in device-independent pixels (dp)
      DisplayMetrics metrics = new DisplayMetrics();
      mDisplay.getMetrics(metrics);
      widthDp = (int) (((double) widthPx / metrics.density) + 0.5);
      heightDp = (int) (((double) heightPx / metrics.density) + 0.5);
      smallestDp = widthDp > heightDp ? heightDp : widthDp;

      // DPI
      densityDpi = metrics.densityDpi;
      xdpi = metrics.xdpi;
      ydpi = metrics.ydpi;

      // Screen density scaling factors
      density = metrics.density;
      scaledDensity = metrics.scaledDensity;

      // Normalize the xdpi/ydpi for the next set of calculations
      double xdpi = metrics.xdpi;
      if (xdpi < 1.0) {
         // Guard against divide-by-zero. This is possible with lazy device manufacturers
         // who set these fields incorrectly. Set the density to our best guess.
         xdpi = metrics.densityDpi;
      }
      double ydpi = metrics.ydpi;
      if (ydpi < 1.0) {
         ydpi = metrics.densityDpi;
      }

      // Calculate physical screen width/height
      physicalWidth = ((double) metrics.widthPixels) / xdpi;
      physicalHeight = ((double) metrics.heightPixels) / ydpi;

      // Calculate diagonal screen size, in both U.S. and Metric units
      double rawDiagonalSizeInches = Math.sqrt(Math.pow(physicalWidth, 2) + Math.pow(physicalHeight, 2));
      diagonalSizeInches = Math.floor(rawDiagonalSizeInches * 10.0 + 0.5) / 10.0;
      diagonalSizeMillimeters = Math.floor(rawDiagonalSizeInches * 25.4 + 0.5);

      // Long/wide
      screenLayout = mConfig.screenLayout & Configuration.SCREENLAYOUT_LONG_MASK;

      // Orientation
      defaultOrientation = mConfig.orientation;

      // Touchscreen type
      touchScreen = mConfig.touchscreen;

      // Current rotation
      determineCurrentRotation();

      // Pixel format
      pixelFormat = mDisplay.getPixelFormat();

      // Refresh rate
      refreshRate = mDisplay.getRefreshRate();
   }

   /**
    * Model name of device.
    * @return
    */
   public String deviceModel() {
      return Build.MODEL;
   }

   /**
    * Version of Android (e.g. "2.3.5").
    * 
    * @return
    */
   public String androidVersion() {
      return Build.VERSION.RELEASE;
   }

   /**
    * Screen Size classification
    */
   public int sizeClassification() {
      return mSizeClass;
   }

   /**
    * Size classification, as displayable text
    * 
    * @param ctx
    * @return
    */
   public String sizeClassificationText() {
      switch (mSizeClass) {
         case Configuration.SCREENLAYOUT_SIZE_SMALL:
            return "small";
         case Configuration.SCREENLAYOUT_SIZE_NORMAL:
            return "normal";
         case Configuration.SCREENLAYOUT_SIZE_LARGE:
            return "large";
         // xlarge only for api level 10 and up
         //case Configuration.SCREENLAYOUT_SIZE_XLARGE:
         //   return "xlarge";
         case Configuration.SCREENLAYOUT_SIZE_UNDEFINED:
            return "UNDEFINED";
      }

      return "UNKNOWN";
   }

   /**
    * Width of screen, in pixels
    * 
    * @return
    */
   public int widthPx() {
      return widthPx;
   }

   /**
    * Height of screen, in pixels
    * 
    * @return
    */
   public int heightPx() {
      return heightPx;
   }

   /**
    * Width of screen, in dp
    * 
    * @return
    */
   public int widthDp() {
      return widthDp;
   }

   /**
    * Height of screen, in dp
    * 
    * @return
    */
   public int heightDp() {
      return heightDp;
   }

   /**
    * Smallest screen dimension in dp, (smallestWidthDp used in layout classification)
    * (see: http://android-developers.blogspot.com/2011/07/new-tools-for-managing-screen-sizes.html)
    * 
    * @return
    */
   public int smallestDp() {
      return smallestDp;
   }

   /**
    * Logical dpi, used for device classification
    * 
    * @return
    */
   public int densityDpi() {
      return densityDpi;
   }

   /**
    * Density classification, as text
    */
   public String densityDpiText() {
      switch (densityDpi) {
         //case DisplayMetrics.DENSITY_TV:
         //   return "tvdpi";
         case DisplayMetrics.DENSITY_LOW:
            return "ldpi";
         case DisplayMetrics.DENSITY_MEDIUM:
            return "mdpi";
         case DisplayMetrics.DENSITY_HIGH:
            return "hdpi";
            // xhigh only for api 10 and up
         //case DisplayMetrics.DENSITY_XHIGH:
         //   return "xhdpi";
      }

      return "UNKNOWN";
   }

   /**
    * Physical x dpi
    * @return
    */
   public float xdpi() {
      return xdpi;
   }

   /**
    * Physical y dpi
    * @return
    */
   public float ydpi() {
      return ydpi;
   }

   /**
    * Density factor for px/dp conversions
    */
   public double density() {
      return density;
   }

   /**
    * Scaling factor for fonts used on the display (DisplayMetrics.scaledDensity)
    * @return
    */
   public float scaledDensity() {
      return scaledDensity;
   }

   /**
    * Calculated physical width of the screen, in inches
    */
   public double physicalWidth() {
      return physicalWidth;
   }

   /**
    * Calculated physical height of the screen, in inches
    */
   public double physicalHeight() {
      return physicalHeight;
   }

   /**
    * Calculated diagonal size of the screen, in inches
    */
   public double diagonalSizeInches() {
      return diagonalSizeInches;
   }

   /**
    * Calculated diagonal size of the screen, in millimeters
    */
   public double diagonalSizeMillimeters() {
      return diagonalSizeMillimeters;
   }

   /**
    * Screen layout, as integer
    */
   public int screenLayout() {
      return screenLayout;
   }

   /**
    * Screen layout, as text
    */
   public String screenLayoutText() {
      switch (screenLayout) {
         case Configuration.SCREENLAYOUT_LONG_YES:
            return ctx.getString(R.string.yes);
         case Configuration.SCREENLAYOUT_LONG_NO:
            return ctx.getString(R.string.no);
         case Configuration.SCREENLAYOUT_LONG_UNDEFINED:
            return "UNDEFINED";
      }

      return "UNDEFINED";
   }

   /**
    * Default, or "natural" screen orientation of the device.
    */
   public int defaultOrientation() {
      return defaultOrientation;
   }

   /**
    * Default orientation as text
    */
   public String defaultOrientationText() {
      return orientationText(ctx, defaultOrientation);
   }

   /**
    * Current orientation as text
    */
   public String currentOrientationText() {
      return currentOrientation;
   }

   /**
    * Touchscreen properties
    */
   public int touchScreen() {
      return touchScreen;
   }

   /**
    * Touchscreen properties as text
    */
   public String touchScreenText() {
      switch (touchScreen) {
         case Configuration.TOUCHSCREEN_FINGER:
            return "TOUCH-FINGER";
         case Configuration.TOUCHSCREEN_STYLUS:
            return "TOUCH-STYLUS";
         case Configuration.TOUCHSCREEN_NOTOUCH:
            return "TOUCH-NONE";
         case Configuration.TOUCHSCREEN_UNDEFINED:
            return "UNDEFINED";
      }

      return "UNDEFINED";
   }

   /**
    * Pixel format
    */
   public int pixelFormat() {
      return pixelFormat;
   }

   /**
    * Pixel format as text
    */
   public String pixelFormatText() {
      switch (pixelFormat) {
         case PixelFormat.A_8:
            return "A_8";
         //case ImageFormat.JPEG:
         //   return "JPEG";
         case PixelFormat.L_8:
            return "L_8";
         case PixelFormat.LA_88:
            return "LA_88";
         case PixelFormat.OPAQUE:
            return "OPAQUE";
         case PixelFormat.RGB_332:
            return "RGB_332";
         case PixelFormat.RGB_565:
            return "RGB_565";
         case PixelFormat.RGB_888:
            return "RGB_888";
         case PixelFormat.RGBA_4444:
            return "RGBA_4444";
         case PixelFormat.RGBA_5551:
            return "RGBA_5551";
         case PixelFormat.RGBA_8888:
            return "RGBA_8888";
         case PixelFormat.RGBX_8888:
            return "RGBX_8888";
         case PixelFormat.TRANSLUCENT:
            return "TRANSLUCENT";
         case PixelFormat.TRANSPARENT:
            return "TRANSPARENT";
         case PixelFormat.UNKNOWN:
            return "UNKNOWN";
         //case ImageFormat.NV21:
         //   return "NV21";
         //case ImageFormat.YUY2:
         //   return "YUY2";
         //case ImageFormat.NV16:
         //   return "NV16";
         default:
            return "UNKNOWN";
      }
   }

   /**
    * Refresh rate
    */
   public float refreshRate() {
      return refreshRate;
   }

   /**
    * Return a string containing a text-based summary, suitable
    * to share, email, save to SD card, etc.
    * 
    * @param ctx
    * @return
    */
   public String summaryText() {
      StringBuilder sb = new StringBuilder();

      addLine(sb, ctx, "MODEL", deviceModel());
      addLine(sb, ctx, "OS-VERSION", androidVersion());
      addLine(sb, ctx, "SCREEN-CLASS", sizeClassificationText());
      addLine(sb, ctx, "DENSITY-CLASS", densityDpiText());
      addLine(sb, ctx, "WIDTH-PIXELS", widthPx());
      addLine(sb, ctx, "HEIGHT-PIXELS", heightPx());
      addLine(sb, ctx, "DP", widthDp());
      addLine(sb, ctx, "HEIGHT-DP", heightDp());
      addLine(sb, ctx, "SMALLEST-DP", smallestDp());
      addLine(sb, ctx, "LONG-WIDE", screenLayoutText());
      addLine(sb, ctx, "NAT-ORIENTATION", defaultOrientationText());
      addLine(sb, ctx, "CURR-ORIENTATION", currentOrientationText());
      addLine(sb, ctx, "TOUCHSCREEN", touchScreenText());
      addLine(sb, ctx, "SCREEN-DPI", densityDpi());
      addLine(sb, ctx, "ACTUAL-XDPI", xdpi());
      addLine(sb, ctx, "ACTUAL-YDPI", ydpi());
      addLine(sb, ctx, "LOGICAL-DENSITY", density());
      addLine(sb, ctx, "FONT-SCALE-DENSITY", scaledDensity());
      addLine(sb, ctx, "COMP-DIAG-SIZE_INCH", diagonalSizeInches());
      addLine(sb, ctx, "COMP-DIAG-SIZE-MM", diagonalSizeMillimeters());
      addLine(sb, ctx, "PIXEL-FORMAT", pixelFormatText());
      addLine(sb, ctx, "REFRESH-RATE", refreshRate());

      return sb.toString();
   }

   private void addLine(StringBuilder sb, Context ctx, String s, String value) {
      sb.append(s).append(" ").append(value).append("\n");
   }

   private void addLine(StringBuilder sb, Context ctx, String s, int value) {
      addLine(sb, ctx, s, Integer.toString(value));
   }

   private void addLine(StringBuilder sb, Context ctx, String s, float value) {
      addLine(sb, ctx, s, Float.toString(value));
   }

   private void addLine(StringBuilder sb, Context ctx, String s, double value) {
      addLine(sb, ctx, s, Double.toString(value));
   }

   //////////////////////////////////////////////////////////////////////////
   // Private
   //////////////////////////////////////////////////////////////////////////

   /**
    * Do the best job we can to find out which way the screen is currently rotated.
    */
   private void determineCurrentRotation() {
      // First, try the Display#getRotation() call, which was introduced in Froyo.
      // Reference: http://android-developers.blogspot.com/2010/09/one-screen-turn-deserves-another.html
      try {
         Method getRotationMethod = mDisplay.getClass().getMethod("getRotation");
         int rotation = (Integer) getRotationMethod.invoke(mDisplay);
         switch (rotation) {
            case Surface.ROTATION_0:
               currentOrientation = "0";
               break;
            case Surface.ROTATION_90:
               currentOrientation = "90";
               break;
            case Surface.ROTATION_180:
               currentOrientation = "180";
               break;
            case Surface.ROTATION_270:
               currentOrientation = "270";
               break;
         }
         return;
      } catch (Exception ignore) {
         ;
      }

      // Fall back on the deprecated Display#getOrientation method from earlier releases of Android.
      int orientation = mDisplay.getOrientation();
      currentOrientation = orientationText(ctx, orientation);
   }

   /**
    * Returns orientation text as string
    */
   private String orientationText(Context ctx, int orientation) {
      switch (orientation) {
         case Configuration.ORIENTATION_LANDSCAPE:
            return "LANDSCAPE";
         case Configuration.ORIENTATION_PORTRAIT:
            return "PORTRAIT";
         case Configuration.ORIENTATION_SQUARE:
            return "SQUARE";
         case Configuration.ORIENTATION_UNDEFINED:
            return "UNDEFINED";
      }

      return "UNDEFINED";
   }

}