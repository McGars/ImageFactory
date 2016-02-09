package com.mcgars.imagefactory;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mcgars.imagefactory.objects.IThumb;
import com.mcgars.imagefactory.objects.Thumb;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Владимир on 24.08.2015.
 */
public class ImageFragment extends Fragment {

    private ImageView image;
    private View pbLoader;
    private IThumb item;
    private PhotoViewAttacher mAtacher;
    private ImageLoader imageLoader;
    boolean isThumb;
    private ImageView.ScaleType scale = ImageView.ScaleType.CENTER_CROP;
    private View.OnClickListener clickListener;

    public static ImageFragment newInstance(String thumb, String origin){
        return newInstance(new Thumb(thumb, origin));
    }

    public static ImageFragment newInstance(IThumb item){
        ImageFragment frag = new ImageFragment();
        frag.setThumbItem(item);
        return frag;
    }

    public void setIsThumb(boolean isThumb){
        this.isThumb = isThumb;
    }

    private void setThumbItem(IThumb item) {
        this.item = item;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.view_imagefactory_image, null);
        image = (ImageView)v.findViewById(R.id.image);
        pbLoader = v.findViewById(R.id.pbLoader);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(item == null)
            return;

        image.setScaleType(scale);
        image.setTag(item);
        if (!isThumb)
            mAtacher = new PhotoViewAttacher(image);
        else
            image.setOnClickListener(clickListener);

        showImage();
    }

    private void showImage() {
        imageLoader = FactoryTool.initImageLoader(getActivity());
        imageLoader.displayImage(isThumb ? item.getThumb() : item.getOrigin(), image, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                refreshImage();
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                refreshImage();
            }
        });
    }

    private void refreshImage(){
        if (!isThumb && mAtacher!=null) {
            mAtacher.update();
        }
        FactoryTool.setVisibleGone(pbLoader);
    }

    @Override
    public void onDestroyView() {
        if(mAtacher!=null){
            mAtacher.cleanup();
            mAtacher = null;
        }
        if(imageLoader!=null)
            imageLoader.cancelDisplayTask(image);
        super.onDestroyView();

    }

    public void setScale(ImageView.ScaleType scale) {
        this.scale = scale;
    }

    public ImageView.ScaleType getScale() {
        return scale;
    }

    public void setOnClickListener(View.OnClickListener clickListener) {
        this.clickListener = clickListener;
    }
}
