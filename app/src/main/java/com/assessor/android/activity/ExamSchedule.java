package com.assessor.android.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.assessor.android.BaseActivity;
import com.assessor.android.R;
import com.assessor.android.activity.model.BatchInfo;
import com.assessor.android.adapter.ExamSchedularAdapter;
import com.assessor.android.cam.SquareActivity;
import com.assessor.android.iface.ItemClickListener;
import com.assessor.android.iface.OnDialogClickListener;
import com.assessor.android.retrofit.RetrofitService;
import com.assessor.android.retrofit.RetrofitServiceListener;
import com.assessor.android.retrofit.request.ExamScheduleRequest;
import com.assessor.android.retrofit.response.ExamListResponse;
import com.assessor.android.retrofit.response.ExamScheduleModel;
import com.assessor.android.utility.AccPref;
import com.assessor.android.utility.Utility;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExamSchedule extends BaseActivity implements ItemClickListener, RetrofitServiceListener {
    List<BatchInfo> batchInfos = new ArrayList<>();
    String date = "26-02-2020";
    RetrofitServiceListener mServiceListener;
    RecyclerView mRecycler;
    ExamSchedularAdapter menuAdapter;
    List<ExamScheduleModel> examScheduleModelList;
    int PERMISSION_REQUEST = 1022;

    Object mSelectedModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_schedule);
        mServiceListener = this;

        TextView txtCam = findViewById(R.id.cam);
        txtCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(getApplicationContext(), LiveExamActivity.class));
            }
        });
        txtCam.setVisibility(View.GONE);
        init();

    }


    public void init() {
        //getDumeryData();
        examScheduleModelList = new ArrayList<>();
        ExamScheduleRequest request = new ExamScheduleRequest(AccPref.getUserId(getApplicationContext()));
        RetrofitService.getInstance(getApplicationContext()).getExamSchedule(request, mServiceListener);
        mRecycler = findViewById(R.id.recycler);
        mRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        menuAdapter = new ExamSchedularAdapter(getApplicationContext(), examScheduleModelList, this);
        mRecycler.setAdapter(menuAdapter);
    }


    public boolean isPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_DENIED;
    }

    @Override
    public void onItemClickListener(Object object) {
        if (object instanceof ExamScheduleModel) {
            mSelectedModel = object;
            if (isPermission(Manifest.permission.RECORD_AUDIO) && isPermission(Manifest.permission.ACCESS_FINE_LOCATION) && isPermission(Manifest.permission.CAMERA) && isPermission(Manifest.permission.RECORD_AUDIO) && isPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ExamScheduleModel model = (ExamScheduleModel) object;
                if (model.isEnroll()) {
                    Utility.showErrorMessageSheet(getSupportFragmentManager(), "Alert", "This Exam is already completed...", false, new OnDialogClickListener() {
                        @Override
                        public void onOkClicked(Object object) {

                        }

                        @Override
                        public void onCancelClicked() {

                        }
                    });
                } else if (AccPref.isSuspended(getApplicationContext(), model.getId())) {
                    Utility.showErrorMessageSheet(getSupportFragmentManager(), "Alert", "Your Exam is suspended..", false, new OnDialogClickListener() {
                        @Override
                        public void onOkClicked(Object object) {

                        }

                        @Override
                        public void onCancelClicked() {

                        }
                    });
                } else {
                    if (AccPref.isExamCompleted(getApplicationContext(), model.getId())) {
                        Utility.showErrorMessageSheet(getSupportFragmentManager(), "Alert", "This Exam is already completed...", false, new OnDialogClickListener() {
                            @Override
                            public void onOkClicked(Object object) {

                            }

                            @Override
                            public void onCancelClicked() {

                            }
                        });
                    } else {
                        Intent intent = new Intent(getApplicationContext(), SquareActivity.class);
                        AccPref.setExamId(getApplicationContext(), model.getId());
                        intent.putExtra("exam_id", model.getId());
                        intent.putExtra("ssc", model.getSsc());
                        Date endDate = Utility.getEndDateTime(model.getExam_date() + " " + model.getExam_time(), model.getExam_duration());
                        intent.putExtra("end_time", endDate.getTime());
                        startActivity(intent);
                    }
                }
            } else {
                int permissionStatus = checkAndRequestPermissions(145);
                switch (permissionStatus) {
                    case 0:
                        ExamScheduleModel model = (ExamScheduleModel) object;
                        Intent intent = new Intent(getApplicationContext(), QuizActivity.class);
                        AccPref.setExamId(getApplicationContext(), model.getId());
                        intent.putExtra("exam_id", model.getId());
                        intent.putExtra("ssc", model.getSsc());
                        Date endDate = Utility.getEndDateTime(model.getExam_date() + " " + model.getExam_time(), model.getExam_duration());
                        intent.putExtra("end_time", endDate.getTime());
                        startActivity(intent);
                        break;
                    case -1:
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                        break;
                    case -2:
                        showLocationDialog();
                        break;
                }
//                if (!isLocationEnabled(this)) {
//                    showLocationDialog();
//                }else {
//                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO,
//                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
//                }

            }
        }
    }

    @Override
    public void onRequestStarted(Object mObject) {
        showProgressDialog("");
    }

    @Override
    public void onResponse(Object mObject) {
        hideDialog();
        if (mObject instanceof ExamListResponse) {
            //ExamScheduleResponse examScheduleResponse = (ExamScheduleResponse) mObject;
            ExamListResponse examScheduleResponse = (ExamListResponse) mObject;
            if (examScheduleResponse != null) {
                try {
                    for (int index = 0; index < examScheduleResponse.getList().size(); index++) {
                        examScheduleResponse.getList().get(index).setCurrentDateTime(examScheduleResponse.getCurrentDate());
                        if (examScheduleResponse.getExamStatus().containsKey(examScheduleResponse.getList().get(index).getId())) {
                            if (examScheduleResponse.getExamStatus().get(examScheduleResponse.getList().get(index).getId()).equalsIgnoreCase("1"))
                                examScheduleResponse.getList().get(index).setEnroll(true);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                examScheduleModelList.clear();
                examScheduleModelList.addAll(examScheduleResponse.getList());
                updateSchedule();
            }
        }
    }

    private void updateSchedule() {
        menuAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFailure(Object mObject, Throwable t) {
        hideDialog();
        showErrorAlert("Alert", "Exam not Scheduled at this movement, Please try again later");
    }


    public boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //showLocationDialog();
            checkAndRequestPermissions(111);
            return true;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            /*locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);*/
        }
    }

    private int checkAndRequestPermissions(int request_code) {

        int status = 0;
        if (!isFinishing()) {
            List<String> permissions = new ArrayList<>();
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            permissions.add(Manifest.permission.RECORD_AUDIO);
            permissions.add(Manifest.permission.CAMERA);
            boolean isNeverASkSelected = false;
            if (Build.VERSION.SDK_INT >= 23)
                if (permissions.size() > 0) {
                    boolean isNotGranted = false;
                    List<String> listPermissionsNeeded = new ArrayList<>();

                    for (int i = 0; i < permissions.size(); i++) {
                        int hasPermission = ContextCompat.checkSelfPermission(this, permissions.get(i));

                        int permissionStatus = getPStatus(hasPermission, permissions.get(i));
                        status = permissionStatus;
                        if (permissionStatus == 0) {
                            //GRANTED
                        } else if (permissionStatus == -1) {
                            //NOT GRANTED
                            listPermissionsNeeded.add(permissions.get(i));
                        } else if (permissionStatus == -2) {
                            isNotGranted = true;
                            listPermissionsNeeded.add(permissions.get(i));
                        } else {
                            // UNKNOWN
                        }
                    }
                    if (isNotGranted) {
                        //showLocationDialog();
                        //openPermissionSetting();
                    }
                    if (!listPermissionsNeeded.isEmpty()) {
                        ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), request_code);
                        status = listPermissionsNeeded.size();
                    }
                }
        }
        return status;
    }

    private void showLocationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ExamSchedule.this);
        builder.setTitle("Location Config")
                .setMessage("Make sure Location Service is enabled..")
                .setPositiveButton("Turn On Location",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private int getPStatus(int status, String permission) {
        if (status == PackageManager.PERMISSION_GRANTED) {
            return 0;
        } else if (status == PackageManager.PERMISSION_DENIED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                //Show permission explanation dialog...
                return -1;
            } else {
                //Never ask again selected, or device policy prohibits the app from having that permission.
                //So, disable that feature, or fall back to another situation...
                return -2;
            }
        }
        return -3;
    }
}
