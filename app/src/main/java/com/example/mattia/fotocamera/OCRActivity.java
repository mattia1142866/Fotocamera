package com.example.mattia.fotocamera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class OCRActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);
        OcrManager manager = new OcrManager();
        manager.initAPI();
        Intent intent=getIntent();
        String path=intent.getStringExtra(MainActivity.PATH);
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        String testo = manager.startRecognizer(bitmap);
        TextView t= findViewById(R.id.textView);
        t.setText(testo);
        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageBitmap(bitmap);

    }
}
