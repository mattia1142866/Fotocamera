package com.example.mattia.fotocamera;

import android.graphics.Bitmap;

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
    public String getTextFromImg(Bitmap image){
        if(baseAPI==null){
            initAPI();
        }
        baseAPI.setImage(image);
        return baseAPI.getUTF8Text();
    }

}
