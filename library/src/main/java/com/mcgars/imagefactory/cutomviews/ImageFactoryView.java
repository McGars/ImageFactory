package com.mcgars.imagefactory.cutomviews;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mcgars.imagefactory.FactoryTool;
import com.mcgars.imagefactory.PagerImageController;
import com.mcgars.imagefactory.R;
import com.mcgars.imagefactory.objects.Thumb;

import java.util.List;
import java.util.Random;

/**
 * Created by Владимир on 19.08.2015.
 */
public class ImageFactoryView extends LinearLayout implements ViewPager.OnPageChangeListener {

    private CircleTabsView tabs;
    private PagerImageController pagerController;

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

    private void init() {
        setOrientation(VERTICAL);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        ViewPager viewPager = (ViewPager) inflater.inflate(R.layout.view_imagefactory_pager, this, false);
        viewPager.setId(new Random().nextInt(1000) + 65);
        LinearLayout.LayoutParams params = (LayoutParams) viewPager.getLayoutParams();
        params.weight = 1;
        viewPager.setLayoutParams(params);
        addView(viewPager);

        pagerController = new PagerImageController(getContext(), viewPager);
        // Circle paging
        tabs = new CircleTabsView(getContext());
        addView(tabs);
        pagerController.setZoomPageListener(this);
    }

    public void setVisibilityPagging(boolean visible){
        FactoryTool.setVisibleGone(!visible, tabs);
    }

    public void setPagerListener(ViewPager.OnPageChangeListener listener){
        pagerController.setPageListener(listener, true);
    }

    public void setRightOffset(float offset){
        pagerController.setRightOffset(offset);
    }

    public void setList(List<Thumb> list){
        setList(0, list, null);
    }

    public void setList(List<Thumb> list, PagerImageController.OnImageClickListener listener){
        setList(0, list, listener);
    }

    public void setList(int position, List<Thumb> list, PagerImageController.OnImageClickListener listener){
        pagerController.setOnImageClickListener(listener);
        pagerController.setList(position, list);
        tabs.setViewPager(pagerController.getViewPager());
    }

    public int getPosition() {
        return pagerController.getPosition();
    }

    public List<Thumb> getThumbList(){
        return pagerController.getThumbList();
    }

    public ImageFactoryView setZoom(boolean b) {
        pagerController.setZoom(b);
        return this;
    }
    public ImageFactoryView setImageScale(ImageView.ScaleType scale) {
        pagerController.setImageScale(scale);
        return this;
    }

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
