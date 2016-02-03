package com.mepsan.callcenter.casa;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageView;

import java.io.File;

public class FullImageActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_image);

        // get intent data
        Intent i = getIntent();

        // Selected image id
        int position = i.getExtras().getInt("id");
        String adi=i.getExtras().getString("resim_adi");


        ImageView imageView = (ImageView) findViewById(R.id.full_image_view);
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/casa/" + adi + ".jpg");
        if (file.exists()) {//resim varsa
            imageView.setImageBitmap(BitmapFactory.decodeFile(file.getPath()));
        }
    }

}