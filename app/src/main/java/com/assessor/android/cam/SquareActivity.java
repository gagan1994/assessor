package com.assessor.android.cam;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.assessor.android.BaseActivity;
import com.assessor.android.R;
import com.assessor.android.activity.QuizActivity;
import com.assessor.android.utility.AccPref;
import com.assessor.android.utility.Utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SquareActivity extends BaseActivity {
    private static final int ACTION_IMAGE_CAPTURE = 122;
    ImageView img;
    String ssc;
    long endTime;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_square);
        img = findViewById(R.id.img);
        try {
            ssc = getIntent().getExtras().getString("ssc");
            endTime = getIntent().getExtras().getLong("end_time");
        } catch (Exception e) {
            e.printStackTrace();
        }
        getLastLocationNewMethod();
        findViewById(R.id.actionnext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), QuizActivity.class);
                intent.putExtra("exam_id", AccPref.getExamId(getApplicationContext()));
                intent.putExtra("ssc", ssc);
                intent.putExtra("end_time", endTime);
                startActivity(intent);
                finish();
            }
        });

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, ACTION_IMAGE_CAPTURE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTION_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            try {
                FileOutputStream outStream = new FileOutputStream(new File(getCacheDir() + "/img.jpg"));

                Bitmap photo = (Bitmap) data.getExtras().get("data");
                int h = (int) (photo.getHeight() * 1.5);
                int w = (int) (photo.getWidth() * 1.5);
                photo = getResizedBitmap(photo, 270, 390);
                Bitmap mBitmap = photo.copy(Bitmap.Config.ARGB_8888, true);
                drawText("Sudhir Patil", mBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    public List<String> getAddress() {
        try {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());
            addresses = geocoder.getFromLocation(Utility.parseDouble(AccPref.getLat(getApplicationContext())),
                    Utility.parseDouble(AccPref.getLong(getApplicationContext())), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            StringBuffer sbf = new StringBuffer();
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String subLocality = addresses.get(0).getSubLocality();
            String knownName = addresses.get(0).getFeatureName();
            List<String> list=new ArrayList<>();
            list.add(subLocality);
            list.add(city);
            list.add(state);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<String>();
    }

    public void drawText(String captionString, Bitmap bm) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm");
        String time = sdf.format(new Date());
        List<String> address=getAddress();
        address.add(0,time);
        bm = waterMark(bm, address);
        img.setImageBitmap(bm);
        try {
            FileOutputStream outStream = new FileOutputStream(new File(getCacheDir() + "/img.jpg"));
            bm.compress(Bitmap.CompressFormat.JPEG, 90, outStream);
            outStream.flush();
            outStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap waterMark(Bitmap src,
                                   List<String> watermark) {
        int widthSreen = src.getWidth();   // 1080L // 1920
        int heightScreen = src.getHeight();  // 1343L  // 2387


        Bitmap result = Bitmap.createBitmap(widthSreen, heightScreen, src.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(src, 0, 0, null);
        Paint paint = new Paint();

        int size = ((widthSreen * 5) / 100);
        paint.setTextSize(size);
        Paint.FontMetrics fm = new Paint.FontMetrics();
        //        paint.setTextSize(18.0f);
        paint.getFontMetrics(fm);
        paint.setColor(Color.WHITE);
        int height=heightScreen*5/100;
        int bottom = watermark.size() * height;
        for (int i = 0; i < watermark.size(); i++) {
            String watText = watermark.get(i);
            canvas.drawText(watText,
                    (src.getWidth() - ((int) paint.measureText(watText) + 10)),
                    (src.getHeight() - bottom)
                    , paint);
            bottom = bottom - height;
        }
        return result;
    }
}
