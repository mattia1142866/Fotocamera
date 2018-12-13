package com.example.mattia.fotocamera;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.InputStream;

/*
@author Fasan Giovanni
@param


 */
public class OCRActivity extends AppCompatActivity {

    private static final String TAG = "AndroidCameraApi";
    Bundle extras;
    String language = "ita";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);

        final InputStream database = getResources().openRawResource(R.raw.database);
        TessOCR mTessOCR = new TessOCR (this, language);
        Log.d(TAG, "OCRActivity");

        /*OcrManager manager = new OcrManager();
        //initialize OCR library
        manager.initAPI();*/


        extras = getIntent().getExtras();
        //get a photo's path from MainActivity
        String path=extras.getString("PATH_I_NEED");
        Log.d(TAG, "OCRActivity");
        Bitmap img_photo = BitmapFactory.decodeFile(path);
        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageBitmap(img_photo);


        //recognize the text from image
        //String text = manager.getTextFromImg(img_photo);
        /*String text = mTessOCR.getOCRResult(img_photo);


        TextView textView_text= findViewById(R.id.textView_text);
        textView_text.setText(text);


        Inci ingredients = new Inci(database);
        TextView textView_inci = findViewById(R.id.textView_ingredients);
        textView_inci.setText(ingredients.listIngredientsToString());*/

    }

}
