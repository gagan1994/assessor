package com.assessor.android.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.assessor.android.BaseActivity;
import com.assessor.android.R;
import com.assessor.android.activity.model.BatchInfo;
import com.assessor.android.adapter.StudentListAdapter;
import com.assessor.android.iface.ItemClickListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static androidx.core.content.FileProvider.getUriForFile;

public class StudentListActivity extends BaseActivity implements ItemClickListener, View.OnClickListener {

    RecyclerView recycler;
    List<BatchInfo> batchInfos = new ArrayList<>();

    public static final int REQUEST_IMAGE_CAPTURE = 0;
    public static final int REQUEST_GALLERY_IMAGE = 1;

    public static String fileName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);
        recycler = findViewById(R.id.recycler);
        init();

    }

    public void init() {
        getDumeryData();
        RecyclerView mRecycler = findViewById(R.id.recycler);
        mRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        StudentListAdapter menuAdapter = new StudentListAdapter(getApplicationContext(), batchInfos, this);
        mRecycler.setAdapter(menuAdapter);
    }

    private void getDumeryData() {
        batchInfos.clear();
        String date = "";
        batchInfos.add(new BatchInfo(1, "1937JAMH/Q0301-333221", "Retail", date, "ODAPADA SKILL DEVELOPMENT", "Sahil Sahoo"));
        batchInfos.add(new BatchInfo(2, "2037MH09/A0431-433322", "Electronics", date, "INKTEST TEAM", "Sangam Mishra"));
        batchInfos.add(new BatchInfo(3, "1873JAMH/Q1341-532121", "Retail", date, "SAHAYA TEAM", "Satyajeet Majhi"));
        batchInfos.add(new BatchInfo(4, "1213JEMH/Q3001-300021", "WEB", date, "SKILL DEVELOPMENT", "Sahil Sahoo"));
        batchInfos.add(new BatchInfo(5, "1937JAMH/Q0301-333221", "Retail", date, "INKTEST TEAM", "Sahil Sahoo"));
        batchInfos.add(new BatchInfo(6, "1117JAMH/Q0202-355221", "Electronics", date, "SAHAYA TEAM", "Sahil Sahoo"));
        batchInfos.add(new BatchInfo(7, "1937JAMH/Q0301-333221", "Retail", date, "ODAPADA SKILL DEVELOPMENT", "Sahil Sahoo"));
        batchInfos.add(new BatchInfo(8, "2037MH09/A0431-433322", "Electronics", date, "INKTEST TEAM", "Sangam Mishra"));
        batchInfos.add(new BatchInfo(9, "1873JAMH/Q1341-532121", "Retail", date, "SAHAYA TEAM", "Satyajeet Majhi"));
        batchInfos.add(new BatchInfo(10, "1213JEMH/Q3001-300021", "WEB", date, "SKILL DEVELOPMENT", "Sahil Sahoo"));
        batchInfos.add(new BatchInfo(11, "1937JAMH/Q0301-333221", "Retail", date, "INKTEST TEAM", "Sahil Sahoo"));
        batchInfos.add(new BatchInfo(12, "1117JAMH/Q0202-355221", "Electronics", date, "SAHAYA TEAM", "Sahil Sahoo"));

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemClickListener(Object object) {
        if (object instanceof String) {
            String response = (String) object;
            if (response.equalsIgnoreCase("IMAGE_CLICK")) {
                showImagePickerOptions(getApplicationContext());
            }
        }
    }


    public void showImagePickerOptions(Context context) {
        // setup the alert builder
        /*AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.lbl_set_profile_photo));

        // add a list
        String[] animals = {context.getString(R.string.lbl_take_camera_picture)};
        builder.setItems(animals, (dialog, which) -> {
            switch (which) {
                case 0:
                    takeCameraImage(REQUEST_IMAGE_CAPTURE);
                    break;
                case 1:
                    chooseImageFromGallery();
                    break;
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();*/
        takeCameraImage(REQUEST_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 120);
        }
    }

    private void takeCameraImage(int request) {
//        Dexter.withActivity(this)
//                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                .withListener(new MultiplePermissionsListener() {
//                    @Override
//                    public void onPermissionsChecked(MultiplePermissionsReport report) {
//                        if (report.areAllPermissionsGranted()) {
//                            fileName = System.currentTimeMillis() + ".jpg";
//                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getCacheImagePath(fileName));
//                            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//                                startActivityForResult(takePictureIntent, request);
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
//                        token.continuePermissionRequest();
//                    }
//                }).check();
    }

    private Uri getCacheImagePath(String fileName) {
        File path = new File(getExternalCacheDir(), "camera");
        if (!path.exists()) path.mkdirs();
        File image = new File(path, fileName);
        return getUriForFile(this, getPackageName() + ".provider", image);
    }

    private void chooseImageFromGallery() {
//        Dexter.withActivity(this)
//                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                .withListener(new MultiplePermissionsListener() {
//                    @Override
//                    public void onPermissionsChecked(MultiplePermissionsReport report) {
//                        if (report.areAllPermissionsGranted()) {
//                            fileName = System.currentTimeMillis() + ".jpg";
//                            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
//                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                            startActivityForResult(pickPhoto, REQUEST_GALLERY_IMAGE);
//                        }
//                    }
//
//                    @Override
//                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
//                        token.continuePermissionRequest();
//                    }
//                }).check();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Toast.makeText(this,"REceiverd",Toast.LENGTH_LONG).show();
        try {
            if (resultCode == RESULT_OK) {
                Uri imageUri = getCacheImagePath(fileName);
                File path = new File(getExternalCacheDir(), "camera");
                if (!path.exists()) path.mkdirs();
                File image = new File(path, fileName);
                //RetrofitService.getInstance(this).uploadFile(this, imageUri, image, "" + mTripInfo.getTripId(), REQUEST_START_TRIP, serviceListener);
                Toast.makeText(getApplicationContext(), "Image Captured", Toast.LENGTH_LONG).show();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s = cursor.getString(column_index);
        cursor.close();
        return s;
    }
}
