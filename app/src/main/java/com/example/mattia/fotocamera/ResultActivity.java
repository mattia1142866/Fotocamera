package com.example.mattia.fotocamera;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;


public class ResultActivity extends AppCompatActivity {
    private TextView ocrTextView;
    private ImageView mImageView;
    private Bitmap lastPhoto;
    //private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        //UI components

        mImageView = findViewById(R.id.img_captured_view);

        ocrTextView = findViewById(R.id.ocr_text_view);

        FloatingActionButton fab = findViewById(R.id.newPictureFab);
    }
}
