package com.assessor.android;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.assessor.android.activity.LoginActivity;
import com.assessor.android.activity.VerificationActivity;
import com.assessor.android.bs.BSLogoutDialogFragment;
import com.assessor.android.bs.BottomSheetErrorDialogFragment;
import com.assessor.android.iface.ItemClickListener;
import com.assessor.android.iface.OnDialogClickListener;
import com.assessor.android.utility.AccPref;
import com.assessor.android.utility.LocalConstants;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;


public class BaseActivity extends AppCompatActivity {
    ProgressDialog mProgressBar = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void navigateDashboard() {
        Intent intent = new Intent(getApplicationContext(), VerificationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    /**
     * Inilise Dialog
     */
    private void initDialog() {
        if (!isFinishing()) {
            mProgressBar = new ProgressDialog(this);
            mProgressBar.setCancelable(false);//you can cancel it by pressing back button
        }
    }

    /**
     * Show Error Message
     */
    public void showErrorAlert(String title, String message) {
        if (!isFinishing()) {
            if (builder == null) {
                initAlertDialog();
            }
            builder.setTitle(title).setMessage(message).show();
        }
    }

    public void showUserNameScreen() {

//        checkLogin(user.getPhoneNumber(),"","m");
        //  RewalletPreferences.setIsGuest(getApplicationContext(), false);
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

//
//    public static int getPermissionStatus(Activity mActivity, int status, String permission) {
//
//        if (mActivity != null) {
//            if (status == PackageManager.PERMISSION_GRANTED) {
//                return 0;
//            } else if (status == PackageManager.PERMISSION_DENIED) {
//// Should we show an explanation?
//                if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, permission)) {
////Show permission explanation dialog...
//                    return -1;
//                } else {
////Never ask again selected, or device policy prohibits the app from having that permission.
////So, disable that feature, or fall back to another situation...
//
//                    return -2;
//                }
//            }
//        } else if (mActivity != null) {
//            if (ContextCompat.checkSelfPermission(mActivity, permission) == PackageManager.PERMISSION_GRANTED) {
//                return 0;
//            } else {
////Do the stuff that requires permission...
//                return -1;
//            }
//        }
//        return -3;
//    }


    AlertDialog.Builder builder;

    /**
     * Alert Dialog not doing anything in onclick listener
     */
    private void initAlertDialog() {
        if (!isFinishing()) {
            builder = new AlertDialog.Builder(this);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            // Create the AlertDialog object and return it
            builder.create();
        }
    }

    /**
     * Show Progress Dialog
     */
    public void showProgressDialog(String message) {
        if (!isFinishing()) {
            if (mProgressBar == null) {
                initDialog();
            }
            mProgressBar.setMessage(message);
            if (!mProgressBar.isShowing())
                mProgressBar.show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        /*if(mProgressBar!=null){
            if(mProgressBar.isShowing())
                mProgressBar.dismiss();
            mProgressBar = null;
        }*/
    }

    /**
     * hide Progress Dialog
     */
    public void hideDialog() {
        if (!isFinishing()) {
            if (mProgressBar != null && mProgressBar.isShowing()) {
                mProgressBar.dismiss();
            }
        }
    }

    protected void openPermissionSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    public static int getPermissionStatus(Activity mActivity, int status, String permission) {

        if (mActivity != null) {
            if (status == PackageManager.PERMISSION_GRANTED) {
                return 0;
            } else if (status == PackageManager.PERMISSION_DENIED) {
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, permission)) {
                    //Show permission explanation dialog...
                    return -1;
                } else {
                    //Never ask again selected, or device policy prohibits the app from having that permission.
                    //So, disable that feature, or fall back to another situation...

                    return -2;
                }
            }
        } else if (mActivity != null) {
            if (ContextCompat.checkSelfPermission(mActivity, permission) == PackageManager.PERMISSION_GRANTED) {
                return 0;
            } else {
                //Do the stuff that requires permission...
                return -1;
            }
        }
        return -3;
    }

    public static void showErrorMessageSheet(FragmentManager manager, String title, String message) {
        try {
            BottomSheetErrorDialogFragment bottomSheetFragment = new BottomSheetErrorDialogFragment();
            Bundle bundle = new Bundle();
            bundle.putString(LocalConstants.CONST_TITLE, title);
            bundle.putString(LocalConstants.CONST_MESSAGE, message);
            bottomSheetFragment.setArguments(bundle);
            bottomSheetFragment.show(manager, bottomSheetFragment.getTag());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showErrorMessageSheet(FragmentManager manager, String title, String message, OnDialogClickListener mListener) {
        BSLogoutDialogFragment bottomSheetFragment = new BSLogoutDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(LocalConstants.CONST_TITLE, title);
        bundle.putString(LocalConstants.CONST_MESSAGE, message);
        bundle.putSerializable(LocalConstants.CONST_LISTENER, mListener);
        bottomSheetFragment.setArguments(bundle);
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
    }

    public static void showDatePicker(Context context, ItemClickListener listener) {
//        Calendar currentDate = Calendar.getInstance();
//        int year = currentDate.get(Calendar.YEAR);
//        int month = currentDate.get(Calendar.MONTH);
//        int dayOfMonth = currentDate.get(Calendar.DAY_OF_MONTH);
//        new SupportedDatePickerDialog(context, new SupportedDatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
//                // Date date = new Date(day+" "+month+" "+year);
//                Calendar c1 = Calendar.getInstance();
//
//                // set Month
//                // MONTH starts with 0 i.e. ( 0 - Jan)
//                c1.set(Calendar.MONTH, month);
//
//                // set Date
//                c1.set(Calendar.DATE, day);
//
//                // set Year
//                c1.set(Calendar.YEAR, year);
//
//                // creating a date object with specified time.
//                /*Date dateOne = c1.getTime();
//                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
//                view.setText(sdf.format(dateOne));*/
//                listener.onItemClickListener(c1);
//            }
//        },currentDate).show();

    }

    protected void getLastLocationNewMethod() {
        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // GPS location can be null if GPS is switched off
                        if (location != null) {
                            //getAddress(location);
                            AccPref.setLat(getApplicationContext(), String.valueOf(location.getLatitude()));
                            AccPref.setLong(getApplicationContext(), String.valueOf(location.getLongitude()));
                            //addMarker(location.getLatitude(), location.getLongitude(), direc, "");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MapDemoActivity", "Error trying to get last GPS location");
                        e.printStackTrace();
                    }
                });
    }
}
