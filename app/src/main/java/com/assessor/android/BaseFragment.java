package com.assessor.android;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.assessor.android.activity.LoginActivity;


public class BaseFragment extends Fragment {
    ProgressDialog mProgressBar = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Inilise Dialog
     */
    private void initDialog() {
        if (isAdded()) {
            mProgressBar = new ProgressDialog(getContext());
            mProgressBar.setCancelable(true);//you can cancel it by pressing back button
        }
    }

    /**
     * Show Error Message
     */
    public void showErrorAlert(String title, String message) {
        if (isAdded()) {
            if (builder == null) {
                initAlertDialog();
            }
            builder.setTitle(title).setMessage(message).show();
        }
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }


    AlertDialog.Builder builder;

    /**
     * Alert Dialog not doing anything in onclick listener
     */
    private void initAlertDialog() {
        if (isAdded()) {
            builder = new AlertDialog.Builder(getContext());
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
        if (isAdded()) {
            if (mProgressBar == null) {

                initDialog();
            }
            mProgressBar.setMessage(message);
            mProgressBar.show();
        }
    }

    /**
     * hide Progress Dialog
     */
    public void hideDialog() {
        if (isAdded()) {
            if (mProgressBar != null && mProgressBar.isShowing()) {
                mProgressBar.dismiss();
            }
        }
    }

    public void navigateDashboard() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }


}
