package com.mcgars.imagefactory.cutomviews;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mcgars.imagefactory.FactoryTool;
import com.mcgars.imagefactory.PagerImageController;
import com.mcgars.imagefactory.R;
import com.mcgars.imagefactory.objects.IThumb;
import com.mcgars.imagefactory.objects.Thumb;

import java.util.List;
import java.util.Random;

/**
 * Created by Владимир on 19.08.2015.
 */
public class ImageFactoryView extends FrameLayout implements ViewPager.OnPageChangeListener {

    protected CircleTabsView tabs;
    protected PagerImageController pagerController;

    public ImageFactoryView(Context context) {
        this(context, null);
    }

    public ImageFactoryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public ImageFactoryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ImageFactoryView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    /**
     * If context not instance of Activity
     * @param activity
     */
    public void setActivity(Activity activity){
        pagerController.setActivity(activity);
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        ViewPager viewPager = (ViewPager) inflater.inflate(R.layout.view_imagefactory_pager, this, false);
        // if exist many viewPagers
        viewPager.setId(new Random().nextInt(1000) + 65);
        FrameLayout.LayoutParams params = (LayoutParams) viewPager.getLayoutParams();
        viewPager.setLayoutParams(params);
        addView(viewPager);
        pagerController = new PagerImageController(getContext(), viewPager);

        View shadow = new View(getContext());
        shadow.setBackgroundResource(R.drawable.imagefactory_shadow);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, FactoryTool.pxToDp(80, getContext()),  Gravity.BOTTOM);
        shadow.setLayoutParams(params);
        addView(shadow);

        // Circle paging
        tabs = new CircleTabsView(getContext());
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,  Gravity.BOTTOM);
        tabs.setLayoutParams(params);
        addView(tabs);

        pagerController.setZoomPageListener(this);
    }

    public PagerImageController getPagerController() {
        return pagerController;
    }

    public CircleTabsView getTabs() {
        return tabs;
    }

    /**
     * Hide circle pagging view
     * @param visible
     */
    public void setVisibilityPagging(boolean visible){
        FactoryTool.setVisibleGone(!visible, tabs);
    }

    public void setPagerListener(ViewPager.OnPageChangeListener listener){
        pagerController.setPageListener(listener, true);
    }

    /**
     * Offset page from right
     * from 0f to 1f;
     * @param offset
     */
    public void setRightOffset(float offset){
        pagerController.setRightOffset(offset);
    }

    public void setList(List<? extends IThumb> list){
        setList(0, list, null);
    }

    public void setList(List<? extends IThumb> list, PagerImageController.OnImageClickListener listener){
        setList(0, list, listener);
    }

    public void setList(int position, List<? extends IThumb> list, PagerImageController.OnImageClickListener listener){
        pagerController.setOnImageClickListener(listener);
        pagerController.setList(position, list);
        tabs.setViewPager(pagerController.getViewPager());
    }

    public int getPosition() {
        return pagerController.getPosition();
    }

    public List<? extends IThumb> getThumbList(){
        return pagerController.getThumbList();
    }

    /**
     * Enable default click on image and zoom her
     * to front
     * @param b
     * @return
     */
    public ImageFactoryView setZoom(boolean b) {
        pagerController.setZoom(b);
        return this;
    }
    public ImageFactoryView setImageScale(ImageView.ScaleType scale) {
        pagerController.setImageScale(scale);
        return this;
    }

    /**
     * ZoomOut
     * @return
     */
    public boolean closeImage() {
        return pagerController.closeImage();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        pagerController.getViewPager().setCurrentItem(position);
//        tabs.onPageSelected(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void setBackgroundColor(int color){
        pagerController.setBackgroundColor(color);
    }

    public void setEndView(View v){
        pagerController.setEndView(v);
    }
}
