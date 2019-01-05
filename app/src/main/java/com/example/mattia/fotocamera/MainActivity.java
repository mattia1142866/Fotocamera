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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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

public class MainActivity extends Activity {

  //declaring variables
  public static final int CAMERA_REQUEST_CODE = 3142325;

  TextureView textureView;
  CameraManager cameraManager;
  Size size;
  String cameraId;
  CameraDevice cameraDevice;
  Handler backgroundHandler;
  HandlerThread handlerThread;
  //  callbackobjects for receiving updates about the state of the camera
  CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
      @Override
      public void onOpened(@NonNull CameraDevice camera) {
          cameraDevice= camera;
      }

      @Override
      public void onDisconnected(@NonNull CameraDevice cameraDevice) {

      }

      @Override
      public void onError(@NonNull CameraDevice cameraDevice, int i) {

      }
  };

   protected void onCreate(Bundle saveInstanceState){
     super.onCreate(saveInstanceState);
     setContentView(R.layout.activity_main);

     //initialize the texture view
     textureView = findViewById(R.id.texture);


   }

   @Override
   protected void onResume() {
      super.onResume();
      textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
          @Override
          public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
              openBackgroundHandler();
              setupCamera();
              openCamera();
          }

          @Override
          public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {

          }

          @Override
          public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
              return false;
          }

          @Override
          public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

          }
      });
   }

   @Override
   protected void onPause() {
      super.onPause();

      closeBackgroundHandler();
   }

   /**
    *
    * @author Leonardo Pratesi
    */
   private void openCamera() {
       //check permissions
       if (Build.VERSION.SDK_INT >= 23) {
           if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
               requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
           } else {
               try {

                   cameraManager.openCamera(cameraId, stateCallback, backgroundHandler);
               } catch (CameraAccessException e) {
                   e.printStackTrace();
               }
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
         e.printStackTrace();
       }
   }
    //add the surface to show the stream from camera
   private void createCaptureSession() {
       SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
       surfaceTexture.setDefaultBufferSize(size.getWidth(), size.getHeight());
       Surface surface = new Surface(surfaceTexture);
        try{


       cameraDevice.createCaptureSession(Collections.singletonList(surface), new CameraCaptureSession.StateCallback() {
           @Override
           public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
               try {
                   CaptureRequest.Builder captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                   cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), null, backgroundHandler);

               }
               catch (CameraAccessException e) {
                   e.printStackTrace();
               }
               }

           @Override
           public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {

           }
       }, backgroundHandler);
        }
        catch (CameraAccessException e) {
            e.printStackTrace();
       }

   }

}
