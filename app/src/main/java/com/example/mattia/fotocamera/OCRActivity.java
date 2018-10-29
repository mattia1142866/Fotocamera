package com.example.mattia.fotocamera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

/*
@author Fasan Giovanni
@param


 */
public class OCRActivity extends AppCompatActivity {

    Bundle extras;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);
        OcrManager manager = new OcrManager();
        //initialize OCR library
        manager.initAPI();
        //Intent intent=getIntent();
        extras = getIntent().getExtras();
        //get a photo's path from MainActivity
        String path=extras.getString("PATH_I_NEED");
        System.out.println(path);
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        //bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        String testo = manager.getTextFromImg(bitmap);
        TextView t= findViewById(R.id.textView);
        t.setText(testo);
        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageBitmap(bitmap);
    }
}
