package com.example.mattia.fotocamera;

import android.graphics.Bitmap;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;

public class OcrManager {

    TessBaseAPI baseAPI = null;

    /*
        The method that initialize the OCR with a predefined language
     */
    public void  initAPI(){
        baseAPI = new TessBaseAPI();
        //Obtaining the path where the trained data are saved
        String dataPath = MainApplication.instance.getTessDataParentDirectory();
        //Initializing the OCR library with a prefixed language
        baseAPI.init(dataPath,"ita");
    }

    /*
        @author Fasan Giovanni
        @param Bitmap image we want to recognize
        @return String of the text found in the image
    */
    public String getOCRResult(Bitmap image){
        if(baseAPI==null){
            initAPI();
        }
        Log.d("OCRActivity", "prima del setImage");
        baseAPI.setImage(image);
        Log.d("OCRActivity", "immagine settata");
        String txt= baseAPI.getUTF8Text();
        Log.d("OCRActivity", "testo trovato");
        return txt;
    }
}
