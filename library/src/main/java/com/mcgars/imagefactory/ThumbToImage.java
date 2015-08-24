package com.mcgars.imagefactory;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.mcgars.imagefactory.animation.BaseAnimationListener;
import com.mcgars.imagefactory.cutomviews.ImageFactoryView;
import com.mcgars.imagefactory.objects.Thumb;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;


/**
 * Created by Владимир on 21.04.2015.
 */
public class ThumbToImage {
    private final ImageLoader iMageLoader;
    private Activity mContext;
    private AnimatorSet mCurrentAnimator;
    boolean isLoad = false;
    private ViewGroup root;
    private View back;
    private ImageView expandedImage;
    private ProgressBar pbLoaderExpanded;
    private Rect startBounds;
    private Rect finalBounds;
    private Point globalOffset;
    float startScale;
    RelevalCircular rev;
    private View wraper;
    PhotoViewAttacher mAttacher;
    private ViewPager viewPager;
    private ImageView thumbView;
    private List<Thumb> list;
    private Class<? extends ImageShowActivity> acticityClass = ImageShowActivity.class;
    private PagerImageController controller;
    private int topOffest;

    public ThumbToImage(Activity mContext) {
        this.mContext = mContext;
        iMageLoader = ImageLoader.getInstance();
        rev = new RelevalCircular(mContext);
        initRootView();
    }

    /**
     * If used toolbar set 0, else if action bar set action bar height
     * @param px
     */
    public void setTopOffset(int px){
        topOffest = px;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void zoom(final ImageView thumbView, List<Thumb> list) {
        zoom(thumbView, 0, list);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void zoom(final ImageView thumbView, List<Thumb> list, ViewPager.OnPageChangeListener listener) {
        zoom(thumbView, 0, list, listener);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void zoom(final ImageView thumbView, int selectedPosition, List<Thumb> list) {
        zoom(thumbView, selectedPosition, list, null);
    }

    public void zoom(ImageView v, ImageFactoryView imgFactory) {
        zoom(v, imgFactory.getPosition(), imgFactory.getThumbList(), imgFactory);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void zoom(final ImageView thumbView, int selectedPosition, List<Thumb> list, ViewPager.OnPageChangeListener listener) {
        this.thumbView = thumbView;
        this.list = list;
        FactoryTool.setVisible(wraper, back, viewPager);
        fadeIn(back);
        fadeIn(viewPager);

        controller = new PagerImageController(mContext, viewPager);
        controller.setClickImage(thumbView)
                .setPageListener(listener)
                .setList(selectedPosition, list, false);

        if (Build.VERSION.SDK_INT > 10) {
            initBounds();
            animateIn(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
//                    FactoryTool.setVisible(viewPager);
                    FactoryTool.setVisibleGone(expandedImage);
                    FactoryTool.setVisibleGone(pbLoaderExpanded);
                }
            });
        } else
            FactoryTool.setVisible(viewPager);
    }

    public int getImagePosition() {
        if (viewPager != null)
            return viewPager.getCurrentItem();
        return 0;
    }


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void zoom(final ImageView thumbView, String url) {
        if (url == null)
            return;
        this.thumbView = thumbView;

        isLoad = false;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            showInActivity(url);
            return;
        }
        mAttacher = new PhotoViewAttacher(expandedImage);
        FactoryTool.setVisible(wraper, expandedImage);
        fadeIn(back);
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
//        expandedImage.setOnClickListener(new View.OnClickListener() {
//            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//            @Override
//            public void onClick(View view) {
//                closeImage();
//            }
//        });

        if (pbLoaderExpanded != null)
            pbLoaderExpanded.setProgress(0);

        expandedImage.setImageDrawable(thumbView.getDrawable());
        mAttacher.update();
        if (Build.VERSION.SDK_INT > 20) {
            rev.setType(RelevalCircular.TYPE.VIEW);
            rev.startProgress(expandedImage, null);
            rev.setProgress(20);
        }

        if (!FactoryTool.getConnection(mContext)) {
            DisplayImageOptions.Builder options = FactoryTool.getImageLoaderOptionsBuilder();
            options.displayer(new SimpleBitmapDisplayer());
            options.resetViewBeforeLoading(false);

            iMageLoader.displayImage(url, expandedImage, options.build(), new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingFailed(String s, View view, FailReason failReason) {
                            if (Build.VERSION.SDK_INT > 20)
                                rev.setProgress(100);
                            mAttacher.update();
                        }

                        @Override
                        public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                            isLoad = true;
                            if (Build.VERSION.SDK_INT > 20)
                                rev.setProgress(100);
                            else if (pbLoaderExpanded.getVisibility() == View.VISIBLE)
                                FactoryTool.setVisibleGone(pbLoaderExpanded);
                            mAttacher.update();
                        }
                    },
                    new ImageLoadingProgressListener() {
                        @Override
                        public void onProgressUpdate(String url, View view, int count, int all_count) {
                            int progress = count * 100 / all_count;
                            if (Build.VERSION.SDK_INT > 20) {
                                if (progress < 100)
                                    rev.setProgress(progress);
                            } else
                                pbLoaderExpanded.setProgress(count * 100 / all_count);
                        }
                    }
            );

        } else {
            if (Build.VERSION.SDK_INT > 20)
                rev.setProgress(100);
        }

        if (Build.VERSION.SDK_INT > 20) {
            return;
        }

        //iMageLoader.displayImage(imageResId, expandedImageView);
//        fadeIn(expandedImage);
        initBounds();
        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        animateIn(null);
    }

    @SuppressLint("NewApi")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    void initBounds() {
        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        startBounds = new Rect();
        finalBounds = new Rect();
        globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        if (thumbView != null) {
            thumbView.getGlobalVisibleRect(startBounds);
            thumbView.getRootView()
                    .getGlobalVisibleRect(finalBounds, globalOffset);
            startBounds.offset(-globalOffset.x, -(globalOffset.y));
            finalBounds.offset(-globalOffset.x, -globalOffset.y);
        } else {
            root.getGlobalVisibleRect(finalBounds, globalOffset);
            startBounds.offset(globalOffset.x / 4, globalOffset.y / 4);
            finalBounds.offset(globalOffset.x / 2, globalOffset.y / 2);
        }


        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }


        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        //thumbView.setAlpha(0f);

        //expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        viewPager.setPivotX(0f);
        viewPager.setPivotY(0f);
        expandedImage.setPivotX(0f);
        expandedImage.setPivotY(0f);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    void animateIn(final AnimatorListenerAdapter listener) {
        boolean isViewPagerOpen = viewPager.getAdapter()!=null;
        View toAnimate = null;
        if(isViewPagerOpen){
            toAnimate = viewPager;
            FactoryTool.setVisible(viewPager);
        } else {
            toAnimate = expandedImage;
            FactoryTool.setVisible(expandedImage);
        }
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(toAnimate, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(toAnimate, View.Y,
                        startBounds.top - topOffest, finalBounds.top))
                .with(ObjectAnimator.ofFloat(toAnimate, View.SCALE_X,
                        startScale, 1f)).with(ObjectAnimator.ofFloat(toAnimate,
                View.SCALE_Y, startScale, 1f));
        set.setDuration(200);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
                if (isLoad)
                    return;
                fadeIn(pbLoaderExpanded);
//                YoYo.with(Techniques.SlideInDown)
//                        .playOn(pbLoaderExpanded);
                if (listener != null)
                    listener.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                FactoryTool.setVisibleGone(pbLoaderExpanded);
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public boolean closeImage() {
        if(mAttacher!=null){
            mAttacher.cleanup();
            mAttacher = null;
        }
        if (wraper.getVisibility() != View.VISIBLE)
            return false;

        FactoryTool.setVisibleGone(viewPager);
        if (Build.VERSION.SDK_INT < 11) {
            FactoryTool.setVisibleGone(wraper);
            if(viewPager.getAdapter()!=null)
                viewPager.setAdapter(null);
            return true;
        }

        boolean isViewPagerOpen = viewPager.getAdapter() !=null;
        View toAnimate = isViewPagerOpen ? viewPager : expandedImage;


        if (isViewPagerOpen) {

//            View v = ((ThumbPagerAdapter) viewPager.getAdapter()).getView(viewPager.getCurrentItem());
//            if (v != null) {
//                if (v instanceof ImageView)
//                    expandedImage.setImageDrawable(((ImageView) v).getDrawable());
//                else {
//                    ImageView image = (ImageView) v.findViewById(R.id.image);
//                    if (image != null)
//                        expandedImage.setImageDrawable(image.getDrawable());
//                }
//                mAttacher.update();
//            }
//            }
            fadeOut(viewPager);
        } else {
            FactoryTool.setVisible(expandedImage);
        }
        iMageLoader.stop();
        FactoryTool.setVisibleGone(pbLoaderExpanded);
        fadeOut(back);


        if (!isViewPagerOpen && Build.VERSION.SDK_INT > 20) {
            rev.closeReleval(new RelevalCircular.OnCircleEndAnimation() {
                @Override
                public void animateEnd() {
                    hideImages();
                }
            });
            return true;
        }

        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        if (thumbView == null) {
            hideImages();
            return true;
        }
        if (startBounds == null) {
            initBounds();
        }
        // Animate the four positioning/sizing properties in parallel,
        // back to their original values.
        fadeOut(expandedImage);
        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator
                .ofFloat(toAnimate, View.X, startBounds.left))
                .with(ObjectAnimator
                        .ofFloat(toAnimate,
                                View.Y, startBounds.top - topOffest))
                .with(ObjectAnimator
                        .ofFloat(toAnimate,
                                View.SCALE_X, startScale))
                .with(ObjectAnimator
                        .ofFloat(toAnimate,
                                View.SCALE_Y, startScale));
        set.setDuration(200);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                hideImages();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                hideImages();
            }
        });
        set.start();
        mCurrentAnimator = set;

        return true;
    }

    private void hideImages() {
        expandedImage.setImageDrawable(null);
        viewPager.setAdapter(null);
        FactoryTool.setVisibleGone(wraper);
        mCurrentAnimator = null;
    }

    public void setShowActivity(Class<? extends ImageShowActivity> acticityClass) {
        this.acticityClass = acticityClass;
    }

    public void showInActivity(String url) {
        ArrayList<String> list = new ArrayList<>();
        list.add(url);
        showInActivity(0, list);
    }

    public void showInActivity(int selectPosition, ArrayList<String> list) {
        Intent intent = new Intent(mContext, acticityClass);
        intent.putStringArrayListExtra(ImageShowFragment.LIST_STRING, list);
        intent.putExtra(ImageShowFragment.POSITION, selectPosition);
        mContext.startActivity(intent);
    }

    void initRootView() {
        ViewGroup decorView = (ViewGroup) mContext.findViewById(android.R.id.content);
        if (decorView != null) {
            View firstChaild = decorView.getChildAt(0);

            if ((firstChaild instanceof ScrollView) || !(firstChaild instanceof RelativeLayout) && !(firstChaild instanceof FrameLayout)) {
                FrameLayout rootViewNew = new FrameLayout(mContext);
                rootViewNew.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                decorView.removeView(firstChaild);
                rootViewNew.addView(firstChaild);
                decorView.addView(rootViewNew);
                root = rootViewNew;
            } else {
                root = (ViewGroup) firstChaild;
            }

            back = root.findViewById(R.id.llbackExpandedImage);
            if (back == null) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                wraper = inflater.inflate(R.layout.view_imagefactory_thumb_to_image, root, false);
                root.addView(wraper);
                back = root.findViewById(R.id.llbackExpandedImage);
            } else {
                wraper = (View) back.getParent();
            }
            expandedImage = (ImageView) root.findViewById(R.id.expanded_image);
            pbLoaderExpanded = (ProgressBar) root.findViewById(R.id.pbLoaderExpanded);
            viewPager = (ViewPager) root.findViewById(R.id.viewPagerActitivity);
        }
    }

    private void fadeIn(View v) {
        FactoryTool.setVisible(v);
        Animation in = AnimationUtils.loadAnimation(mContext, R.anim.imagefactory_fadein);
        v.startAnimation(in);
    }

    private void fadeOut(final View v) {
        Animation out = AnimationUtils.loadAnimation(mContext, R.anim.imagefactory_fadeout);
        out.setAnimationListener(new BaseAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                FactoryTool.setVisibleGone(v);
            }
        });
        v.startAnimation(out);
    }

    public void setBackgroundColor(int backgroundColor) {
        if (backgroundColor != 0)
            back.setBackgroundColor(backgroundColor);
    }
}
