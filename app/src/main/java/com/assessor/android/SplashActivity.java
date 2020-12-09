package com.assessor.android;


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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.TaskStackBuilder;

import com.assessor.android.activity.LoginActivity;
import com.assessor.android.activity.RecordVideoActivity;
import com.assessor.android.utility.AccPref;
import com.assessor.android.utility.Utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SplashActivity extends BaseActivity {

    ImageView img;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Build the camera
        getLastLocationNewMethod();

//        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(cameraIntent, 122);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //startActivity(new Intent(this, RecordVideoActivity.class));
        //startService(new Intent(getApplicationContext(),CameraService.class));
        //startActivity(new Intent(this, CameraActivity.class));
        //startActivity(new Intent(this, SquareActivity.class));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(AccPref.getUserId(getApplicationContext())<=0) {
                        TaskStackBuilder.create(getApplicationContext())
                                .addNextIntentWithParentStack(new Intent(getApplicationContext(), LoginActivity.class))
                                .addNextIntent(new Intent(getApplicationContext(), LoginActivity.class))
                                .startActivities();
                    }else{
                        TaskStackBuilder.create(getApplicationContext())
                                .addNextIntentWithParentStack(new Intent(getApplicationContext(), LoginActivity.class))
                                .addNextIntent(new Intent(getApplicationContext(), LoginActivity.class))
                                .startActivities();
                    }
                }
            },4000);

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 122 && resultCode == Activity.RESULT_OK) {
            try {
                FileOutputStream outStream = new FileOutputStream(new File(getCacheDir() + "/img.jpg"));

                Bitmap photo = (Bitmap) data.getExtras().get("data");
                int h = (int) (photo.getHeight() * 1.5);
                int w = (int) (photo.getWidth() * 1.5);
                photo = getResizedBitmap(photo, 270, 390);
                Bitmap mBitmap = photo.copy(Bitmap.Config.ARGB_8888, true);
                drawText("Sudhir Patil", mBitmap);
                //String mText = "Kolhapur, Maharastra";
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

    public String getAddress() {
        try {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());

            addresses = geocoder.getFromLocation(Utility.parseDouble(AccPref.getLat(getApplicationContext())), Utility.parseDouble(AccPref.getLong(getApplicationContext())), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            StringBuffer sbf = new StringBuffer();
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String subLocality = addresses.get(0).getSubLocality();
            String knownName = addresses.get(0).getFeatureName();
            sbf.append(subLocality);
            sbf.append(" " + city);
            sbf.append(" " + state);
            return sbf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public void drawText(String captionString, Bitmap bm) {
        Canvas cv = new Canvas(bm);

        int height = 720;
        int width = 480;
        cv.drawBitmap(bm, 0, 0, null);

        //Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        Paint paintText = new Paint(Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        paintText.setColor(Color.WHITE);
        paintText.setTextSize(8.0F);
        paintText.setAntiAlias(true);
        paintText.setStyle(Paint.Style.FILL);
//                paintText.setShadowLayer(10f, 10f, 10f, Color.BLACK);

        Rect rectText = new Rect();
        paintText.setTextAlign(Paint.Align.LEFT);
        paintText.getTextBounds(captionString, 0, captionString.length(), rectText);

//    TOP CENTER
//        cv.drawText(captionString,
//                (cv.getWidth() / 2) - (rectText.width() / 2), rectText.height() + (rectText.height() / 3), paintText);
//CENTER
//        cv.drawText(captionString,
//                (cv.getWidth() / 2) - (rectText.width() / 2), cv.getHeight() / 2, paintText);
//BOTTOM CENTER
//        cv.drawText(captionString,
//                (cv.getWidth() / 2) - (rectText.width() / 2), cv.getHeight() - (rectText.height() / 3), paintText);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm");
        String time = sdf.format(new Date());
        cv.drawText(time,
                (cv.getWidth() / 2) - (2 * time.length()), cv.getHeight() - (rectText.height() / 3), paintText);
        String address = getAddress();
        cv.drawText(address,
                ((cv.getWidth() / 2) - (2 * address.length())), cv.getHeight() - 15, paintText);
        /*cv.drawText(location,
                ((int)(cv.getWidth() /2 )), cv.getHeight() - 15, paintText);*/

//        TOP LEFT
//        cv.drawText(captionString, 0, rectText.height(), paintText);

        Toast.makeText(getApplicationContext(),
                "drawText: " + captionString,
                Toast.LENGTH_LONG).show();
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

    @NonNull
    private Bitmap createBitmap(@NonNull String text, @Nullable Bitmap reuseBmp) {
        Canvas mCanvas = new Canvas(reuseBmp);
        int boundsWidth = mCanvas.getWidth();

        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(10);
        // init params - size, color, typeface
        textPaint.setStyle(Paint.Style.FILL);
        //textPaint.setTextSize(textLayer.getFont().getSize() * mCanvas.getWidth());
        //textPaint.setColor(textLayer.getFont().getColor());
        //textPaint.setTypeface(fontProvider.getTypeface(textLayer.getFont().getFace()));

        // drawing text guide : http://ivankocijan.xyz/android-drawing-multiline-text-on-canvas/
        // Static layout which will be drawn on canvas
        StaticLayout sl = new StaticLayout(
                text, // - text which will be drawn
                textPaint,
                boundsWidth, // - width of the layout
                Layout.Alignment.ALIGN_CENTER, // - layout alignment
                1, // 1 - text spacing multiply
                1, // 1 - text spacing add
                true); // true - include padding

        // calculate height for the entity, min - Limits.MIN_BITMAP_HEIGHT
        int boundsHeight = sl.getHeight();

        // create bitmap not smaller than TextLayer.Limits.MIN_BITMAP_HEIGHT
        int bmpHeight = mCanvas.getHeight(); /*(int) (canvasHeight * Math.max(TextLayer.Limits.MIN_BITMAP_HEIGHT,
                1.0F * boundsHeight / canvasHeight));*/

        // create bitmap where text will be drawn
        Bitmap bmp;
        if (reuseBmp != null && reuseBmp.getWidth() == boundsWidth
                && reuseBmp.getHeight() == bmpHeight) {
            // if previous bitmap exists, and it's width/height is the same - reuse it
            bmp = reuseBmp;
            bmp.eraseColor(Color.TRANSPARENT); // erase color when reusing
        } else {
            bmp = Bitmap.createBitmap(boundsWidth, bmpHeight, Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bmp);
        canvas.save();

        // move text to center if bitmap is bigger that text
        if (boundsHeight < bmpHeight) {
            //calculate Y coordinate - In this case we want to draw the text in the
            //center of the canvas so we move Y coordinate to center.
            float textYCoordinate = (bmpHeight - boundsHeight) / 2;
            canvas.translate(0, textYCoordinate);
        }

        //draws static layout on canvas
        sl.draw(canvas);
        canvas.restore();

        return bmp;
    }

}
