package com.example.mattia.fotocamera;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

  //declaring variables
  public static final int CAMERA_REQUEST_CODE = 3142325;

  TextureView textureView;
  CameraManager cameraManager;
  Size size;
  String cameraId;
  Handler backgroundHandler;
  //HandlerThread handlerThread
  CameraDevide.StateCallback stateCallback =

   protected void onCreate(Bundle saveInstanceState){
     super.onCreate(saveInstanceState);
     setContentView(R.layout.activity_main);

     textureView = findViewById(R.id.texture);

   }

   /**
    *
    * @author
    */
   private void openCamera(){
        //check permissions
        if (Build.VERSION.SDK_INT  >= 23) {
          checkSelfPermission(Manifest.permissions.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            requestPermission(new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
          }
        else
        {
          CameraManager.openCamera(cameraId)
        }
        }

   }



   private void openBackgroundHandler(){
     HandlerThread handlerThread = new HandlerThread("camera_app");
     handlerThread.start();
     backgroundHandler = new Handler(handlerThread.getLooper());
     }



   private void closeBackgroundHandler(){
     handlerThread.quit();
     handlerThread = null;
     backgroundHandler = null;
   }




   private void setupCamera(){
       cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
       try{
          String[] cameraIds = cameraManager.getCameraIdList();

          for (String cameraId : cameraIds) {
            CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);

            if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == CameraMetadata.LENS_FACING_FRONT) {
              this.cameraId = cameraId;
              StreamConfigurationMap streamConfigurationMap = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
              size = streamConfigurationMap.getOutputSizes(SurfaceTexture.class)[0];
            }

          }

       }
       catch (CameraAccessException e) {
         e.printStackTrance();
       }
   }

}
