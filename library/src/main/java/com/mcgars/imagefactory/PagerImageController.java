package com.mcgars.imagefactory;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.mcgars.imagefactory.objects.Thumb;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Феофилактов on 18.08.2015.
 */
public class PagerImageController implements View.OnClickListener {

    private final LayoutInflater inflater;
    private Context context;
    private ViewPager viewPager;
    private ImageView clickImage;
    PhotoViewAttacher animatedAtacher;
    private ThumbPagerAdapter adapter;
    private OnImageClickListener imageClickListener;
    private ThumbToImage thumbToImage;
    private List<Thumb> thumbList;
    private boolean zoom;
    private ImageView.ScaleType scale = ImageView.ScaleType.CENTER_CROP;
    private float offset = 1f;
    private ViewPager.OnPageChangeListener zoomPageListener;
    private int backColor;

    public PagerImageController(Context context,ViewPager viewPager){
        this.context = context;
        this.viewPager = viewPager;
        inflater = LayoutInflater.from(context);
    }

    public PagerImageController setClickImage(ImageView clickImage, PhotoViewAttacher animatedAtacher){
        this.clickImage = clickImage;
        this.animatedAtacher = animatedAtacher;
        return this;
    }

    public PagerImageController setPageListener(ViewPager.OnPageChangeListener listener){
        return setPageListener(listener, false);
    }

    public PagerImageController setOnImageClickListener(OnImageClickListener imageClickListener){
        this.imageClickListener = imageClickListener;
        return this;
    }

    public PagerImageController setPageListener(ViewPager.OnPageChangeListener listener, boolean isAdd){
        if(!isAdd)
            viewPager.clearOnPageChangeListeners();
        if(listener!=null){
            viewPager.removeOnPageChangeListener(listener);
            viewPager.addOnPageChangeListener(listener);
        }
        return this;
    }

    public PagerImageController setZoomPageListener(ViewPager.OnPageChangeListener zoomPageListener){
        this.zoomPageListener = zoomPageListener;
        return this;
    }

    public void setList(int selectedPosition, List<Thumb> list){
        setList(selectedPosition, list, true);
    }

    public List<Thumb> getThumbList(){
        return thumbList;
    }

    public void setList(int selectedPosition, List<Thumb> list, final boolean isThumb){
        thumbList = list;
        List<View> views = new ArrayList<>();
        for (Thumb thumb : list) {

            View v = inflater.inflate(R.layout.view_imagefactory_image, null);
            ImageView image = (ImageView) v.findViewById(R.id.image);
            image.setScaleType(scale);
            final View loader = v.findViewById(R.id.pbLoader);

            if(!isThumb){
                if(animatedAtacher!=null && clickImage!=null){
                    image.setImageDrawable(clickImage.getDrawable());
                    animatedAtacher.update();
                }
            } else {
                image.setTag(thumb);
                image.setOnClickListener(this);
            }
            FactoryTool.initImageLoader(context).displayImage(isThumb ? thumb.getThumb() : thumb.getOrigin(), image, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    super.onLoadingComplete(imageUri, view, loadedImage);
                    if (!isThumb) {
                        new PhotoViewAttacher((ImageView) view);
//                        atacher.update();

                    }
                    loader.setVisibility(View.GONE);
                }
            });
            views.add(v);
        }
        adapter = new ThumbPagerAdapter(views);
        adapter.setRightOffset(offset);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(selectedPosition);
    }

    public void setRightOffset(float offset){
        this.offset = offset;
        if(adapter!=null){
            adapter.setRightOffset(offset);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        Thumb thumb = (Thumb) v.getTag();
        if(imageClickListener!=null)
            imageClickListener.onImageClick((ImageView) v, thumb);
        else {
            if(zoom){
                if(thumbToImage == null && context instanceof Activity){
                    thumbToImage = new ThumbToImage((Activity) context);
                }
                if(thumbToImage!=null){
                    thumbToImage.setBackgroundColor(backColor);
                    thumbToImage.zoom((ImageView) v, viewPager.getCurrentItem(), thumbList, zoomPageListener);
                }
            }
        }
    }

    public int getPosition() {
        return viewPager.getCurrentItem();
    }

    public void setZoom(boolean zoom) {
        this.zoom = zoom;
    }

    public ViewPager getViewPager() {
        return viewPager;
    }

    public void setImageScale(ImageView.ScaleType scale) {
        this.scale = scale;
    }

    public boolean closeImage() {
        if(thumbToImage!=null)
            return thumbToImage.closeImage();
        return false;
    }

    public void setBackgroundColor(int color) {
        backColor = color;
    }

    public interface OnImageClickListener{
        public void onImageClick(ImageView v, Thumb thumb);
    }
}
