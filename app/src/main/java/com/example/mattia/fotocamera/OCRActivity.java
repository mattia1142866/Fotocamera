package com.example.mattia.fotocamera;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

/*
    @author Fasan Giovanni
 */
public class OCRActivity extends AppCompatActivity {
    Bundle extras;
    private ProgressDialog mProgressDialog;
    //OcrManager tessOCR = new OcrManager();
    TessOCR tessOCR = new TessOCR();
    TextView t;

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
        //Initialize the OCR library
        //tessOCR.initAPI();

        //Get the photo's path from MainActivity
        extras = getIntent().getExtras();
        String path=extras.getString("PATH_I_NEED");
        //Decode the file in the format that tesserect request
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        //Obtaining the text fro the OCR
        t = findViewById(R.id.textView);
        t.setMovementMethod(new ScrollingMovementMethod());
        //String testo = manager.getTextFromImg(bitmap);
        doOCR(bitmap);
        //Creating the view of the activity
        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageBitmap(bitmap);
    }



    public void doOCR(final Bitmap bitmap) {
        /*if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(this, "Processing",
                    "Please wait...", true);
        }
        else {
            mProgressDialog.show();
        }
        */
        t.setText("Processing....Please wait....");
        Log.d("OCRActivity", "Prima del thread");
        new Thread(new Runnable() {
            public void run() {
                Log.d("OCRActivity", "Run");
                final String result = tessOCR.getOCRResult(bitmap).toLowerCase();


                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        if (result != null && !result.equals("")) {
                            String s = result.trim();
                            Log.d("OCRActivity", "Find: "+s);
                            t.setText(s);
                        }
                       // mProgressDialog.dismiss();
                    }

                });

            };
        }).start();

    }

}
