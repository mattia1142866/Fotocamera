package com.example.mattia.fotocamera;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

public class TessOCR {

    private final TessBaseAPI mTess;

    public TessOCR(Context context, String language) {
        mTess = new TessBaseAPI();
        //String datapath = context.getFilesDir() + "/tesseract/";
        //String datapath = MainApplication.instance.getTessDataParentDirectory();
        String datapath = context.getExternalFilesDir(null).getAbsolutePath()+"/tesseract/";
        Log.d("AndroidCameraApi", datapath);
        mTess.init(datapath, language);
    }


    /*
     * @author Giovanni Fasan(g1)
     * @param Bitmap image we want to recognize
     * @return String of the text found in the image
    */
    public String getOCRResult(Bitmap bitmap) {
        mTess.setImage(bitmap);
        return mTess.getUTF8Text();
    }

    public void onDestroy() {
        if (mTess != null) mTess.end();
    }

}
