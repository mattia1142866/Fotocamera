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
 */
public class OCRActivity extends AppCompatActivity {
    Bundle extras;

    /*
        Called when the activity is first created
        @param savedInstanceState If the activity is being re-initialized after
               previously being shut down then this Bundle contains the data it most
               recently supplied in onSaveInstanceState(Bundle). Otherwise it's null.
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);
        OcrManager manager = new OcrManager();
        //Initialize the OCR library
        manager.initAPI();
        //Get the photo's path from MainActivity
        extras = getIntent().getExtras();
        String path=extras.getString("PATH_I_NEED");
        //Decode the file in the format that tesserect request
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        //Obtaining the text fro the OCR
        String testo = manager.getTextFromImg(bitmap);
        //Creating the view of the activity
        TextView t= findViewById(R.id.textView);
        t.setText(testo);
        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageBitmap(bitmap);
    }
}
