package com.example.mattia.fotocamera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Calendar;
import java.util.Date;
public class MainActivity extends AppCompatActivity {

    //Declaration of the variables
    private static final String TAG = "AndroidCameraApi";
    public static final String PATH="com.example.fotocamera.MESSAGE";
    private Button takePictureButton;
    private TextureView textureView;
    private String cameraId;
    protected CameraDevice cameraDevice;
    protected CameraCaptureSession cameraCaptureSessions;
    protected CaptureRequest.Builder captureRequestBuilder;
    private Size imageDimension;
    private ImageReader imageReader;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    /*
        Called when the activity is first created
        @param savedInstanceState If the activity is being re-initialized after
            previously being shut down then this Bundle contains the data it most
            recently supplied in onSaveInstanceState(Bundle). Otherwise it's null.
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //load the layout
        setContentView(R.layout.activity_main);
        textureView = (TextureView) findViewById(R.id.texture);
        assert textureView != null;
        //load the botton and camera layout
        textureView.setSurfaceTextureListener(textureListener);
        takePictureButton = (Button) findViewById(R.id.btn_takePhoto);
        assert takePictureButton != null;
        //when the button is pressed it takes the photo
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });
    }

    //This listener can be used to be notified when the surface texture associated with this texture view is available
    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        /*
            Invoked when a TextureView's SurfaceTexture is ready for use
            @param The surface returned by TextureView.getSurfaceTexture()
            @param Width is the width of the surface
            @param Weight is the height of the surface
        */
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            //open your camera
            openCamera();
        }

        /*
            Invoked when the SurfaceTexture's buffers size changed.
            @param The surface returned by TextureView.getSurfaceTexture()
            @param Width is the width of the surface
            @param Weight is the height of the surface
        */
        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            // Transform you image captured size according to the surface width and height
        }

        /*
            Invoked when the specified SurfaceTexture is about to be destroyed.
            @param surface The surface about to be destroyed
            @return If returns true, no rendering should happen inside the surface texture after this method is invoked
         */
        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        /*
            Invoked when the specified SurfaceTexture is updated through SurfaceTexture.updateTexImage().
            @param The surface just updated
         */
        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    };

    //A callback object for receiving updates about the state of a camera device.
    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        /*
            The method called when a camera device has finished opening.
            @param the camera device that has become opened. This value must never be null.
         */
        @Override
        public void onOpened(CameraDevice camera) {
            //This is called when the camera is open
            Log.e(TAG, "onOpened");
            cameraDevice = camera;
            createCameraPreview();
        }

        /*
            The method called when a camera device is no longer available for use.
            @param the device that has been disconnected. This value must never be null.
         */
        @Override
        public void onDisconnected(CameraDevice camera) {
            cameraDevice.close();
        }

        /*
            The method called when a camera device has encountered a serious error
            @param camera The device reporting the error. This value must never be null.
            @param error The error code
         */
        @Override
        public void onError(CameraDevice camera, int error) {
            cameraDevice.close();
            cameraDevice = null;
        }
    };

    //The method that starts a thread that controlls the looper when the camera is open
    protected void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    //The method that stops the thread when the camera is closed
    protected void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //The method called when we want to take a picture
    protected void takePicture() {
        //Verify if the object camera is Null
        if(null == cameraDevice) {
            Log.e(TAG, "cameraDevice is null");
            return;
        }
        //A system service manager for detecting, characterizing, and connecting to the camera devices
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            //Get the properties from the camera device
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
            //Create a vector that contains the sizes of the image format jpeg
            Size[] jpegSizes = null;
            //Initialize the vector
            if (characteristics != null) {
                jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
            }
            //Declaring the variables reassigning the value variables
            int width = 600;
            int height = 450;
            if (jpegSizes != null && 0 < jpegSizes.length) {
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }
            //Create a new reader for images of the desired size and format
            ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 2);
            List<Surface> outputSurfaces = new ArrayList<Surface>(2);
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(new Surface(textureView.getSurfaceTexture()));
            //A builder for capture requests
            final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            //Add a surface to the list of targets for this request
            captureBuilder.addTarget(reader.getSurface());
            //Set a capture request field to a value
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            // Getting the orientation of the window and putting in the captureBuilder
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
            //Creating a file with a specific path and name
            int currentTime = Calendar.getInstance().getTime().hashCode();
            final File file = new File(Environment.getExternalStorageDirectory()+"/"+Environment.DIRECTORY_PICTURES+"/camera2/"+currentTime+".jpg");
            //Create the folder
            file.mkdirs();
            //Verify that a file with the same name exists. If it exist it deletes and make a new one
            try {
                if(file.exists()){
                    file.delete();
                }
                file.createNewFile();
            } catch(IOException e){
                e.printStackTrace();
            }
            //Create intent for OCRActivity and passing the file's path
            final Intent intentOCR= new Intent(this,OCRActivity.class);
            intentOCR.putExtra("PATH_I_NEED", file.getPath());
            //Callback interface for being notified that a new image is available.
            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                /*
                    Callback that is called when a new image is available from ImageReader
                    @param reader the ImageReader the callback is associated with.
                 */
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image image = null;
                    //Trying to acquire the last image available and putting it on a vector to save it
                    try {
                        image = reader.acquireLatestImage();
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);
                        save(bytes);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (image != null) {
                            //Free up this frame for reuse
                            image.close();
                        }
                    }
                }

                /*
                    The method that save the image in the memory
                    @param bytes The vector that must be saved
                 */
                private void save(byte[] bytes) throws IOException {
                    FileOutputStream output = null;
                    try {
                        output = new FileOutputStream(file);
                        output.write(bytes);
                    } finally {
                        if (null != output) {
                            output.flush();
                            output.close();
                        }
                    }
                }
            };

            //Register a listener to be invoked when a new image becomes available from the ImageReader
            reader.setOnImageAvailableListener(readerListener, mBackgroundHandler);

            //Callback object for tracking the progress of a CaptureRequest submitted to the camera device
            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                /*
                    This method is called when an image capture has fully completed and all the result metadata is available.
                    @param session the session returned by CameraDevice.createCaptureSession(SessionConfiguration).
                           This value must never be null.
                    @param request The request that was given to the CameraDevice. This value must never be null.
                    @param result The total output metadata from the capture, including the final capture parameters
                           and the state of the camera system during capture.
                 */
                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    Toast.makeText(MainActivity.this, "Saved:" + file, Toast.LENGTH_SHORT).show();
                    //Starts the activity for the OCR recognition
                    startActivity(intentOCR);
                }
            };

            //Create a new camera capture session by providing the target output set of Surfaces to the camera device.
            cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                /*
                    This method is called when the camera device has finished configuring itself,
                    and the session can start processing capture requests.
                    @param session The session returned by CameraDevice.createCaptureSession(SessionConfiguration).
                           This value must never be null
                 */
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try {
                        //Submit a request for an image to be captured by the camera device.
                        session.capture(captureBuilder.build(), captureListener, mBackgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                /*
                    This method is called if the session cannot be configured as requested.
                    @param session The session returned by CameraDevice.createCaptureSession(SessionConfiguration).
                           This value must never be null
                 */
                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                }
            }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    //The mathod that create the camera preview
    protected void createCameraPreview() {
        try {
            //Creates a new surface identical at textureView
            SurfaceTexture texture = textureView.getSurfaceTexture();
            assert texture != null;
            //Set the default size of the image buffers
            texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());
            Surface surface = new Surface(texture);
            //Create a CaptureRequest.Builder for new capture requests, initialized with template for a target use case.
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            //Create a new camera capture session by providing the target output set of Surfaces to the camera device.
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback(){
                /*
                    This method is called when the camera device has finished configuring itself,
                    and the session can start processing capture requests.
                    @param session The session returned by CameraDevice.createCaptureSession(SessionConfiguration).
                           This value must never be null
                 */
                @Override
                public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                    //The camera is already closed
                    if (null == cameraDevice) {
                        return;
                    }
                    // When the session is ready, we start displaying the preview.
                    cameraCaptureSessions = cameraCaptureSession;
                    updatePreview();
                }

                /*
                    This method is called if the session cannot be configured as requested.
                    @param session The session returned by CameraDevice.createCaptureSession(SessionConfiguration).
                           This value must never be null
                 */
                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(MainActivity.this, "Configuration change", Toast.LENGTH_SHORT).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    //The method that allow to the camera to open
    private void openCamera() {
        //A system service manager for detecting, characterizing, and connecting to Camera devices.
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        Log.e(TAG, "is camera open");
        try {
            //Get the camera ID
            cameraId = manager.getCameraIdList()[0];
            //Get the characteristics of the camera
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
            imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];
            // Add permission for camera and let user grant the permission
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
                return;
            }
            //Opens the camera
            manager.openCamera(cameraId, stateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "openCamera X");
    }

    //The method that allows the update of the picture on the screen
    protected void updatePreview() {
        if(null == cameraDevice) {
            Log.e(TAG, "updatePreview error, return");
        }
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try {
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /*
        Callback for the result from requesting permissions.
        @param requestCode The request code passed in requestPermissions(android.app.Activity, String[], int)
        @param permissions The requested permissions. Never null.
        @param grantResults The grant results for the corresponding permissions which is either
               PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // close the app
                Toast.makeText(MainActivity.this, "Sorry!!!, you can't use this app without granting permission", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    /*
        Called when the activity will start interacting with the user. At this point your activity
        is at the top of the activity stack, with user input going to it.
    */
    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        startBackgroundThread();
        if (textureView.isAvailable()) {
            openCamera();
        } else {
            textureView.setSurfaceTextureListener(textureListener);
        }
    }

    /*
        Called when the system is about to start resuming a previous activity. This is typically used to commit
        unsaved changes to persistent data, stop animations and other things that may be consuming CPU, etc.
        Implementations of this method must be very quick because the next activity will not be resumed until this
        method returns. Followed by either onResume() if the activity returns back to the front, or onStop() if it
        becomes invisible to the user.
     */
    @Override
    protected void onPause() {
        Log.e(TAG, "onPause");
        //closeCamera();
        stopBackgroundThread();
        super.onPause();
    }
}
