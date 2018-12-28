package com.example.mattia.fotocamera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class ResultActivity extends AppCompatActivity {

    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        extras = getIntent().getExtras();
        String path=extras.getString("PATH_I_NEED");

        Bitmap bitmap = BitmapFactory.decodeFile(path);

        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageBitmap(bitmap);


    }
}
