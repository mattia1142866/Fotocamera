package com.example.mattia.fotocamera;

import android.graphics.Bitmap;

import com.googlecode.tesseract.android.TessBaseAPI;

public class OcrManager {

    TessBaseAPI baseAPI = null;

    public void  initAPI(){
        baseAPI = new TessBaseAPI();
        String dataPath = MainApplication.instance.getTessDataParentDirectory();
        baseAPI.init(dataPath,"ita"); //first param is datapath which is part to the your trainned data, second is language code
        //now, your trained data stored in asset folder, we need to cpy it to another exernal storage folder
        //It is better do this work when application start firt time
    }

    public String startRecognizer(Bitmap bitmap){
        if(baseAPI==null){
            initAPI();
        }
        baseAPI.setImage(bitmap);
        return baseAPI.getUTF8Text();
    }

}
