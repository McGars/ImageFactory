package com.mcgars.imagefactory.cutomviews;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.mcgars.imagefactory.FactoryTool;
import com.mcgars.imagefactory.R;

/**
 * Created by Владимир on 06.07.2015.
 */
public class CircleTabsView extends LinearLayout implements ViewPager.OnPageChangeListener {
    private int size;
    private int selectedColor;
    private int selected;
    private int radius;

    public CircleTabsView(Context context) {
        this(context, null);
    }

    public CircleTabsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public CircleTabsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        selectedColor = getContext().getResources().getColor(FactoryTool.getAttributeResourceId(getContext(), R.attr.colorAccent));
        radius = FactoryTool.pxToDp(8, getContext());
    }

    public void setViewPager(ViewPager viewPager) {
        size = viewPager.getAdapter().getCount();
        viewPager.removeOnPageChangeListener(this);
        viewPager.addOnPageChangeListener(this);
        buildCircle();
    }

    private void buildCircle() {
        removeAllViews();
        for (int i = 0; i < size; i++) {
            View v = new View(getContext());
            LayoutParams params = new LayoutParams(radius, radius);
            params.setMargins(radius, radius, 0, radius);
            v.setLayoutParams(params);
            Drawable icon = getContext().getResources().getDrawable(R.drawable.tabs_shape);
            setColorCircle(icon, i == selected);
            v.setBackgroundDrawable(icon);
            addView(v);
        }
    }

    private void refresh(){
        for (int i = 0, size = getChildCount(); i < size; i++) {
            View v = getChildAt(i);
            Drawable icon = v.getBackground();
            icon.clearColorFilter();
            setColorCircle(icon, i == selected);
        }
    }

    private void setColorCircle(Drawable drawable, boolean isSelected){
        if (isSelected)
            drawable.setColorFilter(selectedColor, PorterDuff.Mode.SRC_ATOP);
        else
            drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        selected = position;
        refresh();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
