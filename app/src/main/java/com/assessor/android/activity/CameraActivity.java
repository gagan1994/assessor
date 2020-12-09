package com.assessor.android.activity;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.SparseArray;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.assessor.android.R;
import com.assessor.android.cam.listener.PictureCapturingListener;
import com.assessor.android.cam.services.APictureCapturingService;
import com.assessor.android.cam.utils.CameraConfig;
import com.assessor.android.cam.utils.CameraError;
import com.assessor.android.cam.utils.CameraFacing;
import com.assessor.android.cam.utils.CameraImageFormat;
import com.assessor.android.cam.utils.CameraResolution;
import com.assessor.android.cam.utils.CameraRotation;
import com.assessor.android.cam.utils.HiddenCameraActivity;
import com.assessor.android.cam.utils.HiddenCameraUtils;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class CameraActivity extends HiddenCameraActivity implements PictureCapturingListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private Bitmap myBitmap;
    private SparseArray<Face> sparseArray;
    private final int PICK_IMAGE = 1;
    private Bitmap tempBitmap;

    private static final String[] requiredPermissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
    };
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_CODE = 1;

    private ImageView uploadBackPhoto;
    private ImageView uploadFrontPhoto;

    //The capture service
    private APictureCapturingService pictureService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        checkPermissions();
        uploadBackPhoto = findViewById(R.id.backIV);
        uploadFrontPhoto = findViewById(R.id.frontIV);
        final Button btn = findViewById(R.id.startCaptureBtn);
        // getting instance of the Service from PictureCapturingServiceImpl

        CameraConfig mCameraConfig = new CameraConfig()
                .getBuilder(this)
                .setCameraFacing(CameraFacing.FRONT_FACING_CAMERA)
                .setCameraResolution(CameraResolution.MEDIUM_RESOLUTION)
                .setImageFormat(CameraImageFormat.FORMAT_JPEG)
                .setImageRotation(CameraRotation.ROTATION_270)
                .build();

        //Check for the camera permission for the runtime
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

            //Start camera preview
            startCamera(mCameraConfig);
        } else {
            ActivityCompat.requestPermissions(CameraActivity.this, new String[]{Manifest.permission.CAMERA}, 101);
        }


        btn.setOnClickListener(v -> {
            showToast("Starting capture!");
            //pictureService.startCapturing(this);
            //Camera2Activity.newInstance(CameraActivity.this).startCapturing(null);
            try {
                takePicture();
            } catch (Exception e) {
                e.printStackTrace();
            }


        });
    }

    private void showToast(final String text) {
        runOnUiThread(() ->
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show()
        );
    }

    /**
     * We've finished taking pictures from all phone's cameras
     */
    @Override
    public void onDoneCapturingAllPhotos(TreeMap<String, byte[]> picturesTaken) {
        if (picturesTaken != null && !picturesTaken.isEmpty()) {
            showToast("Done capturing all photos!");

            return;
        }
        showToast("No camera detected!");
    }

    /**
     * Displaying the pictures taken.
     */
    @Override
    public void onCaptureDone(String pictureUrl, byte[] pictureData) {
        if (pictureData != null && pictureUrl != null) {
            runOnUiThread(() -> {
                final Bitmap bitmap = BitmapFactory.decodeByteArray(pictureData, 0, pictureData.length);
//                    final int nh = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
//                    final Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
//                    if (pictureUrl.contains("0_pic.jpg")) {
//                        uploadBackPhoto.setImageBitmap(scaled);
//                    } else if (pictureUrl.contains("1_pic.jpg")) {
//                        uploadFrontPhoto.setImageBitmap(scaled);
//                    }
                File imgFile = new File(pictureUrl);

                if (imgFile.exists()) {

                    Bitmap bitMap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    myBitmap = bitMap;
                    tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.RGB_565);
                    if (pictureUrl.contains("0_pic.jpg")) {
                        uploadBackPhoto.setImageBitmap(myBitmap);
                    } else {
                        uploadFrontPhoto.setImageBitmap(myBitmap);
                    }

                }
                new BackgroundTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            });
            showToast("Picture saved to " + pictureUrl);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_CODE: {
                if (!(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    checkPermissions();
                }
            }
        }
    }

    /**
     * checking  permissions at Runtime.
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissions() {
        final List<String> neededPermissions = new ArrayList<>();
        for (final String permission : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    permission) != PackageManager.PERMISSION_GRANTED) {
                neededPermissions.add(permission);
            }
        }
        if (!neededPermissions.isEmpty()) {
            requestPermissions(neededPermissions.toArray(new String[]{}),
                    MY_PERMISSIONS_REQUEST_ACCESS_CODE);
        }
    }

    private void displayFrames() {

        if (sparseArray != null) {
            for (int i = 0; i < sparseArray.size(); i++) {
                Face face = sparseArray.valueAt(i);
                float x1 = face.getPosition().x;
                float y1 = face.getPosition().y;
                float x2 = x1 + face.getWidth();
                float y2 = y1 + face.getHeight();
                RectF rectF = new RectF(x1, y1, x2, y2);
                //canvas.drawRoundRect(rectF, 2, 2, rectPaint);
            }
            Toast.makeText(getApplicationContext(), " frames  " + sparseArray.size(), Toast.LENGTH_LONG).show();
            uploadFrontPhoto.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));
        }
    }

    @Override
    public void onImageCapture(@NonNull File imageFile) {
        // Convert file to bitmap.
        // Do something.
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        myBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);

        //Display the image to the image view
        ((ImageView) findViewById(R.id.backIV)).setImageBitmap(myBitmap);
        new BackgroundTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onCameraError(int errorCode) {
        switch (errorCode) {
            case CameraError.ERROR_CAMERA_OPEN_FAILED:
                //Camera open failed. Probably because another application
                //is using the camera
                break;
            case CameraError.ERROR_IMAGE_WRITE_FAILED:
                //Image write failed. Please check if you have provided WRITE_EXTERNAL_STORAGE permission
                break;
            case CameraError.ERROR_CAMERA_PERMISSION_NOT_AVAILABLE:
                //camera permission is not available
                //Ask for the camra permission before initializing it.
                break;
            case CameraError.ERROR_DOES_NOT_HAVE_OVERDRAW_PERMISSION:
                //Display information dialog to the user with steps to grant "Draw over other app"
                //permission for the app.
                HiddenCameraUtils.openDrawOverPermissionSetting(this);
                break;
            case CameraError.ERROR_DOES_NOT_HAVE_FRONT_CAMERA:
                Toast.makeText(this, "Your device does not have front camera.", Toast.LENGTH_LONG).show();
                break;
        }
    }


    class BackgroundTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            com.google.android.gms.vision.face.FaceDetector faceDetector = new com.google.android.gms.vision.face.FaceDetector.Builder(getApplicationContext())
                    .setTrackingEnabled(false)
                    .setLandmarkType(com.google.android.gms.vision.face.FaceDetector.ALL_LANDMARKS)
                    .setMode(com.google.android.gms.vision.face.FaceDetector.FAST_MODE)
                    .build();
            if (!faceDetector.isOperational()) {
                Toast.makeText(CameraActivity.this, "Face Detector could not be set up on your device", Toast.LENGTH_SHORT).show();
            } else {
                Frame frame = new Frame.Builder().setBitmap(myBitmap).build();
                sparseArray = faceDetector.detect(frame);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            displayFrames();
        }
    }
}
