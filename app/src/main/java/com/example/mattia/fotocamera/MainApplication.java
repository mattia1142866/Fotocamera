package com.example.mattia.fotocamera;

import android.app.Application;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class MainApplication extends Application {
    public static MainApplication instance = null;

    /*
        Called when the activity is first created. This is where you should do all of your normal static
        set up: create views, bind data to lists, etc. This method also provides you with a Bundle
        containing the activity's previously frozen state, if there was one.
     */
    public void onCreate(){
        super.onCreate();
        //Starting copy file here
        instance=this;
        copyTessDataForTextRecognizor();
    }

    /*
        @author Fasan Giovanni
        @return String of tess's data path
    */
    private String tessDataPath(){
        Log.d("path", MainApplication.instance.getExternalFilesDir(null)+"/tessdata/");
        return MainApplication.instance.getExternalFilesDir(null)+"/tessdata/";
    }

    /*
        @author Fasan Giovanni
        @return String of tess's data parent directory
    */
    public String getTessDataParentDirectory(){
        Log.d("path", MainApplication.instance.getExternalFilesDir(null).getAbsolutePath());
        return MainApplication.instance.getExternalFilesDir(null).getAbsolutePath();
    }

    /*
        @author Fasan Giovanni
        The method that allow the OCR to read the trained data
    */
    private void copyTessDataForTextRecognizor(){
        Runnable run = new Runnable() {
            /*
                When an object implementing interface Runnable is used to create a thread, starting
                the thread causes the object's run method to be called in that separately executing thread.
             */

            public void run() {
                //Provides the access to the assets file
                AssetManager assetManager = MainApplication.instance.getAssets();
                OutputStream out = null;
                try{
                    Log.d("MainApplication", "CopyTessDataForTextRecognizor");
                    //Trying ot open the trained data
                    InputStream in = assetManager.open("ita.traineddata");
                    String tesspath = instance.tessDataPath();
                    File tessFolder = new File(tesspath);
                    if(!tessFolder.exists()) {
                        tessFolder.mkdir();
                    }
                    String tessData = tesspath+"/"+"ita.traineddata";
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
        };
        new Thread(run).start();
    }

}
