package com.mcgars.imagefactory;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mcgars.imagefactory.objects.Thumb;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Феофилактов on 18.08.2015.
 */
public class PagerImageController {

    private Context context;
    private ViewPager viewPager;
    private ImageView clickImage;
    PhotoViewAttacher animatedAtacher;

    public PagerImageController(Context context,ViewPager viewPager){
        this.context = context;
        this.viewPager = viewPager;
    }

    public PagerImageController setClickImage(ImageView clickImage, PhotoViewAttacher animatedAtacher){
        this.clickImage = clickImage;
        this.animatedAtacher = animatedAtacher;
        return this;
    }

    public void setList(int selectedPosition, List<Thumb> list){
        List<View> views = new ArrayList<>();

        for (Thumb thumb : list) {
            ImageView image = new ImageView(context);

            image.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            final PhotoViewAttacher atacher = new PhotoViewAttacher(image);
            if(animatedAtacher!=null && clickImage!=null){
                image.setImageDrawable(clickImage.getDrawable());
                animatedAtacher.update();
            }
            hs.initImageLoader(context).displayImage(thumb.getOrigin(), image, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    super.onLoadingComplete(imageUri, view, loadedImage);
                    atacher.update();
                }
            });
            views.add(image);
        }
        ThumbPagerAdapter adapter = new ThumbPagerAdapter(views);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(selectedPosition);
    }
}
