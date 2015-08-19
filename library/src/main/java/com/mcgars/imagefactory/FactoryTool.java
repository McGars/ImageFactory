package com.mcgars.imagefactory;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.TypedValue;
import android.view.View;

import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

/**
 * Created by Феофилактов on 18.08.2015.
 */
public class FactoryTool {
    public static void setVisible(boolean visible, View... views) {
        setVisible(visible?0:4, views);
    }

    public static void setVisibleGone(View... v) {
        setVisible(8, v);
    }

    public static void setVisible(View... v) {
        setVisible(0, v);
    }

    public static void setVisibleGone(boolean gone, View... views) {
        setVisible(gone?8:0, views);
    }

    private static void setVisible(int status, View... views) {
        if(views != null) {
            View[] var2 = views;
            int var3 = views.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                View v = var2[var4];
                if(v != null && v.getVisibility() != status) {
                    v.setVisibility(status);
                }
            }

        }
    }

    public static int pxToDp(int px, Context contex) {
        return (int) TypedValue.applyDimension(1, (float) px, contex.getResources().getDisplayMetrics());
    }

    public static ImageLoader initImageLoader(Context c) {
        if (ImageLoader.getInstance().isInited())
            return ImageLoader.getInstance();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(c)
                .defaultDisplayImageOptions(getImageLoaderOptions())
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) // 2 Mb
                .build();

        ImageLoader.getInstance().init(config);
        return ImageLoader.getInstance();
    }

    public static DisplayImageOptions getImageLoaderOptions() {
        return getImageLoaderOptionsBuilder().build();
    }

    public static DisplayImageOptions.Builder getImageLoaderOptionsBuilder() {
        return new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
//                .showImageOnFail(R.drawable.missing_preview)
                .resetViewBeforeLoading(false)
                .displayer(new FadeInBitmapDisplayer(400, true, true, false));
    }

    public static boolean getConnection(Context c) {
        ConnectivityManager conMgr = (ConnectivityManager) c
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo i = conMgr.getActiveNetworkInfo();
        boolean ret = true;
        if (i != null) {
            if (!i.isConnected())
                ret = false;
            if (!i.isAvailable())
                ret = false;
        }

        if (i == null)
            ret = false;

        return ret;
    }

    public static int getAttributeResourceId(Context context, int attr) {
        try {
            TypedValue e = new TypedValue();
            int[] resIdAttr = new int[]{attr};
            TypedArray a = context.obtainStyledAttributes(e.data, resIdAttr);
            int resId = a.getResourceId(0, 0);
            a.recycle();
            return resId;
        } catch (Exception var6) {
            var6.printStackTrace();
            return 0;
        }
    }
}
