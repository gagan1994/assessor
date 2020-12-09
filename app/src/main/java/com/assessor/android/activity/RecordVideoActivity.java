package com.assessor.android.activity;


import android.Manifest;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.assessor.android.BaseActivity;
import com.assessor.android.R;
import com.assessor.android.bs.BSConfirmationDialogFragment;
import com.assessor.android.camera.CameraView;
import com.assessor.android.camera.CanvasDrawer;
import com.assessor.android.camera.Error;
import com.assessor.android.camera.Photographer;
import com.assessor.android.camera.PhotographerFactory;
import com.assessor.android.camera.PhotographerHelper;
import com.assessor.android.camera.SimpleOnEventListener;
import com.assessor.android.camera.Size;
import com.assessor.android.camera.Values;
import com.assessor.android.camera.dialog.PickerDialog;
import com.assessor.android.camera.dialog.SimplePickerDialog;
import com.assessor.android.camera.options.AspectRatioItem;
import com.assessor.android.camera.options.Commons;
import com.assessor.android.camera.options.SizeItem;
import com.assessor.android.iface.OnDialogClickListener;
import com.assessor.android.retrofit.RetrofitService;
import com.assessor.android.retrofit.RetrofitServiceListener;
import com.assessor.android.utility.AccPref;
import com.assessor.android.utility.LocalConstants;
import com.assessor.android.utility.Utility;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import cn.iwgang.countdownview.CountdownView;
import top.defaults.view.TextButton;


public class RecordVideoActivity extends BaseActivity implements RetrofitServiceListener {

    Photographer photographer;
    PhotographerHelper photographerHelper;
    private boolean isRecordingVideo;
    RetrofitServiceListener mServiceListener;


    CameraView preview;
    TextView statusTextView;

    TextButton chooseSizeButton;


    TextButton flashTextButton;

    ImageButton flashTorch;

    TextButton switchButton;

    TextView actionButton;


    ImageButton flipButton;

    TextView zoomValueTextView;

    File file;


    private int currentFlash = Values.FLASH_AUTO;

    private static final int[] FLASH_OPTIONS = {
            Values.FLASH_AUTO,
            Values.FLASH_OFF,
            Values.FLASH_ON,
    };

    private static final int[] FLASH_ICONS = {
            R.drawable.ic_flash_auto,
            R.drawable.ic_flash_off,
            R.drawable.ic_flash_on,
    };

    private static final int[] FLASH_TITLES = {
            R.string.flash_auto,
            R.string.flash_off,
            R.string.flash_on,
    };
    private CountdownView countdownView;


    void chooseRatio() {
        List<AspectRatioItem> supportedAspectRatios = Commons.wrapItems(photographer.getSupportedAspectRatios(), AspectRatioItem::new);
        if (supportedAspectRatios != null) {
            SimplePickerDialog<AspectRatioItem> dialog = SimplePickerDialog.create(new PickerDialog.ActionListener<AspectRatioItem>() {
                @Override
                public void onCancelClick(PickerDialog<AspectRatioItem> dialog) {
                }

                @Override
                public void onDoneClick(PickerDialog<AspectRatioItem> dialog) {
                    AspectRatioItem item = dialog.getSelectedItem(AspectRatioItem.class);
                    photographer.setAspectRatio(item.get());
                }
            });
            dialog.setItems(supportedAspectRatios);
            dialog.setInitialItem(Commons.findEqual(supportedAspectRatios, photographer.getAspectRatio()));
            dialog.show(getFragmentManager(), "aspectRatio");
        }
    }

    void chooseSize() {
        Size selectedSize = null;
        List<SizeItem> supportedSizes = null;
        int mode = photographer.getMode();
        if (mode == Values.MODE_VIDEO) {
            Set<Size> videoSizes = photographer.getSupportedVideoSizes();
            selectedSize = photographer.getVideoSize();
            if (videoSizes != null && videoSizes.size() > 0) {
                supportedSizes = Commons.wrapItems(videoSizes, SizeItem::new);
            }
        } else if (mode == Values.MODE_IMAGE) {
            Set<Size> imageSizes = photographer.getSupportedImageSizes();
            selectedSize = photographer.getImageSize();
            if (imageSizes != null && imageSizes.size() > 0) {
                supportedSizes = Commons.wrapItems(imageSizes, SizeItem::new);
            }
        }

        if (supportedSizes != null) {
            SimplePickerDialog<SizeItem> dialog = SimplePickerDialog.create(new PickerDialog.ActionListener<SizeItem>() {
                @Override
                public void onCancelClick(PickerDialog<SizeItem> dialog) {
                }

                @Override
                public void onDoneClick(PickerDialog<SizeItem> dialog) {
                    SizeItem sizeItem = dialog.getSelectedItem(SizeItem.class);
                    if (mode == Values.MODE_VIDEO) {
                        photographer.setVideoSize(sizeItem.get());
                    } else {
                        photographer.setImageSize(sizeItem.get());
                    }
                }
            });
            dialog.setItems(supportedSizes);
            dialog.setInitialItem(Commons.findEqual(supportedSizes, selectedSize));
            dialog.show(getFragmentManager(), "cameraOutputSize");
        }
    }

    void onFillSpaceChecked(boolean checked) {
        preview.setFillSpace(checked);
    }


    void onEnableZoomChecked(boolean checked) {
        preview.setPinchToZoom(checked);
    }


    void flash() {
        currentFlash = (currentFlash + 1) % FLASH_OPTIONS.length;
        flashTextButton.setText(FLASH_TITLES[currentFlash]);
        flashTextButton.setCompoundDrawablesWithIntrinsicBounds(FLASH_ICONS[currentFlash], 0, 0, 0);
        photographer.setFlash(FLASH_OPTIONS[currentFlash]);
    }

    private long getMilisecondLeft(){
        Date date = new Date();
        long estDateInLong= date.getTime();
        long currentTimeinLong= Calendar.getInstance().getTimeInMillis();
        long diff=(long)(currentTimeinLong-estDateInLong);
        if(currentTimeinLong > estDateInLong){
            diff=(long)(currentTimeinLong-estDateInLong);
        }else{
            diff=(long)(estDateInLong - currentTimeinLong);
        }
        long diffDay=diff/(10000);
        if(diffDay > 0 ){
            return 0;
        }else {
            StringBuffer sbf = new StringBuffer();
            return diff;
        }
    }
    
    private void initCounter(){
        countdownView = findViewById(R.id.cv_countdownView);
        countdownView.start(15000);
        countdownView.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
            @Override
            public void onEnd(CountdownView cv) {
                finishRecordingIfNeeded();
            }
        });
    }

    void action() {
        int mode = photographer.getMode();
        if (mode == Values.MODE_VIDEO) {
            if (isRecordingVideo) {
                finishRecordingIfNeeded();
                actionButton.setText("START RECORDING");
            } else {
                isRecordingVideo = true;
                photographer.startRecording(null);
                actionButton.setEnabled(false);
                actionButton.setText("STOP");
                initCounter();
            }
        } else if (mode == Values.MODE_IMAGE) {
            photographer.takePicture();
        }
    }

    void toggleFlashTorch() {
        int flash = photographer.getFlash();
        if (flash == Values.FLASH_TORCH) {
            photographer.setFlash(currentFlash);
            flashTextButton.setEnabled(true);
            flashTorch.setImageResource(R.drawable.light_off);
        } else {
            photographer.setFlash(Values.FLASH_TORCH);
            flashTextButton.setEnabled(false);
            flashTorch.setImageResource(R.drawable.light_on);
        }
    }

    void switchMode() {
        photographerHelper.switchMode();
    }


    void flip() {
        photographerHelper.flip();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_record);
        AccPref.setSuspended(getApplicationContext(), AccPref.getExamId(getApplicationContext()), true);
        preview = findViewById(R.id.preview);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        chooseSizeButton = findViewById(R.id.chooseSize);
        statusTextView = findViewById(R.id.status);
        flashTorch = findViewById(R.id.flash_torch);
        flashTorch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFlashTorch();
            }
        });
        flashTextButton = findViewById(R.id.flash);
        flashTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flash();
            }
        });
        switchButton = findViewById(R.id.switch_mode);
        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchMode();
            }
        });
        actionButton = findViewById(R.id.action);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                action();
            }
        });
        flipButton  = findViewById(R.id.flip);
        flipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flip();
            }
        });
        zoomValueTextView = findViewById(R.id.zoomValue);
        mServiceListener = this;
        preview.setFocusIndicatorDrawer(new CanvasDrawer() {
            private static final int SIZE = 300;
            private static final int LINE_LENGTH = 50;

            @Override
            public Paint[] initPaints() {
                Paint focusPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                focusPaint.setStyle(Paint.Style.STROKE);
                focusPaint.setStrokeWidth(2);
                focusPaint.setColor(Color.WHITE);
                return new Paint[]{focusPaint};
            }

            @Override
            public void draw(Canvas canvas, Point point, Paint[] paints) {
                if (paints == null || paints.length == 0) return;

                int left = point.x - (SIZE / 2);
                int top = point.y - (SIZE / 2);
                int right = point.x + (SIZE / 2);
                int bottom = point.y + (SIZE / 2);

                Paint paint = paints[0];

                canvas.drawLine(left, top + LINE_LENGTH, left, top, paint);
                canvas.drawLine(left, top, left + LINE_LENGTH, top, paint);

                canvas.drawLine(right - LINE_LENGTH, top, right, top, paint);
                canvas.drawLine(right, top, right, top + LINE_LENGTH, paint);

                canvas.drawLine(right, bottom - LINE_LENGTH, right, bottom, paint);
                canvas.drawLine(right, bottom, right - LINE_LENGTH, bottom, paint);

                canvas.drawLine(left + LINE_LENGTH, bottom, left, bottom, paint);
                canvas.drawLine(left, bottom, left, bottom - LINE_LENGTH, paint);
            }
        });
        photographer = PhotographerFactory.createPhotographerWithCamera2(this, preview);
        photographerHelper = new PhotographerHelper(photographer);
        photographerHelper.setFileDir(Commons.MEDIA_DIR);

        photographer.setOnEventListener(new SimpleOnEventListener() {
            @Override
            public void onDeviceConfigured() {
                if (photographer.getMode() == Values.MODE_VIDEO) {
                    //actionButton.setImageResource(R.drawable.record);
                    chooseSizeButton.setText(R.string.video_size);
                    switchButton.setText(R.string.video_mode);
                } else {
                    //actionButton.setImageResource(R.drawable.ic_camera);
                    chooseSizeButton.setText(R.string.image_size);
                    switchButton.setText(R.string.image_mode);
                }
            }

            @Override
            public void onZoomChanged(float zoom) {
                zoomValueTextView.setText(String.format(Locale.getDefault(), "%.1fX", zoom));
            }

            @Override
            public void onStartRecording() {
                switchButton.setVisibility(View.INVISIBLE);
                flipButton.setVisibility(View.INVISIBLE);
                actionButton.setEnabled(true);
                //actionButton.setImageResource(R.drawable.stop);
                actionButton.setText("STOP RECORDING");
                statusTextView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinishRecording(String filePath) {
                announcingNewFile(filePath);
            }

            @Override
            public void onShotFinished(String filePath) {
                announcingNewFile(filePath);
            }

            @Override
            public void onError(Error error) {
                /*Timber.e("Error happens: %s", error.getMessage());*/
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        enterFullscreen();
        photographer.startPreview();
        //photographer.startRecording(null);

    }

    @Override
    protected void onPause() {

        super.onPause();
        finishRecordingIfNeeded();
        photographer.stopPreview();
    }

    private void enterFullscreen() {
        View decorView = getWindow().getDecorView();
        decorView.setBackgroundColor(Color.BLACK);
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void finishRecordingIfNeeded() {
        if (isRecordingVideo) {
            isRecordingVideo = false;
            photographer.finishRecording();
            photographer.finishRecording();
            statusTextView.setVisibility(View.INVISIBLE);
            switchButton.setVisibility(View.VISIBLE);
            flipButton.setVisibility(View.VISIBLE);
            actionButton.setEnabled(true);
            //actionButton.setImageResource(R.drawable.record);
            actionButton.setText("START RECORDING");
        }
    }

    private void announcingNewFile(String filePath) {
        Toast.makeText(RecordVideoActivity.this, "File: " + filePath, Toast.LENGTH_SHORT).show();
        //Utils.addMediaToGallery(RecordVideoActivity.this, filePath);
        //finish();
        file = new File(filePath);
        //RetrofitService.getInstance(getApplicationContext()).updateFile(file, 12, 11, 0.0, 0.0, mServiceListener);

        getConfirmation();
        /*TypedFile file = new TypedFile("multipart/form-data",
                new File(path));*/
    }

    @Override
    public void onRequestStarted(Object mObject) {
        showProgressDialog("Video Uploading Please wait...");
    }

    @Override
    public void onResponse(Object mObject) {
        hideDialog();
        if(mObject instanceof  String) {
            String msg = (String) mObject;
            Utility.showErrorMessageSheet(getSupportFragmentManager(), "SUCCESS", msg, false, new OnDialogClickListener() {
                @Override
                public void onOkClicked(Object object) {
                    Intent intent=new Intent(getApplicationContext(),ExamSchedule.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }

                @Override
                public void onCancelClicked() {

                }
            });
        }
    }

    @Override
    public void onFailure(Object mObject, Throwable t) {
        hideDialog();
        if(mObject instanceof  String) {
            String msg = (String) mObject;
            Utility.showErrorMessageSheet(getSupportFragmentManager(), "ERROR", msg, false, new OnDialogClickListener() {
                @Override
                public void onOkClicked(Object object) {
                    Intent intent=new Intent(getApplicationContext(),ExamSchedule.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }

                @Override
                public void onCancelClicked() {

                }
            });
        }
    }

    private class UploadFileAsync extends AsyncTask<String, Void, String> {

        private int serverResponseCode;
        String sourceFileUri;

        public UploadFileAsync(String sourceFileUri) {
            this.sourceFileUri = sourceFileUri;
        }

        @Override
        protected String doInBackground(String... params) {

            try {

                HttpURLConnection conn = null;
                DataOutputStream dos = null;
                String lineEnd = "\r\n";
                String twoHyphens = "--";
                String boundary = "*****";
                int bytesRead, bytesAvailable, bufferSize;
                byte[] buffer;
                int maxBufferSize = 1 * 1024 * 1024;
                File sourceFile = new File(sourceFileUri);

                if (sourceFile.isFile()) {

                    try {
                        String upLoadServerUri = "http://jemengg.com/";

                        // open a URL connection to the Servlet
                        FileInputStream fileInputStream = new FileInputStream(
                                sourceFile);
                        URL url = new URL(upLoadServerUri);

                        // Open a HTTP connection to the URL
                        conn = (HttpURLConnection) url.openConnection();
                        String basicAuth = "Bearer " + AccPref.getAccessToken(getApplicationContext());

                        conn.setRequestProperty ("Authorization", basicAuth);
                        conn.setDoInput(true); // Allow Inputs
                        conn.setDoOutput(true); // Allow Outputs
                        conn.setUseCaches(false); // Don't use a Cached Copy
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Connection", "Keep-Alive");
                        conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                        conn.setRequestProperty("Content-Type","multipart/form-data;boundary=" + boundary);
                        conn.setRequestProperty("video_file", sourceFileUri);
                        conn.setRequestProperty("file", sourceFileUri);

                        dos = new DataOutputStream(conn.getOutputStream());

                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        dos.writeBytes("Content-Disposition: form-data; name=\"bill\";filename=\""
                                + sourceFileUri + "\"" + lineEnd);

                        dos.writeBytes(lineEnd);

                        // create a buffer of maximum size
                        bytesAvailable = fileInputStream.available();

                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        buffer = new byte[bufferSize];

                        // read file and write it into form...
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                        while (bytesRead > 0) {

                            dos.write(buffer, 0, bufferSize);
                            bytesAvailable = fileInputStream.available();
                            bufferSize = Math
                                    .min(bytesAvailable, maxBufferSize);
                            bytesRead = fileInputStream.read(buffer, 0,
                                    bufferSize);

                        }

                        // send multipart form data necesssary after file
                        // data...
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(twoHyphens + boundary + twoHyphens
                                + lineEnd);

                        // Responses from the server (code and message)
                        serverResponseCode = conn.getResponseCode();
                        String serverResponseMessage = conn
                                .getResponseMessage();

                        if (serverResponseCode == 200) {

                            // messageText.setText(msg);
                            //Toast.makeText(ctx, "File Upload Complete.",
                            //      Toast.LENGTH_SHORT).show();

                            // recursiveDelete(mDirectory1);

                        }

                        // close the streams //
                        fileInputStream.close();
                        dos.flush();
                        dos.close();

                    } catch (Exception e) {

                        // dialog.dismiss();
                        e.printStackTrace();

                    }
                    // dialog.dismiss();

                } // End else block


            } catch (Exception ex) {
                // dialog.dismiss();

                ex.printStackTrace();
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    private void getConfirmation(){

    //    embedTextOnVideo("","");
        BSConfirmationDialogFragment bottomSheetFragment = new BSConfirmationDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(LocalConstants.CONST_TITLE, getString(R.string.app_name));
        bundle.putString(LocalConstants.CONST_MESSAGE, "Do you want to upload this video?");
        bundle.putSerializable(LocalConstants.CONST_LISTENER, new OnDialogClickListener() {
            @Override
            public void onOkClicked(Object object) {
                RetrofitService.getInstance(getApplicationContext()).uploadFile(file,AccPref.getUserId(getApplicationContext()),
                        AccPref.getExamId(getApplicationContext()),
                        Utility.parseDouble(AccPref.getLat(getApplicationContext())),
                        Utility.parseDouble(AccPref.getLong(getApplicationContext()))
                        ,mServiceListener);
            }

            @Override
            public void onCancelClicked() {

            }
        });
        bottomSheetFragment.setArguments(bundle);
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
    }

    /****************************************/
    /*FFmpeg ffmpeg;

    private void initUI() {

    }

    private void showUnsupportedExceptionDialog() {
        new AlertDialog.Builder(getApplicationContext())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("device_not_supported")
                .setMessage("device_not_supported_message")
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create()
                .show();

    }

    private void loadFFMpegBinary() {
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onFailure() {
                    showUnsupportedExceptionDialog();
                }
            });
        } catch (FFmpegNotSupportedException e) {
            showUnsupportedExceptionDialog();
        }
    }*/

    /*private void execFFmpegBinary(final String[] command) {
        try {
            ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
                @Override
                public void onFailure(String s) {

                    Log.d("MPEG","FAILED with output : "+s);
                }

                @Override
                public void onSuccess(String s) {

                    Log.d("MPEG","SUCCESS with output : "+s);
                }

                @Override
                public void onProgress(String s) {
                    Log.d("MPEG", "Started command : ffmpeg "+command);
                    //addTextViewToLayout("progress : "+s);
                    //progressDialog.setMessage("Processing\n"+s);
                }

                @Override
                public void onStart() {
                    //outputLayout.removeAllViews();

                    //Log.d(TAG, "Started command : ffmpeg " + command);
                    //progressDialog.setMessage("Processing...");
                    //progressDialog.show();
                }

                @Override
                public void onFinish() {
                    Log.d("MPEG", "Finished command : ffmpeg "+command);
                    //progressDialog.dismiss();
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // do nothing for now
        }
    }

    public void embedTextOnVideo(String text, String path)
    {
        ffmpeg = FFmpeg.getInstance(getApplicationContext());
        loadFFMpegBinary();
        initUI();
        File output =new File(getCacheDir()+"/output.mp4");
        String cm[] = new String[]{ "-i", file.getAbsolutePath(), "-vf", "drawtext=text=" + text + ":fontcolor=black:fontsize=70:x="+0+ ":y="+0+"):enable='between(t,"+0+","+2500+")'", "-codec:a", "copy", output.getAbsolutePath()};
        String[] cmd = new String[] {"ffmpeg -i "+file.getAbsolutePath()+" -vf drawtext=text='Super User':x=(w-text_w)/2:y=(h-text_h)/2:fontsize=24:fontcolor=white\" -c:a copy "+file.getAbsolutePath()};
        if(!output.exists()){
            try {
                output.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String cmd2[] = new String[]{"ffmpeg -i "+file.getAbsolutePath()+" -vf " +
                "\"format=yuv444p, " +
                " drawbox=y=ih/PHI:color=black@0.4:width=iw:height=48:t=fill," +
                " drawtext=fontfile=OpenSans-Regular.ttf:text='Title of this Video':fontcolor=white:fontsize=24:x=(w-tw)/2:y=(h/PHI)+th," +
                " format=yuv420p " +
                "-c:v libx264 -c:a copy -movflags +faststart "+output.getAbsolutePath()};
        execFFmpegBinary(cmd2);
*//*
        String[] cmd = new String[] {"-y", "-i", file.getAbsolutePath(),  "-vf", "drawtext=text='Centered Text':x=(w-text_w)/2:y=(h-text_h)/2:fontsize=24:fontcolor=white", file.getAbsolutePath()
        };
        File output =new File(getCacheDir()+"\\output.mp4");

        String cm[] = new String[]{ "-i", file.getAbsolutePath(), "-vf", "drawtext=text=" + text + ":fontcolor=black:fontsize=70:x="+0+ ":y="+0+"):enable='between(t,"+0+","+2500+")'", "-codec:a", "copy", output.getAbsolutePath()};

        String cmd1[] = new String[]{file.getAbsolutePath()+" -y -i IMG_0696.MP4 -acodec libmp3lame -vcodec msmpeg4 -b:a 192k -b:v 1000k -ar 44100 " +
                "-vf drawtext=text=string1 string2 string3 string4 string5 string6 string7 :expansion=normal:y=0:x=h-(2*lh)-n: fontcolor=white: fontsize=24: box=1: boxcolor=0x00000000@1" +
                "-an "+file.getAbsolutePath()};



        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler());
            ffmpeg.execute(cmd2, new ExecuteBinaryResponseHandler() {

                @Override
                public void onStart() {}

                @Override
                public void onProgress(String message) {
                    Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(String message) {
                    //errorCallback.invoke("Error ffmpeg executing with message:\n\t" + message);
                    Toast.makeText(getApplicationContext(),"ERROR "+message,Toast.LENGTH_LONG).show();
                }

                @Override
                public void onSuccess(String message) {
                    Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
                    //successCallback.invoke("Successfully output file with message:\n\t");
                }

                @Override
                public void onFinish() {}
            });
        } catch (Exception e) {
            // Handle if FFmpeg is already running
        }*//*
    }

    @Nullable
    private Throwable writeDataToFile(byte[] data, File file) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data);
            fos.close();
        } catch (FileNotFoundException e) {
            return e;
        } catch (IOException e) {
            return e;
        }

        return null;
    }
    @Nullable
    private File getOutputFile(int type) {
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

// Create storage dir if it does not exist
        if (!storageDir.exists()) {
            if (!storageDir.mkdirs()) {
                Log.e("TAG", "Failed to create directory:" + storageDir.getAbsolutePath());
                return null;
            }
        }

// media file name
        String fileName = String.format("%s", new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()));


//        if (type == TYPE_VIDEO) {
//            fileName = String.format("VID_%s.mp4", fileName);
//        } else {
//            Log.e(TAG, "Unsupported media type:" + type);
//            return null;
//        }

        return new File(String.format("%s%s%s", storageDir.getPath(), File.separator, fileName));
    }*/

}