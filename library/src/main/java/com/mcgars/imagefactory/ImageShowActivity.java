package com.mcgars.imagefactory;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageShowActivity extends Activity {

    public static String IMAGE_URL = "image";
    public View loader;
    private PhotoViewAttacher mAttacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imagefactory_image_activity);
        loader = findViewById(R.id.pbLoader);

        Bundle extra = getIntent().getExtras();
        String url = extra.getString(IMAGE_URL);
        if (url == null) {
//            hs.showMsg(this, R.string.cant_load_image);
            finish();
        } else {
//            if (hs.getConnection(this)) {
                ImageView iv = (ImageView) findViewById(R.id.ivBigImage);
                mAttacher = new PhotoViewAttacher(iv);
                hs.initImageLoader(this).displayImage(url, iv, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        hs.setVisibleGone(loader);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        hs.setVisibleGone(loader);
                        mAttacher.update();
                    }
                });
//            }
            //else {
//                hs.showMsg(this, R.string.error_connection);
//            }

        }
    }
}
