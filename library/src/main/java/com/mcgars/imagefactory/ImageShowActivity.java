package com.mcgars.imagefactory;

import android.os.Bundle;

public class ImageShowActivity extends android.support.v7.app.AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagefactory_image);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, ImageShowFragment.newInstance(getIntent().getExtras()));
    }
}
