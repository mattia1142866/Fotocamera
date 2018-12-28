package com.example.mattia.fotocamera.OCRManager;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;


class Tesseract extends Application implements TextRecognizer{
    private TessBaseAPI mTess;
    String datapath;
    String language;


    /**
     * @author Giovanni Fasan (g1)
     * Costrunctor
     *
     */
    public Tesseract() {
        // TODO Auto-generated constructor stub
        mTess = new TessBaseAPI();
        datapath = Environment.getExternalStorageDirectory() + "/DemoOCR/";
        language = "ita";
        File dir = new File(datapath + "/tessdata/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        loadAsset();
        mTess.init(datapath, language);
    }


    /**
     * @param bitmap
     * @return
     * @author Giovanni Fasan (g1)
     */
    @Override
    public String getTextFromImg(Bitmap bitmap) {
        mTess.setImage(bitmap);
        String result = mTess.getUTF8Text();
        return result;
    }

    /**
     * @author Giovanni Fasan (g1)
     */
    private void loadAsset(){
        AssetManager assetManager = getAssets();
        OutputStream out = null;
        try{
            Log.d("MainApplication", "CopyTessDataForTextRecognizor");
            //Trying ot open the trained data
            InputStream in = assetManager.open("ita.traineddata");
            String tesspath = datapath;
            File tessFolder = new File(tesspath);
            if(!tessFolder.exists()) {
                tessFolder.mkdir();
            }
            String tessData = tesspath+"/tessdata/"+language+".traineddata";
            File tessFile = new File(tessData);
            if(!tessFile.exists()){
                //Create a new tessFile
                out = new FileOutputStream(tessData);
                byte[] buffer = new byte[1024];
                int read = in.read(buffer);
                while (read != -1){
                    out.write(buffer, 0, read);
                    read = in.read(buffer);
                }
                Log.d("MainApplication", " Did finish copy tess file ");
            }
            else{
                Log.d("MainApplication", " tess file exist ");
            }
        }catch(Exception e){
            Log.d("MainApplication", "couldn't copy with the following error :"+e.toString());
        }
        finally{
            try{
                if(out!=null){
                    out.close();
                }
            }catch(Exception exx){

            }
        }
    }

}
