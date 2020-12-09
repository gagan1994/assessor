package com.assessor.android.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.assessor.android.BaseActivity;
import com.assessor.android.R;
import com.assessor.android.SplashActivity;
import com.assessor.android.adapter.QuestionAdapter;
import com.assessor.android.cam.listener.PictureCapturingListener;
import com.assessor.android.cam.services.APictureCapturingService;
import com.assessor.android.cam.services.PictureCapturingServiceImpl;
import com.assessor.android.iface.OnDialogClickListener;
import com.assessor.android.model.ImagebasedQuestions;
import com.assessor.android.model.OptionalQuestions;
import com.assessor.android.model.PracticalQuestions;
import com.assessor.android.model.QuestionAnswerModel;
import com.assessor.android.retrofit.RetrofitService;
import com.assessor.android.retrofit.RetrofitServiceListener;
import com.assessor.android.retrofit.request.AnswerResponse;
import com.assessor.android.retrofit.request.QuestionListRequest;
import com.assessor.android.retrofit.response.QuestionsListResponse;
import com.assessor.android.utility.AccPref;
import com.assessor.android.utility.Utility;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.gson.Gson;
import com.skyfishjy.library.RippleBackground;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cn.iwgang.countdownview.CountdownView;

public class LiveExamActivity extends BaseActivity implements View.OnClickListener, RetrofitServiceListener, PictureCapturingListener, ActivityCompat.OnRequestPermissionsResultCallback {

    CountdownView countdownView;
    boolean isTimeCompleted = false;
    List<OptionalQuestions> mQuestionModel;
    public static int questionCounter = 0;
    QuestionAdapter questionAdapter;
    List<OptionalQuestions> currentQuestion = new ArrayList<>();
    RecyclerView mRecycler;

    List<Object> questionList = new ArrayList<>();

    TextView txtNext, txtPrevious, txt_question, action_finish;
    int examID;
    RetrofitServiceListener mServiceListener;

    private File mVideoFile;
    private File mThumbnailFile;
    private View mErrorView;
    static final String FILE_PREFIX = "recorder-";
    static final String THUMBNAIL_FILE_EXTENSION = "jpg";


    //FACE RECOGNIZAIONS
    private Bitmap myBitmap;
    private SparseArray<Face> sparseArray;
    private final int PICK_IMAGE = 1;
    private Bitmap tempBitmap;

    private int faceDetctionAttemts;


    private static final String[] requiredPermissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
    };
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_CODE = 1;

    //The capture service
    private APictureCapturingService pictureService;
    RelativeLayout mAlertView;

    Handler _handlar;

    //FACE RECOG END
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_exam);
        checkPermissions();
        mAlertView = findViewById(R.id.alert);
        pictureService = PictureCapturingServiceImpl.getInstance(this);
        examID = getIntent().getExtras().getInt("exam_id");
        mServiceListener = this;
        questionCounter = 0;
        txt_question = findViewById(R.id.txt_question);
        txtNext = findViewById(R.id.action_next);
        action_finish = findViewById(R.id.action_finish);
        txtPrevious = findViewById(R.id.action_previous);
        txtNext.setOnClickListener(this);
        txtPrevious.setOnClickListener(this);
        action_finish.setOnClickListener(this);

        final RippleBackground rippleBackground = findViewById(R.id.content);
        ImageView imageView = findViewById(R.id.centerImage);
        rippleBackground.startRippleAnimation();
        init();

        countdownView = findViewById(R.id.cv_countdownView);
        countdownView.start(getMilisecondLeft(getNextHour()));
        countdownView.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
            @Override
            public void onEnd(CountdownView cv) {
                isTimeCompleted = true;
                txtNext.setEnabled(false);
                txtPrevious.setEnabled(false);
                Utility.showErrorMessageSheet(getSupportFragmentManager(), "Time Completed", "TIme has been completed", false, new OnDialogClickListener() {
                    @Override
                    public void onOkClicked(Object object) {
                        AccPref.clear(getApplicationContext());
                        Intent in = new Intent(getApplicationContext(), SplashActivity.class);
                        in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(in);
                    }

                    @Override
                    public void onCancelClicked() {
                        finish();
                    }
                });
            }
        });

        _handlar = new Handler(Looper.getMainLooper());
        delayProcess();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);

        }
    }

    private void delayProcess() {
        if (_handlar != null) {
            _handlar.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 100ms
                    onProcessFace();
                }
            }, 1 * 15 * 1000);
        }
    }

    private void onProcessFace() {
        pictureService.startCapturing(this);
        delayProcess();
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


//    private void insertDummeyQuestions(){
//        mQuestionModel = new ArrayList<>();
//        List<AnswerModel> ans1 = new ArrayList<>();
//        ans1.add(new AnswerModel(1,1,"1921"));
//        ans1.add(new AnswerModel(2,1,"1926"));
//        ans1.add(new AnswerModel(3,1,"1931"));
//        ans1.add(new AnswerModel(4,1,"1934"));
//        mQuestionModel.add(new QuestionModel(1,"In which year ‘Hilton Young Commission’ recommends the establishment of a central bank? ",ans1));
//
//
//        List<AnswerModel> ans2 = new ArrayList<>();
//        ans2.add(new AnswerModel(1,1,"Capital Market"));
//        ans2.add(new AnswerModel(2,1,"Commodity Market"));
//        ans2.add(new AnswerModel(3,1,"Futures Market"));
//        ans2.add(new AnswerModel(4,1,"Money Market"));
//        mQuestionModel.add(new QuestionModel(2," Collateralised Borrowing and Lending Obligation (CBLO) is an instrument of-  ",ans2));
//
//        List<AnswerModel> ans3 = new ArrayList<>();
//        ans3.add(new AnswerModel(1,1,"simplex"));
//        ans3.add(new AnswerModel(2,1,"half duplex"));
//        ans3.add(new AnswerModel(3,1,"full duplex"));
//        ans3.add(new AnswerModel(4,1,"all of these"));
//        mQuestionModel.add(new QuestionModel(3," Which data communication method is used to send data over a serial communication link?  ",ans3));
//
//        List<AnswerModel> ans4 = new ArrayList<>();
//        ans4.add(new AnswerModel(1,1,"teleprocessing combing telecommunication and DP techniques in online activities."));
//        ans4.add(new AnswerModel(2,1,"Multiplexers are designed to accept data from several I/O devices and transmit a unified stream of data on one communication line."));
//        ans4.add(new AnswerModel(3,1,"a half-duplex line is a communication line in which data can move in two directions, but not the same time."));
//        ans4.add(new AnswerModel(4,1,"batch processing is the preferred processing mode for telecommunication operations."));
//        mQuestionModel.add(new QuestionModel(4," Which of the following statements is incorrect? ",ans4));
//
//    }

    public void init() {
        mRecycler = findViewById(R.id.recycler);
        mRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //insertDummeyQuestions();
        QuestionListRequest request = new QuestionListRequest(examID);
        RetrofitService.getInstance(getApplicationContext()).getQuestionList(request, mServiceListener);
        questionList.clear();
        questionAdapter = new QuestionAdapter(getApplicationContext(), questionList, null);
        mRecycler.setAdapter(questionAdapter);
    }

    public Date getNextHour() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR_OF_DAY, 1);
        return cal.getTime();

//        long ONE_MINUTE_IN_MILLIS=60000;//millisecs
//
//        Calendar date = Calendar.getInstance();
//        long t= date.getTimeInMillis();
//        Date afterAddingTenMins=new Date(t + (1 * ONE_MINUTE_IN_MILLIS));
//        return afterAddingTenMins;
    }

    private long getMilisecondLeft(Date date) {
        long estDateInLong = date.getTime();
        long currentTimeinLong = Calendar.getInstance().getTimeInMillis();
        long diff = currentTimeinLong - estDateInLong;
        if (currentTimeinLong > estDateInLong) {
            diff = currentTimeinLong - estDateInLong;
        } else {
            diff = estDateInLong - currentTimeinLong;
        }
        long diffDay = diff / (24 * 60 * 60 * 1000);
        if (diffDay > 0) {
            return 0;
        } else {
            StringBuffer sbf = new StringBuffer();
            //diff = diff - (diffDay * 24 * 60 * 60 * 1000); //will give you remaining milli seconds relating to hours,minutes and seconds
            /*long diffHours = diff / (60 * 60 * 1000);
            if(diffHours>0){
                sbf.append(diffHours+" Houres ");
            }
            diff = diff - (diffHours * 60 * 60 * 1000);
            long diffMinutes = diff / (60 * 1000);
            if(diffMinutes>0){
                sbf.append(diffMinutes+" Minutes ");
            }
            diff = diff - (diffMinutes * 60 * 1000);
            long diffSeconds = diff / 1000;
            sbf.append(" Left");
            diff = diff - (diffSeconds * 1000);
            System.out.println(diffDay + "\t" + diffHours + "\t" + diffMinutes + "\t" + diffSeconds);*/
            return diff;
        }
    }

    private static final int RECORD_VIDEO_REQUEST = 1000;


    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        Uri videoUri = data.getData();

        if (requestCode == VIDEO_CAPTURE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Video saved to:\n" + videoUri, Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Video recording cancelled.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Failed to record video", Toast.LENGTH_LONG).show();
            }
        }
    }
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == RECORD_VIDEO_REQUEST) {
//            switch (resultCode) {
//                case RESULT_OK:
//                    Uri videoUri = data.getData();
//                    //Uri thumbnailUri =data.getParcelableExtra(FFmpegRecorderActivity.THUMBNAIL_URI_KEY);
//                    break;
//                case Activity.RESULT_CANCELED:
//                    break;
//                case FFmpegRecorderActivity.RESULT_ERROR:
////                    Exception error = (Exception)
////                            data.getSerializableExtra(FFmpegRecorderActivity.ERROR_PATH_KEY);
//                    break;
//            }
//        }
//    }

    private boolean hasCamera() {
        return (getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_ANY));
    }

    private static final int VIDEO_CAPTURE = 101;

    @Override
    public void onClick(View v) {
        if (v.getId() == action_finish.getId()) {
            countdownView.stop();
            List<Object> answers = questionAdapter.getmQuestionModel();
            List<QuestionAnswerModel> answerList = new ArrayList<>();
            TreeMap<String, Object> sorted = new TreeMap<>();
            Map<String, Object> map = new HashMap<>();
            for (int index = 0; index < answers.size(); index++) {
                if (answers.get(index) instanceof ImagebasedQuestions) {
                    ImagebasedQuestions qModel = (ImagebasedQuestions) answers.get(index);
                    answerList.add(new QuestionAnswerModel(qModel.getId(), qModel.getAnswerPosition(), "",qModel.getHaltTime()));
                    sorted.put(qModel.getId(), qModel);

                } else if (answers.get(index) instanceof OptionalQuestions) {
                    OptionalQuestions qModel = (OptionalQuestions) answers.get(index);
                    sorted.put(qModel.getId(), qModel);
                    answerList.add(new QuestionAnswerModel(qModel.getId(), qModel.getAnswerPosition(), "",qModel.getHaltTime()));
                } else {
                    PracticalQuestions qModel = (PracticalQuestions) answers.get(index);
                    sorted.put(qModel.getId(), qModel);
                    if (qModel.getOptions() instanceof Boolean) {
                        answerList.add(new QuestionAnswerModel(qModel.getId(), 0, qModel.getAnswer(),qModel.getHaltTime()));
                    } else {
                        answerList.add(new QuestionAnswerModel(qModel.getId(), qModel.getAnswerPosition(), "",qModel.getHaltTime()));
                    }
                }
            }
            // Copy all data from hashMap into TreeMap
            sorted.putAll(map);

            // Display the TreeMap which is naturally sorted
            for (Map.Entry<String, Object> entry : sorted.entrySet())
                Log.d("CSORT", "Key = " + entry.getKey() + ", Value = " + entry.getValue());
            AnswerResponse response = new AnswerResponse(AccPref.getUserId(getApplicationContext()), examID, answerList);
            Gson gson = new Gson();
            String obValue = gson.toJson(response, AnswerResponse.class);
            //createTempFiles();
            RetrofitService.getInstance(getApplicationContext()).submitAnswerList(response, mServiceListener);
            //startActivity(mVideoFile,mThumbnailFile);

            System.out.println("print val");
        } else if (v.getId() == txtNext.getId()) {
            if (questionCounter >= mQuestionModel.size() - 1) {
                finish();
            } else {
                currentQuestion.clear();
                questionCounter++;
                currentQuestion.add(mQuestionModel.get(questionCounter));
                questionAdapter.notifyDataSetChanged();
                if (questionCounter >= mQuestionModel.size() - 1) {
                    txtNext.setText("Finish");
                }
            }
        } else if (v.getId() == txtPrevious.getId()) {
            if (questionCounter > 1) {
                questionCounter--;
                currentQuestion.add(mQuestionModel.get(questionCounter));
                questionAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onRequestStarted(Object mObject) {
        showProgressDialog("");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        _handlar = null;
    }

    @Override
    public void onResponse(Object mObject) {
        hideDialog();
        if (mObject instanceof QuestionsListResponse) {
            QuestionsListResponse response = (QuestionsListResponse) mObject;
            if (response != null) {
                if (questionList == null) {
                    questionList = new ArrayList<>();
                }
                questionList.clear();
                if (response.getImagebased() != null) {
                    questionList.addAll(response.getImagebased());
                }
                if (response.getOptional() != null) {
                    questionList.addAll(response.getOptional());
                }
                if (response.getPractical() != null) {
                    questionList.addAll(response.getPractical());
                }


                if (questionList.size() == 0) {
                    Utility.showErrorMessageSheet(getSupportFragmentManager(), "Alert", "Question List not avaliable at this movement, Please try again, Later", false, new OnDialogClickListener() {
                        @Override
                        public void onOkClicked(Object object) {
                            finish();
                        }

                        @Override
                        public void onCancelClicked() {

                        }
                    });
                }
                questionAdapter.notifyDataSetChanged();
                try {
                    txt_question.setText(response.getOptional().get(0).getTc_name());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (mObject instanceof String) {
            String res = (String) mObject;
            if (TextUtils.isEmpty(res)) {
                showErrorMessageSheet(getSupportFragmentManager(), "Alert", "Unable to save Answers, Please try again later");
            } else if (!res.contains("Successfully")) {
                //showErrorMessageSheet(getSupportFragmentManager(),"Alert",res);
                Intent intent = new Intent(getApplicationContext(), RecordVideoActivity.class);
                intent.putExtra("examid", examID);
                startActivity(intent);
                finish();
            } else {
                Utility.showErrorMessageSheet(getSupportFragmentManager(), "SUCCESS", "Your answers saved successfully, Now Click here to record your Video", false, new OnDialogClickListener() {
                    @Override
                    public void onOkClicked(Object object) {
                        Intent intent = new Intent(getApplicationContext(), RecordVideoActivity.class);
                        intent.putExtra("examid", examID);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onCancelClicked() {

                    }
                });

            }
        }
    }

    @Override
    public void onFailure(Object mObject, Throwable t) {
        hideDialog();
    }

    private Spinner mOutputFormatSpinner;

    private void createTempFiles() {
        if (mVideoFile != null && mThumbnailFile != null) {
            return;
        }

        File dir = getExternalCacheDir();
        try {
            String videoExt = ".mp4";
            while (true) {
                int n = (int) (Math.random() * Integer.MAX_VALUE);
                String videoFileName = FILE_PREFIX + n + "." + videoExt;
                mVideoFile = new File(dir, videoFileName);
                if (!mVideoFile.exists() && mVideoFile.createNewFile()) {
                    String thumbnailFileName =
                            FILE_PREFIX + n + "." + THUMBNAIL_FILE_EXTENSION;
                    mThumbnailFile = new File(dir, thumbnailFileName);
                    if (!mThumbnailFile.exists() && mThumbnailFile.createNewFile()) {
                        return;
                    }
                    mVideoFile.delete();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /*FACE RECOGNIZATIONS*/
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
            if (sparseArray.size() == 0) {
                questionCounter++;
                mAlertView.setVisibility(View.VISIBLE);
            } else {
                mAlertView.setVisibility(View.GONE);
                questionCounter = 0;
            }
            if (questionCounter >= 3) {
                //showErrorMessageSheet(getSupportFragmentManager(),"Error","Face not detected, Your exam is suspended..");
                Utility.showErrorMessageSheet(getSupportFragmentManager(), "Error", "Face not detected, Your exam is suspended..Please click here to exit from exam", false, new OnDialogClickListener() {
                    @Override
                    public void onOkClicked(Object object) {

                        AccPref.clear(getApplicationContext());
                        Intent in = new Intent(getApplicationContext(), SplashActivity.class);
                        in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(in);
                    }

                    @Override
                    public void onCancelClicked() {
                        finish();
                    }
                });
            }
            Toast.makeText(getApplicationContext(), " frames  " + sparseArray.size(), Toast.LENGTH_LONG).show();
            //uploadFrontPhoto.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));
        }
    }

    @Override
    public void onCaptureDone(String pictureUrl, byte[] pictureData) {
        if (pictureData != null && pictureUrl != null) {
            runOnUiThread(() -> {
                final Bitmap bitmap = BitmapFactory.decodeByteArray(pictureData, 0, pictureData.length);
                File imgFile = new File(pictureUrl);

                if (imgFile.exists()) {

                    Bitmap bitMap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    myBitmap = bitMap;
                    tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.RGB_565);
                    /*if (pictureUrl.contains("0_pic.jpg")) {
                        uploadBackPhoto.setImageBitmap(myBitmap);
                    }else{
                        uploadFrontPhoto.setImageBitmap(myBitmap);
                    }*/

                }
                new BackgroundTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            });
            //showToast("Picture saved to " + pictureUrl);
        }
    }

    @Override
    public void onDoneCapturingAllPhotos(TreeMap<String, byte[]> picturesTaken) {

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
                Toast.makeText(LiveExamActivity.this, "Face Detector could not be set up on your device", Toast.LENGTH_SHORT).show();
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
    /*END FACE*/

    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        showErrorMessageSheet(getSupportFragmentManager(), "Alert", "Do you want to exit?", new OnDialogClickListener() {
            @Override
            public void onOkClicked(Object object) {
                finish();
            }

            @Override
            public void onCancelClicked() {

            }
        });
    }

}
