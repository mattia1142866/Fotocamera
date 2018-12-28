package com.example.mattia.fotocamera.OCRManager;

import com.googlecode.tesseract.android.TessBaseAPI;

class Tesseract implements TextRecognizer {
    private TessBaseAPI mTess;
    String datapath;
    String language;
}
