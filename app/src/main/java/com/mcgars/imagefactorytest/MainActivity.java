package com.mcgars.imagefactorytest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.Toast;

import com.mcgars.imagefactory.PagerImageController;
import com.mcgars.imagefactory.ThumbToImage;
import com.mcgars.imagefactory.cutomviews.ImageFactoryView;
import com.mcgars.imagefactory.objects.Thumb;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PagerImageController.OnImageClickListener {

    private ThumbToImage thumbToImage;
    private ImageFactoryView imgFactoryClicked;
    private ImageFactoryView imgFactoryWithZoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        thumbToImage = new ThumbToImage(MainActivity.this);

        // default
        ImageFactoryView pager = (ImageFactoryView) findViewById(R.id.imgFactory);
        pager.setList(getList());

        // default zoom
        imgFactoryWithZoom = (ImageFactoryView) findViewById(R.id.imgFactoryWithZoom);
        imgFactoryWithZoom
                .setImageScale(ImageView.ScaleType.CENTER_INSIDE)
                .setZoom(true)
                .setList(getList());

        // custom click, offset
        imgFactoryClicked = (ImageFactoryView) findViewById(R.id.imgFactoryClicked);
        imgFactoryClicked.setRightOffset(.9f);
        imgFactoryClicked.setList(getList(), this);

        // custom zoom
        final ImageFactoryView imgFactoryClickedZoom = (ImageFactoryView) findViewById(R.id.imgFactoryClickedZoom);
        imgFactoryClickedZoom.setList(getList(), new PagerImageController.OnImageClickListener() {
            @Override
            public void onImageClick(ImageView v, Thumb thumb) {

                showToast("Custom zoom");
                thumbToImage.zoom(v, imgFactoryClickedZoom);
            }
        });
    }

    List<Thumb> getList(){
        List<Thumb> list = new ArrayList<>();
        String url = "https://vk.com/images/pics/nichosi_2x.png";
        list.add(new Thumb(url,url));
        url = "http://goodimg.ru/img/kartinki-jpg2.jpg";
        list.add(new Thumb(url,url));
        url = "http://habrastorage.org/storage/habraeffect/8e/de/8ede5c77f2055b9374613f69b39c8e1c.png";
        list.add(new Thumb(url, url));
        url = "http://vignette2.wikia.nocookie.net/rift/images/7/7f/AWESOME_FACE!!!.png/revision/latest?cb=20110302225528";
        list.add(new Thumb(url, url));
        url = "http://www.koreanrandom.com/forum/uploads/monthly_11_2012/post-7163-0-27196200-1352682609.png";
        list.add(new Thumb(url, url));
        return list;
    }

    @Override
    public void onImageClick(ImageView v, Thumb thumb) {
        showToast(thumb.getThumb());
    }

    private void showToast(String msg){
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 50);
        toast.show();
    }

    @Override
    public void onBackPressed() {

        if(imgFactoryWithZoom.closeImage())
            return;
        if(thumbToImage.closeImage())
            return;

        super.onBackPressed();
    }
}
