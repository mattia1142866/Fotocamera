package com.example.mattia.fotocamera.OCRManager;

import android.graphics.Bitmap;

public interface TextRecognizer {

    String getTextFromImg(Bitmap bitmap);

}
