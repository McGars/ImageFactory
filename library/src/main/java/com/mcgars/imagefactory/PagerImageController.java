package com.mcgars.imagefactory;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.mcgars.imagefactory.objects.IThumb;
import com.mcgars.imagefactory.objects.Thumb;
import com.nostra13.universalimageloader.core.assist.FailReason;
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
    private ImageFragmentAdapter adapter;
    private ThumbPagerAdapter adapterThumb;
    private OnImageClickListener imageClickListener;
    private ThumbToImage thumbToImage;
    private List<? extends IThumb> thumbList;
    private boolean zoom;
    private ImageView.ScaleType scale = ImageView.ScaleType.CENTER_CROP;
    private float offset = 1f;
    private ViewPager.OnPageChangeListener zoomPageListener;
    private int backColor;
    private View endView;
    private ImageView currentImageSelected;
    private Activity activity;

    public PagerImageController(Context context, ViewPager viewPager) {
        this.context = context;
        this.viewPager = viewPager;
        inflater = LayoutInflater.from(context);
    }

    public PagerImageController setClickImage(ImageView clickImage) {
        this.clickImage = clickImage;
        return this;
    }

    public PagerImageController setPageListener(ViewPager.OnPageChangeListener listener) {
        return setPageListener(listener, false);
    }

    public PagerImageController setOnImageClickListener(OnImageClickListener imageClickListener) {
        this.imageClickListener = imageClickListener;
        return this;
    }

    public PagerImageController setPageListener(ViewPager.OnPageChangeListener listener, boolean isAdd) {
        if (!isAdd)
            viewPager.clearOnPageChangeListeners();
        if (listener != null) {
            viewPager.removeOnPageChangeListener(listener);
            viewPager.addOnPageChangeListener(listener);
        }
        return this;
    }

    public PagerImageController setZoomPageListener(ViewPager.OnPageChangeListener zoomPageListener) {
        this.zoomPageListener = zoomPageListener;
        return this;
    }

    public void setList(int selectedPosition, List<? extends IThumb> list) {
        setList(selectedPosition, list, true);
    }

    public List<? extends IThumb> getThumbList() {
        return thumbList;
    }

    public void setList(int selectedPosition, List<? extends IThumb> list, final boolean isThumb) {
        thumbList = list;

        if(!isThumb){
            for (int i = 0; i < list.size(); i++) {
                IThumb thumb = list.get(i);
                if (thumb.getPosition() < 0)
                    thumb.setPosition(i);
            }
            adapter = new ImageFragmentAdapter(((AppCompatActivity) context).getSupportFragmentManager(),
                    list, isThumb);
            adapter.setRightOffset(offset);
            viewPager.setAdapter(adapter);
        } else {

            List<View> views = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                IThumb thumb = list.get(i);
                if (thumb.getPosition() < 0)
                    thumb.setPosition(i);
                View v = inflater.inflate(R.layout.view_imagefactory_image, null);
                ImageView image = (ImageView) v.findViewById(R.id.image);
                image.setScaleType(scale);
                final View loader = v.findViewById(R.id.pbLoader);

                image.setTag(thumb);
                image.setOnClickListener(this);
                FactoryTool.initImageLoader(context).displayImage(isThumb ? thumb.getThumb() : thumb.getOrigin(), image, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        loader.setVisibility(View.GONE);

                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        loader.setVisibility(View.GONE);
                    }
                });
                views.add(v);
            }
            if (endView != null)
                views.add(endView);
            adapterThumb = new ThumbPagerAdapter(views);
            adapterThumb.setRightOffset(offset);
            viewPager.setAdapter(adapterThumb);
        }

        iniPagerMargin();
        viewPager.setCurrentItem(selectedPosition);

    }

    private void iniPagerMargin() {
        if (offset < 1) {
            viewPager.setPageMargin(
                    (int) context.getResources()
                            .getDimension(R.dimen.padding8));
        } else {
            viewPager.setPageMargin(0);
        }
    }

    public void setEndView(View v) {
        endView = v;
    }

    public void setRightOffset(float offset) {
        this.offset = offset;
        if (adapter != null) {
            adapter.setRightOffset(offset);
            adapter.notifyDataSetChanged();
        }
        if(adapterThumb!=null){
            adapterThumb.setRightOffset(offset);
            adapterThumb.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        IThumb thumb = (IThumb) v.getTag();
        if (imageClickListener != null)
            imageClickListener.onImageClick((ImageView) v, thumb);
        else {
            if (zoom) {
                if (thumbToImage == null) {
                    if(context instanceof Activity)
                        thumbToImage = new ThumbToImage((Activity) context);
                    else if (activity!=null)
                        thumbToImage = new ThumbToImage(activity);
                }
                if (thumbToImage != null) {
                    thumbToImage.setBackgroundColor(backColor);
                    thumbToImage.zoom((ImageView) v, thumb.getPosition(), thumbList, zoomPageListener);
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
        if (thumbToImage != null)
            return thumbToImage.closeImage();
        return false;
    }

    public void setBackgroundColor(int color) {
        backColor = color;
    }

    public ImageView.ScaleType getScaleType() {
        return scale;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public class ImageFragmentAdapter extends FragmentStatePagerAdapter {
        private List<? extends IThumb> list;
        private boolean isThumb;
        private float rightOffset;

        public ImageFragmentAdapter(FragmentManager fm, List<? extends IThumb> list, boolean isThumb) {
            super(fm);
            this.list = list;
            this.isThumb = isThumb;
        }

        @Override
        public Fragment getItem(int i) {
            ImageFragment frag = ImageFragment.newInstance(list.get(i));
            frag.setIsThumb(isThumb);
            frag.setScale(scale);
            frag.setOnClickListener(PagerImageController.this);
            return frag;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        /**
         *
         * @param offset From 0 to 1
         */
        public void setRightOffset(float offset){
            if(offset <0 || offset > 1)
                rightOffset = 1;
            else
                rightOffset = offset;
        }

        @Override
        public float getPageWidth(int position) {
            return (rightOffset);
        }
    }

    public interface OnImageClickListener {
        public void onImageClick(ImageView v, IThumb thumb);
    }
}
