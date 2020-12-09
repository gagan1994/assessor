package com.assessor.android.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.fragment.app.FragmentManager;

import com.assessor.android.bs.BottomSheetErrorDialogFragment;
import com.assessor.android.iface.OnDialogClickListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Utility {


    public static void showErrorMessageSheet(FragmentManager manager, String title, String message, boolean isCancelable, OnDialogClickListener mListener) {
        BottomSheetErrorDialogFragment bottomSheetFragment = new BottomSheetErrorDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(LocalConstants.CONST_TITLE, title);
        bundle.putString(LocalConstants.CONST_MESSAGE, message);
        bundle.putSerializable(LocalConstants.CONST_LISTENER, mListener);

        bundle.putSerializable("listener", mListener);
        bottomSheetFragment.setArguments(bundle);
        bottomSheetFragment.setCancelable(isCancelable);
        bottomSheetFragment.show(manager, bottomSheetFragment.getTag());
    }

    public static Date getDateTime(String date) {
        try {
            //2020-07-27 08:57:37.0
            //Jul 27 2020 2:09PM
            //"12/04/2000"
            //30-08-2020 18:38:06
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date1 = sdf.parse(date);
            //SimpleDateFormat sdf1= new SimpleDateFormat("hh:mm aa");
            return date1;
        } catch (Exception e) {
            return null;
        }
    }

    public static String getTime() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Date date1 = new Date();
            return sdf.format(date1);
        } catch (Exception e) {
            return "";
        }
    }

    public static Date getCurrentDateTime(String date) {
        try {

            //30-08-2020 18:38:06
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
            Date date1 = sdf.parse(date);
            //SimpleDateFormat sdf1= new SimpleDateFormat("hh:mm aa");
            return date1;
        } catch (Exception e) {
            return null;
        }
    }

    public static String formateDateTime(Date date) {
        try {
            //2020-07-27 08:57:37.0
            //Jul 27 2020 2:09PM
            //"12/04/2000"
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
            String date1 = sdf.format(date);
            //SimpleDateFormat sdf1= new SimpleDateFormat("hh:mm aa");
            return date1;
        } catch (Exception e) {
            return null;
        }
    }

    public static Date getEndDateTime(String date, String duration) {
        try {
            //2020-07-27 08:57:37.0
            //Jul 27 2020 2:09PM
            //"12/04/2000"

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
            Date date1 = sdf.parse(date);
            try {
                String[] mDur = duration.split(":");
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date1);
                calendar.add(Calendar.HOUR_OF_DAY, Integer.parseInt(mDur[0]));
                calendar.add(Calendar.MINUTE, Integer.parseInt(mDur[1]));
                date1 = calendar.getTime();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //SimpleDateFormat sdf1= new SimpleDateFormat("hh:mm aa");
            return date1;
        } catch (Exception e) {
            return null;
        }
    }

    public static String getDate(Date date) {
        try {
            //2020-07-27 08:57:37.0
            //Jul 27 2020 2:09PM
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd ");
            return sdf.format(date);
            //SimpleDateFormat sdf1= new SimpleDateFormat("hh:mm aa");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static long getMilisecondLeft(Date date) {
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
            return diff;
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

    public static String getHour(Date date) {
        try {

            SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm");
            return sdf1.format(date);
        } catch (Exception e) {
            return null;
        }
    }

    public static double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public static String getAddress(Context context) {
        try {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(context, Locale.getDefault());

            addresses = geocoder.getFromLocation(Utility.parseDouble(AccPref.getLat(context)), Utility.parseDouble(AccPref.getLong(context)), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
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

    public static File getFileWithdrawText(Context context,String captionString, Bitmap bm) {
        Canvas cv = new Canvas(bm);
        File file = null;
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

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm");
        String time = sdf.format(new Date());
        cv.drawText(time,
                (cv.getWidth() / 2) - (2 * time.length()), cv.getHeight() - (rectText.height() / 3), paintText);
        String address = getAddress(context);
        cv.drawText(address,
                ((cv.getWidth() / 2) - (2 * address.length())), cv.getHeight() - 15, paintText);
        /*cv.drawText(location,
                ((int)(cv.getWidth() /2 )), cv.getHeight() - 15, paintText);*/

//        TOP LEFT
//        cv.drawText(captionString, 0, rectText.height(), paintText);

        try {
            file = new File(context.getCacheDir() + "/img.jpg");
            FileOutputStream outStream = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, outStream);
            outStream.flush();
            outStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}
