package com.assessor.android.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.assessor.android.R;
import com.assessor.android.ScoreManager;
import com.assessor.android.SplashActivity;
import com.assessor.android.adapter.SimpleFragmentPagerAdapter;
import com.assessor.android.cam.listener.PictureCapturingListener;
import com.assessor.android.cam.services.APictureCapturingService;
import com.assessor.android.cam.services.PictureCapturingServiceImplNew;
import com.assessor.android.cam.utils.CameraConfig;
import com.assessor.android.cam.utils.CameraError;
import com.assessor.android.cam.utils.CameraFacing;
import com.assessor.android.cam.utils.CameraImageFormat;
import com.assessor.android.cam.utils.CameraResolution;
import com.assessor.android.cam.utils.CameraRotation;
import com.assessor.android.cam.utils.HiddenCameraUtils;
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
import com.assessor.android.utility.NotificationUtil;
import com.assessor.android.utility.Utility;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cn.iwgang.countdownview.CountdownView;


public class QuizActivity extends HiddenCameraBase implements RetrofitServiceListener, View.OnClickListener, PictureCapturingListener, ActivityCompat.OnRequestPermissionsResultCallback, OnDialogClickListener {
    final Context context = null;

    ProgressBar progressBar;
    int score = 0;
    private int examID;
    List<Object> questionList = new ArrayList<>();
    RetrofitServiceListener mServiceListener;
    ImageView imgNext, imgPrev;
    ViewPager viewPager;
    TextView txtSkip;
    CountdownView countdownView;
    boolean isTimeCompleted = false;
    String ssc;

    long lastImageSyncTime = 0;


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

    TextView txtAttempts;
    CameraConfig mCameraConfig;

    int lastPageIndex = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.view_start);
        faceDetctionAttemts = 0;

        getLastLocationNewMethod();
        setContentView(R.layout.activity_quiz);
        mAlertView = findViewById(R.id.alert);
        txtAttempts = findViewById(R.id.txt_attempts);

        //Check for the camera permission for the runtime
//        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//
//            //Start camera preview
//            startCamera(mCameraConfig);
//        } else {
//            ActivityCompat.requestPermissions(QuizActivity.this, new String[]{Manifest.permission.CAMERA}, 101);
//        }
        /*9987152927*/

        imgNext = findViewById(R.id.action_next);
        imgPrev = findViewById(R.id.action_prev);
        txtSkip = findViewById(R.id.action_skip);
        imgNext.setOnClickListener(this);
        txtSkip.setOnClickListener(this);
        imgPrev.setOnClickListener(this);
        imgPrev.setVisibility(View.GONE);
        examID = getIntent().getExtras().getInt("exam_id");
        ssc = getIntent().getExtras().getString("ssc");
        TextView title = findViewById(R.id.title);
        title.setText(ssc);
        Date endTime = new Date(getIntent().getExtras().getLong("end_time"));
        //startQuiz();

        mCameraConfig = new CameraConfig()
                .getBuilder(QuizActivity.this)
                .setCameraFacing(CameraFacing.FRONT_FACING_CAMERA)
                .setCameraResolution(CameraResolution.MEDIUM_RESOLUTION)
                .setImageFormat(CameraImageFormat.FORMAT_JPEG)
                .setImageRotation(CameraRotation.ROTATION_270)
                .build();
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

            //Start camera preview
            startCamera(mCameraConfig);
        }
        mServiceListener = this;
        QuestionListRequest request = new QuestionListRequest(examID);
        RetrofitService.getInstance(getApplicationContext()).getQuestionList(request, mServiceListener);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 100);

        }
        pictureService = PictureCapturingServiceImplNew.getInstance(this);
        _handlar = new Handler(Looper.getMainLooper());
        delayProcess();


        Drawable nextDrawable = ContextCompat.getDrawable(this, R.drawable.next);
        imgNext.setImageDrawable(nextDrawable);
        countdownView = findViewById(R.id.cv_countdownView);
        countdownView.start(getMilisecondLeft(endTime));
        countdownView.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
            @Override
            public void onEnd(CountdownView cv) {
                isTimeCompleted = true;
                //txtNext.setEnabled(false);
                //txtPrevious.setEnabled(false);
                Utility.showErrorMessageSheet(getSupportFragmentManager(), "Time Completed", "Time has been completed", false, new OnDialogClickListener() {
                    @Override
                    public void onOkClicked(Object object) {
                        finish();
                    }

                    @Override
                    public void onCancelClicked() {
                        finish();
                    }
                });
            }
        });
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
            return diff;
        }
    }


    public void startQuiz() {


        // Find the view pager that will allow the user to swipe between fragments
        viewPager = findViewById(R.id.viewpager);
        progressBar = findViewById(R.id.progressBar);
        // Create an adapter that knows which fragment show be shown on each page
        SimpleFragmentPagerAdapter adapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(), onScoreChange, questionList);

        //Set the adapter onto the view pager
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position >= questionList.size() - 1) {
                    Drawable nextDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_tick);
                    imgNext.setImageDrawable(nextDrawable);
                } else {
                    Drawable nextDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.next);
                    imgNext.setImageDrawable(nextDrawable);
                }
                if (position > 0) {
                    imgPrev.setVisibility(View.VISIBLE);
                } else {
                    imgPrev.setVisibility(View.GONE);
                }
                if (lastPageIndex != position) {
                    calculateTime(position, lastPageIndex);
                }
                lastPageIndex = position;
            }

            @Override
            public void onPageSelected(int position) {
                progressBar.setProgress(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        progressBar.setMax(adapter.getCount() - 1);
        progressBar.setProgress(0);
    }

    private void calulateLastPageDuration(int lastPageIndex) {
        Object obj = questionList.get(lastPageIndex);
        long currentMiles = System.currentTimeMillis();
        if (obj instanceof ImagebasedQuestions) {
            //return TYPE_IMAGE;

            long startTime = ((ImagebasedQuestions) questionList.get(lastPageIndex)).getStartTime();
            if (startTime > 0) {
                ((ImagebasedQuestions) questionList.get(lastPageIndex)).setEndTime(System.currentTimeMillis());
                long currentDuration = currentMiles - startTime;
                long lastCapturedDuration = ((ImagebasedQuestions) questionList.get(lastPageIndex)).getDurationinMiles();
                ((ImagebasedQuestions) questionList.get(lastPageIndex)).setDurationinMiles(currentMiles);
                ((ImagebasedQuestions) questionList.get(lastPageIndex)).setHaltTime(getTimeDuration(startTime, currentMiles, lastCapturedDuration));
                // Toast.makeText(getApplicationContext(),((ImagebasedQuestions) questionList.get(lastPageIndex)).getHaltTime(),Toast.LENGTH_LONG).show();
            }

        } else if (obj instanceof OptionalQuestions) {
            //return TYPE_OPIONAL;
            long startTime = ((OptionalQuestions) questionList.get(lastPageIndex)).getStartTime();
            if (startTime > 0) {
                long currentDuration = currentMiles - startTime;
                long lastCapturedDuration = ((OptionalQuestions) questionList.get(lastPageIndex)).getDurationinMiles();
                ((OptionalQuestions) questionList.get(lastPageIndex)).setEndTime(System.currentTimeMillis());
                ((OptionalQuestions) questionList.get(lastPageIndex)).setHaltTime(getTimeDuration(startTime, currentMiles, lastCapturedDuration));
                //Toast.makeText(getApplicationContext(),((OptionalQuestions) questionList.get(lastPageIndex)).getHaltTime(),Toast.LENGTH_LONG).show();
            }
        } else {
            //return TYPE_PRACTICAL;
            long startTime = ((PracticalQuestions) questionList.get(lastPageIndex)).getStartTime();
            if (startTime > 0) {
                long currentDuration = currentMiles - startTime;
                long lastCapturedDuration = ((PracticalQuestions) questionList.get(lastPageIndex)).getDurationinMiles();
                ((PracticalQuestions) questionList.get(lastPageIndex)).setEndTime(System.currentTimeMillis());
                ((PracticalQuestions) questionList.get(lastPageIndex)).setHaltTime(getTimeDuration(startTime, currentMiles, lastCapturedDuration));
                //Toast.makeText(getApplicationContext(),((PracticalQuestions) questionList.get(lastPageIndex)).getHaltTime(),Toast.LENGTH_LONG).show();
            }
        }
    }

    private String getTimeDuration(long startTime, long currentTimeMillis, long lastDuration) {
        long milliseconds = currentTimeMillis - startTime + lastDuration;
        int minutes = (int) (milliseconds / 1000) / 60;
        int seconds = (int) (milliseconds / 1000) % 60;
        String strMinute = "" + minutes;
        String strSeconds = "" + seconds;
        if (minutes <= 9) {
            strMinute = "0" + minutes;
        }
        if (seconds <= 9) {
            strSeconds = "0" + seconds;
        }
        String duration = "00:" + strMinute + ":" + strSeconds;
        return duration;
    }

    private void calculateTime(int currentPageIndex, int lastPageIndex) {

        calulateLastPageDuration(lastPageIndex);
        //INSERT CURRENT TIME HERE
        Object obj = questionList.get(currentPageIndex);
        if (obj instanceof ImagebasedQuestions) {
            //return TYPE_IMAGE;
            ((ImagebasedQuestions) questionList.get(currentPageIndex)).setStartTime(System.currentTimeMillis());
        } else if (obj instanceof OptionalQuestions) {
            //return TYPE_OPIONAL;
            ((OptionalQuestions) questionList.get(currentPageIndex)).setStartTime(System.currentTimeMillis());
        } else {
            //return TYPE_PRACTICAL;
            ((PracticalQuestions) questionList.get(currentPageIndex)).setStartTime(System.currentTimeMillis());
        }
    }

    private String getId(Object a) {
        if (a instanceof ImagebasedQuestions) {
            ImagebasedQuestions obj1 = (ImagebasedQuestions) a;
            return obj1.getId();
        } else if (a instanceof OptionalQuestions) {
            OptionalQuestions obj1 = (OptionalQuestions) a;
            return obj1.getId();
        } else {
            PracticalQuestions obj1 = (PracticalQuestions) a;
            return obj1.getId();
        }
    }

    private boolean isAnswered(Object a) {
        if (a instanceof ImagebasedQuestions) {
            ImagebasedQuestions obj1 = (ImagebasedQuestions) a;
            return !TextUtils.isEmpty(obj1.getAnswer()) || obj1.getAnswerPosition() > 0;
        } else if (a instanceof OptionalQuestions) {
            OptionalQuestions obj1 = (OptionalQuestions) a;
            //return obj1.getId();
            return !TextUtils.isEmpty(obj1.getAnswer()) || obj1.getAnswerPosition() > 0;
        } else {
            PracticalQuestions obj1 = (PracticalQuestions) a;
            //return obj1.getId();
            return !TextUtils.isEmpty(obj1.getAnswer()) || obj1.getAnswerPosition() > 0;
        }
    }

    //<editor-fold desc="Listeners">

    // the interface method that is getting resolved here. Interface scoreManager onScore Change that includes logic for adding score and getting score.
    ScoreManager onScoreChange = new ScoreManager() {


        @Override
        public void addToScore(int position, String answer, int answerPosition) {
            Object obj = questionList.get(position);
            if (obj instanceof ImagebasedQuestions) {
                //return TYPE_IMAGE;
                ((ImagebasedQuestions) questionList.get(position)).setAnswer(answer);
                ((ImagebasedQuestions) questionList.get(position)).setAnswerPosition(answerPosition);
            } else if (obj instanceof OptionalQuestions) {
                //return TYPE_OPIONAL;
                ((OptionalQuestions) questionList.get(position)).setAnswer(answer);
                ((OptionalQuestions) questionList.get(position)).setAnswerPosition(answerPosition);
            } else {
                //return TYPE_PRACTICAL;
                ((PracticalQuestions) questionList.get(position)).setAnswer(answer);
                ((PracticalQuestions) questionList.get(position)).setAnswerPosition(answerPosition);
            }
        }

        @Override
        public int getScore() {
            return score;
        }
    };

    @Override
    public void onRequestStarted(Object mObject) {
        showProgressDialog("");
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

                /**********************************/
                TreeMap<Integer, Object> sorted = new TreeMap<>();
                Map<Integer, Object> map = new HashMap<>();
                for (int index = 0; index < questionList.size(); index++) {
                    if (questionList.get(index) instanceof ImagebasedQuestions) {
                        ImagebasedQuestions qModel = (ImagebasedQuestions) questionList.get(index);
                        map.put(Integer.parseInt(qModel.getId()), qModel);

                    } else if (questionList.get(index) instanceof OptionalQuestions) {
                        OptionalQuestions qModel = (OptionalQuestions) questionList.get(index);
                        map.put(Integer.parseInt(qModel.getId()), qModel);
                    } else {
                        PracticalQuestions qModel = (PracticalQuestions) questionList.get(index);
                        map.put(Integer.parseInt(qModel.getId()), qModel);

                    }
                }
                // Copy all data from hashMap into TreeMap
                sorted.putAll(map);

                // Display the TreeMap which is naturally sorted
                questionList.clear();
                for (Map.Entry<Integer, Object> entry : sorted.entrySet()) {
                    //Log.d("CSORT", "Key = " + entry.getKey() + ", Value = " + entry.getValue());
                    questionList.add(entry.getValue());
                }


                /*****************************/

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
                if (questionList != null) {
                    Collections.sort(questionList, new Comparator<Object>() {
                        @Override
                        public int compare(Object o1, Object o2) {
                            String o1Id = null;
                            String o2Id = null;
                            if (o1 instanceof ImagebasedQuestions) {
                                o1Id = ((ImagebasedQuestions) o1).getId();
                            }
                            if (o1 instanceof OptionalQuestions) {
                                o1Id = ((OptionalQuestions) o1).getId();
                            }
                            if (o1 instanceof PracticalQuestions) {
                                o1Id = ((PracticalQuestions) o1).getId();
                            }

                            if (o2 instanceof ImagebasedQuestions) {
                                o2Id = ((ImagebasedQuestions) o2).getId();
                            }
                            if (o2 instanceof OptionalQuestions) {
                                o2Id = ((OptionalQuestions) o2).getId();
                            }
                            if (o2 instanceof PracticalQuestions) {
                                o2Id = ((PracticalQuestions) o2).getId();
                            }
                            if (o1Id == null || o2Id == null) return 1;
                            return o1Id.compareTo(o2Id);
                        }
                    });
                }

                try {
                    //txt_question.setText(response.getOptional().get(0).getTc_name());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                startQuiz();
            }
        } else if (mObject instanceof String) {
            String res = (String) mObject;
            if (TextUtils.isEmpty(res)) {
                showErrorMessageSheet(getSupportFragmentManager(), "Alert", "Unable to save Answers, Please try again later");
            } else if (!res.contains("Successfully")) {
                //showErrorMessageSheet(getSupportFragmentManager(),"Alert",res);
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // Do something for lollipop and above versions
                    dispatchTakeVideoIntent();
                } else {
                    AccPref.setSuspended(getApplicationContext(), examID, true);
                    Intent intent = new Intent(getApplicationContext(), RecordVideoActivity.class);
                    intent.putExtra("examid", examID);
                    startActivity(intent);
                    finish();
                }
            } else {
                Utility.showErrorMessageSheet(getSupportFragmentManager(), "SUCCESS", "Your answers saved successfully, Now Click here to record your Video", false, new OnDialogClickListener() {
                    @Override
                    public void onOkClicked(Object object) {
                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            // Do something for lollipop and above versions
                            dispatchTakeVideoIntent();
                        } else {
                            AccPref.setSuspended(getApplicationContext(), examID, true);
                            Intent intent = new Intent(getApplicationContext(), RecordVideoActivity.class);
                            intent.putExtra("examid", examID);
                            startActivity(intent);
                            finish();
                        }
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

    @Override
    public void onClick(View v) {
        int itemPosition = viewPager.getCurrentItem();
        if (v.getId() == txtSkip.getId()) {
            if (itemPosition >= questionList.size() - 2) {
                Drawable nextDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_tick);
                imgNext.setImageDrawable(nextDrawable);
            } else {
                Drawable nextDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.next);
                imgNext.setImageDrawable(nextDrawable);
            }
            if (itemPosition >= questionList.size() - 1) {
                finishQuiz();
            } else {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            }
        } else if (v.getId() == imgPrev.getId()) {
            if (itemPosition > 0) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
                imgPrev.setVisibility(View.VISIBLE);
            } else {
                imgPrev.setVisibility(View.GONE);
            }
        } else {
            callNextBtn(itemPosition);
        }
    }

    private void callNextBtn(int itemPosition) {
        if (!isAnswered(questionList.get(itemPosition))) {
            showErrorMessageSheet(getSupportFragmentManager(), "Alert", "Please enter your answer");
        } else {
            if (itemPosition > 0) {
                imgPrev.setVisibility(View.VISIBLE);
            } else {
                imgPrev.setVisibility(View.GONE);
            }
            if (itemPosition >= questionList.size() - 2) {
                Drawable nextDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_tick);
                imgNext.setImageDrawable(nextDrawable);
            } else {
                Drawable nextDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.next);
                imgNext.setImageDrawable(nextDrawable);
            }
            if (itemPosition >= questionList.size() - 1) {
                finishQuiz();
            } else {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            }
        }
    }

    private void finishQuiz() {
        countdownView.stop();
        List<Object> answers = questionList;
        List<QuestionAnswerModel> answerList = new ArrayList<>();
        for (int index = 0; index < answers.size(); index++) {
            if (answers.get(index) instanceof ImagebasedQuestions) {
                ImagebasedQuestions qModel = (ImagebasedQuestions) answers.get(index);
                answerList.add(new QuestionAnswerModel(qModel.getId(), qModel.getAnswerPosition(), "", qModel.getHaltTime()));

            } else if (answers.get(index) instanceof OptionalQuestions) {
                OptionalQuestions qModel = (OptionalQuestions) answers.get(index);
                answerList.add(new QuestionAnswerModel(qModel.getId(), qModel.getAnswerPosition(), "", qModel.getHaltTime()));
            } else {
                PracticalQuestions qModel = (PracticalQuestions) answers.get(index);
                if (qModel.getOptions() instanceof Boolean) {
                    answerList.add(new QuestionAnswerModel(qModel.getId(), 0, qModel.getAnswer(), qModel.getHaltTime()));
                } else {
                    answerList.add(new QuestionAnswerModel(qModel.getId(), qModel.getAnswerPosition(), "", qModel.getHaltTime()));
                }
            }
        }
        AnswerResponse response = new AnswerResponse(AccPref.getUserId(getApplicationContext()), examID, answerList);
        Gson gson = new Gson();
        String obValue = gson.toJson(response, AnswerResponse.class);
        //createTempFiles();
        RetrofitService.getInstance(getApplicationContext()).submitAnswerList(response, mServiceListener);
        //startActivity(mVideoFile,mThumbnailFile);

        System.out.println("print val");
    }

    //</editor-fold>
    static final int REQUEST_VIDEO_CAPTURE = 1;

    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Uri videoUri = intent.getData();
            File imageFile = new File(getRealPathFromURI(videoUri));
            File file = new File(videoUri.getPath());

            //videoView.setVideoURI(videoUri);
            RetrofitService.getInstance(getApplicationContext()).uploadFile(imageFile.getAbsoluteFile(), AccPref.getUserId(getApplicationContext()),
                    AccPref.getExamId(getApplicationContext()),
                    Utility.parseDouble(AccPref.getLat(getApplicationContext())),
                    Utility.parseDouble(AccPref.getLong(getApplicationContext()))
                    , new RetrofitServiceListener() {
                        @Override
                        public void onRequestStarted(Object mObject) {
                            showProgressDialog("");
                        }

                        @Override
                        public void onResponse(Object mObject) {
                            hideDialog();
                            if (mObject instanceof String) {
                                Intent intent1 = new Intent(getApplicationContext(), ThankYouActivity.class);
                                startActivity(intent1);
                                finish();
//                                Utility.showErrorMessageSheet(getSupportFragmentManager(), "Thank you", "Exam completed..theory and viva done.", false, new OnDialogClickListener() {
//                                    @Override
//                                    public void onOkClicked(Object object) {
//                                        Intent intent=new Intent(getApplicationContext(),ExamSchedule.class);
//                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                        startActivity(intent);
//                                    }
//
//                                    @Override
//                                    public void onCancelClicked() {
//
//                                    }
//                                });
                            }
                        }

                        @Override
                        public void onFailure(Object mObject, Throwable t) {
                            hideDialog();
                            if (mObject instanceof String) {
                                showErrorMessageSheet(getSupportFragmentManager(), "Alert", (String) mObject);
                            }
                        }
                    });
        }
    }


    private void delayProcess() {
        if (_handlar != null) {
            _handlar.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 100ms
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onProcessFace();
                        }
                    });


                }
            }, 1 * 20 * 1000);
        }
    }

    private void onProcessFace() {
        //pictureService.startCapturing(this);
        if (_handlar != null) {
            takePicture();
            delayProcess();
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
                faceDetctionAttemts++;
                mAlertView.setVisibility(View.VISIBLE);
                txtAttempts.setText(String.valueOf(faceDetctionAttemts));
                NotificationUtil.showNotification(getApplicationContext(), "Face Not Detected", "Your Face is not detected, Please ensure your face is infront of front camera");
            } else {
                //mAlertView.setVisibility(View.GONE);
                //faceDetctionAttemts=0;
            }
            if (faceDetctionAttemts >= 3) {
                _handlar = null;
                countdownView.setVisibility(View.GONE);
                countdownView.stop();
                NotificationUtil.showNotification(getApplicationContext(), "Your Exam is Suspended", "Unable to recognize your presents, So your exam is suspended");
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

    @Override
    public void onImageCapture(@NonNull File imageFile) {
        // Convert file to bitmap.
        //stopCamera();

        // Do something.
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        myBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);

        if (isTimeExceed()) {
            Bitmap bitmap = myBitmap.copy(Bitmap.Config.ARGB_8888, true);
            //upload Image
            File file = Utility.getFileWithdrawText(getApplicationContext(), AccPref.getUserName(getApplicationContext()), bitmap);
            lastImageSyncTime = System.currentTimeMillis();
            RetrofitService.getInstance(getApplicationContext()).uploadImageFile(file, AccPref.getUserId(getApplicationContext()),
                    AccPref.getExamId(getApplicationContext()),
                    Utility.getTime(), mServiceListener);
        }
        //Display the image to the image view
        //((ImageView) findViewById(R.id.img)).setImageBitmap(myBitmap);
        new BackgroundTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private boolean isTimeExceed() {
        if (lastImageSyncTime == 0) {
            return true;
        } else {
            long milliseconds = System.currentTimeMillis() - lastImageSyncTime;
            int minutes = (int) (milliseconds / 1000) / 60;
            if (minutes >= 15) {
                return true;
            }
            return false;
        }
    }

    @Override
    public void onCameraError(int errorCode) {

        switch (errorCode) {
            case CameraError.ERROR_CAMERA_OPEN_FAILED:
                //Camera open failed. Probably because another application
                //is using the camera
                Toast.makeText(getApplicationContext(), "Camera open failed. Probably because another application", Toast.LENGTH_LONG).show();
                break;
            case CameraError.ERROR_IMAGE_WRITE_FAILED:
                //Image write failed. Please check if you have provided WRITE_EXTERNAL_STORAGE permission
                Toast.makeText(getApplicationContext(), "Image write failed. Please check if you have provided WRITE_EXTERNAL_STORAGE permission", Toast.LENGTH_LONG).show();
                break;
            case CameraError.ERROR_CAMERA_PERMISSION_NOT_AVAILABLE:
                //camera permission is not available
                //Ask for the camra permission before initializing it.
                Toast.makeText(getApplicationContext(), "Ask for the camra permission before initializing it.", Toast.LENGTH_LONG).show();
                break;
            case CameraError.ERROR_DOES_NOT_HAVE_OVERDRAW_PERMISSION:
                //Display information dialog to the user with steps to grant "Draw over other app"
                //permission for the app.
                Toast.makeText(getApplicationContext(), "permission for the app", Toast.LENGTH_LONG).show();
                HiddenCameraUtils.openDrawOverPermissionSetting(this);
                break;
            case CameraError.ERROR_DOES_NOT_HAVE_FRONT_CAMERA:
                Toast.makeText(this, "Your device does not have front camera.", Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "Your device does not have front camera.", Toast.LENGTH_LONG).show();
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
                Toast.makeText(getApplicationContext(), "Face Detector could not be set up on your device", Toast.LENGTH_SHORT).show();
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
        showErrorMessageSheet(getSupportFragmentManager(), "Alert", "Do you want to exit?", this);
    }

    @Override
    public void onOkClicked(Object object) {
        finish();
    }

    @Override
    public void onCancelClicked() {

    }

}