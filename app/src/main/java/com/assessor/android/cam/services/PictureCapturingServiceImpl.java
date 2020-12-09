package com.assessor.android.cam.services;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.assessor.android.cam.Camera2Utils;
import com.assessor.android.cam.listener.PictureCapturingListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.TreeMap;
import java.util.UUID;


/**
 * The aim of this service is to secretly take pictures (without preview or opening device's camera app)
 * from all available cameras using Android Camera 2 API
 *
 * @author hzitoun (zitoun.hamed@gmail.com)
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP) //NOTE: camera 2 api was added in API level 21
public class PictureCapturingServiceImpl extends APictureCapturingService {

    private static final String TAG = PictureCapturingServiceImpl.class.getSimpleName();

    /**
     * {@link CaptureRequest.Builder} for the camera preview
     */
    CaptureRequest.Builder mPreviewRequestBuilder;

    /**
     * {@link CaptureRequest} generated by {@link #mPreviewRequestBuilder}
     */
    CaptureRequest mPreviewRequest;


    private CameraDevice cameraDevice;
    private ImageReader imageReader;
    /***
     * camera ids queue.
     */
    private Queue<String> cameraIds;

    private String currentCameraId;
    private boolean cameraClosed;
    /**
     * stores a sorted map of (pictureUrlOnDisk, PictureData).
     */
    private TreeMap<String, byte[]> picturesTaken;
    private PictureCapturingListener capturingListener;


    /**
     * A {@link Handler} for running tasks in the background.
     */
    Handler mBackgroundHandler;

    /***
     * private constructor, meant to force the use of {@link #getInstance}  method
     */
    private PictureCapturingServiceImpl(final Activity activity) {
        super(activity);
    }

    /**
     * @param activity the activity used to get the app's context and the display manager
     * @return a new instance
     */
    public static APictureCapturingService getInstance(final Activity activity) {
        return new PictureCapturingServiceImpl(activity);
    }

    /**
     * Starts pictures capturing treatment.
     *
     * @param listener picture capturing listener
     */
    @Override
    public void startCapturing(final PictureCapturingListener listener) {
        this.picturesTaken = new TreeMap<>();
        this.capturingListener = listener;
        this.cameraIds = new LinkedList<>();
        try {
            final String[] cameraIds = manager.getCameraIdList();
            if (cameraIds.length > 0) {
                this.cameraIds.addAll(Arrays.asList(cameraIds));
                this.currentCameraId = this.cameraIds.poll();
                openCamera();
            } else {
                //No camera detected!
                capturingListener.onDoneCapturingAllPhotos(picturesTaken);
            }
        } catch (final CameraAccessException e) {
            Log.e(TAG, "Exception occurred while accessing the list of cameras", e);
        }
    }

    private void openCamera() {
        Log.d(TAG, "opening camera " + currentCameraId);
        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {

                DisplayMetrics displayMetrics = new DisplayMetrics();

                int height = displayMetrics.heightPixels;
                int width = displayMetrics.widthPixels;

                //setUpCameraOutputs(width, height);
                //configureTransform(width, height);

                manager.openCamera(currentCameraId, stateCallback, null);
            }
        } catch (final CameraAccessException e) {
            Log.e(TAG, " exception occurred while opening camera " + currentCameraId, e);
        }
    }

    void configureTransform(int viewWidth, int viewHeight) {

        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        //RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        /*if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max((float) viewHeight / mPreviewSize.getHeight(), (float) viewWidth / mPreviewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        } else if (Surface.ROTATION_180 == rotation) {*/
        matrix.postRotate(180, centerX, centerY);
        /*}
        mTextureView.setTransform(matrix);*/
    }


    private final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request,
                                       @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            if (picturesTaken.lastEntry() != null) {
                capturingListener.onCaptureDone(picturesTaken.lastEntry().getKey(), picturesTaken.lastEntry().getValue());
                //Log.i(TAG, "done taking picture from camera " + cameraDevice.getId());
            }
            closeCamera();
        }
    };


    private final ImageReader.OnImageAvailableListener onImageAvailableListener = (ImageReader imReader) -> {
        final Image image = imReader.acquireLatestImage();
        final ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        final byte[] bytes = new byte[buffer.capacity()];
        buffer.get(bytes);
        saveImageToDisk(bytes);
        image.close();
    };

    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraClosed = false;
            Log.d(TAG, "camera " + camera.getId() + " opened");
            cameraDevice = camera;
            Log.i(TAG, "Taking picture from camera " + camera.getId());
            //Take the picture after some delay. It may resolve getting a black dark photos.
            new Handler().postDelayed(() -> {
                try {
                    takePicture();
                } catch (final CameraAccessException e) {
                    Log.e(TAG, " exception occurred while taking picture from " + currentCameraId, e);
                }
            }, 500);
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            Log.d(TAG, " camera " + camera.getId() + " disconnected");
            if (cameraDevice != null && !cameraClosed) {
                cameraClosed = true;
                cameraDevice.close();
            }
        }

        @Override
        public void onClosed(@NonNull CameraDevice camera) {
            cameraClosed = true;
            Log.d(TAG, "camera " + camera.getId() + " closed");
            //once the current camera has been closed, start taking another picture
            if (!cameraIds.isEmpty()) {
                takeAnotherPicture();
            } else {
                capturingListener.onDoneCapturingAllPhotos(picturesTaken);
            }
        }


        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            Log.e(TAG, "camera in error, int code " + error);
            if (cameraDevice != null && !cameraClosed) {
                cameraDevice.close();
            }
        }
    };


    private void takePicture() throws CameraAccessException {
        if (null == cameraDevice) {
            Log.e(TAG, "cameraDevice is null");
            return;
        }
        final CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());

        /****************************/
        StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

        // For still image captures, we use the largest available size.
        /*Size largest = Collections.max(Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)), new CompareSizesByArea());*/
        /**
         * 替换了寻找最大尺寸的算法.
         * 从OutputSizes中找到满足16:9比例，且像素数不超过3840*2160的最大Size.
         * 若找不到，则选择满足16:9比例的最大Size（像素数可能超过3840*2160)，若仍找不到，返回最大Size。
         */
        Size largest = Camera2Utils.findBestSize(map.getOutputSizes(ImageFormat.JPEG), 3840 * 2160);
        imageReader = ImageReader.newInstance(largest.getWidth(), largest.getHeight(), ImageFormat.JPEG, /*maxImages*/2);
        imageReader.setOnImageAvailableListener(onImageAvailableListener, mBackgroundHandler);
        /***************************/


//        Size[] jpegSizes = null;
//        StreamConfigurationMap streamConfigurationMap = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
//        if (streamConfigurationMap != null) {
//            jpegSizes = streamConfigurationMap.getOutputSizes(ImageFormat.JPEG);
//        }
//        final boolean jpegSizesNotEmpty = jpegSizes != null && 0 < jpegSizes.length;
//        int width = jpegSizesNotEmpty ? jpegSizes[0].getWidth() : 640;
//        int height = jpegSizesNotEmpty ? jpegSizes[0].getHeight() : 480;
//        final ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
//        final List<Surface> outputSurfaces = new ArrayList<>();
//        outputSurfaces.add(reader.getSurface());
//        final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
//        captureBuilder.addTarget(reader.getSurface());
//        captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
//        captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, getOrientation());
//        reader.setOnImageAvailableListener(onImageAvailableListener, null);

        mPreviewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        mPreviewRequestBuilder.addTarget(imageReader.getSurface());

        final List<Surface> outputSurfaces1 = new ArrayList<>();
        outputSurfaces1.add(imageReader.getSurface());
//        cameraDevice.createCaptureSession(outputSurfaces1, new CameraCaptureSession.StateCallback() {
        cameraDevice.createCaptureSession(Arrays.asList(imageReader.getSurface()), new CameraCaptureSession.StateCallback() {
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession session) {

                        if (null == cameraDevice) return;

                        // When the session is ready, we start displaying the preview.

                        try {
                            // Auto focus should be continuous for camera preview.
                            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                            // Flash is automatically enabled when necessary.
                            setAutoFlash(mPreviewRequestBuilder);

                            // Finally, we start displaying the camera preview.
                            mPreviewRequest = mPreviewRequestBuilder.build();
                            session.setRepeatingRequest(mPreviewRequest, captureListener, mBackgroundHandler);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Failed to open camera preview, try again", Toast.LENGTH_LONG).show();
                            //fin = true;


                        }


//                        try {
//                            session.capture(captureBuilder.build(), captureListener, null);
//                        } catch (final CameraAccessException e) {
//                            Log.e(TAG, " exception occurred while accessing " + currentCameraId, e);
//                        }
                    }

                    @Override
                    public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                        Toast.makeText(context, "ERROR SESSION", Toast.LENGTH_LONG).show();
                    }
                }
                , null);
    }

    void setAutoFlash(CaptureRequest.Builder requestBuilder) {
        /**
         * 若相机支持自动开启/关闭闪光灯，则使用. 否则闪光灯总是关闭的.
         */
        requestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
    }


    private void saveImageToDisk(final byte[] bytes) {
        final String cameraId = this.cameraDevice == null ? UUID.randomUUID().toString() : this.cameraDevice.getId();
        final File file = new File(Environment.getExternalStorageDirectory() + "/" + cameraId + "_pic.jpg");
        try (final OutputStream output = new FileOutputStream(file)) {
            output.write(bytes);
            this.picturesTaken.put(file.getPath(), bytes);
        } catch (final IOException e) {
            Log.e(TAG, "Exception occurred while saving picture to external storage ", e);
        }
    }

    private void takeAnotherPicture() {
        this.currentCameraId = this.cameraIds.poll();
        openCamera();
    }

    private void closeCamera() {
        //Log.d(TAG, "closing camera " + cameraDevice.getId());
        if (null != cameraDevice && !cameraClosed) {
            cameraDevice.close();
            cameraDevice = null;
        }
        if (null != imageReader) {
            imageReader.close();
            imageReader = null;
        }
    }


}
