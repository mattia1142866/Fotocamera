package com.example.mattia.fotocamera;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class TessOCR {

    private TessBaseAPI mTess;
    String datapath;
    String language;

    public TessOCR() {
        // TODO Auto-generated constructor stub
        mTess = new TessBaseAPI();
        // AssetManager assetManager=
        datapath = Environment.getExternalStorageDirectory() + "/DemoOCR/";
        language = "ita";
        // AssetManager assetManager = getAssets();
        File dir = new File(datapath + "/tessdata/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        loadAsset();
        mTess.init(datapath, language);
    }

    public String getOCRResult(Bitmap bitmap) {
        Log.d("OCRActivity", "prima del setImage");
        mTess.setImage(bitmap);
        Log.d("OCRActivity", "immagine settata");
        String result = mTess.getUTF8Text();
        Log.d("OCRActivity", "testo trovato");
        return result;
    }

    public void onDestroy() {
        if (mTess != null)
            mTess.end();
    }

    private void loadAsset(){
        AssetManager assetManager = MainApplication.instance.getAssets();
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
