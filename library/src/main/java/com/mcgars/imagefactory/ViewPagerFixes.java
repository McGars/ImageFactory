package com.mcgars.imagefactory;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Владимир on 20.08.2015.
 */
public class ViewPagerFixes extends ViewPager {

    public ViewPagerFixes(Context context) {
        super(context);
    }

    public ViewPagerFixes(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        try {
            return super.onInterceptTouchEvent(ev);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
