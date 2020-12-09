package com.assessor.android.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.assessor.android.BaseActivity;
import com.assessor.android.BuildConfig;
import com.assessor.android.R;
import com.assessor.android.retrofit.RetrofitService;
import com.assessor.android.retrofit.RetrofitServiceListener;
import com.assessor.android.retrofit.response.LoginResponse;
import com.assessor.android.retrofit.response.UserResponse;
import com.assessor.android.utility.AccPref;

public class LoginActivity extends BaseActivity implements RetrofitServiceListener {

    EditText user;
    EditText pass;
    EditText aadhar;
    RetrofitServiceListener mServiceListener;
/*
    android:text="demo2@gmail.com"
    android:text="123456"*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mServiceListener = this;
        pass = findViewById(R.id.pass);
        user = findViewById(R.id.user);
        aadhar = findViewById(R.id.aadhar);
//        user.setText("demo2@gmail.com");
//        pass.setText("123456");
        if (BuildConfig.DEBUG) {
            user.setText("spandey@gmail.com");
            pass.setText("12345678");
            aadhar.setText("333344445555");
        }

        pass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    //Log.i(TAG,"Enter pressed");
                    if (isValidForm()) {
                        RetrofitService.getInstance(getApplicationContext()).checkLogin(user.getText().toString(), pass.getText().toString(), aadhar.getText().toString(), mServiceListener);
                    }
                }
                return false;
            }
        });
        findViewById(R.id.action_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidForm()) {
                    RetrofitService.getInstance(getApplicationContext()).checkLogin(user.getText().toString(), pass.getText().toString(), aadhar.getText().toString(), mServiceListener);
                }
            }
        });

    }

    private boolean isValidForm() {
        if (TextUtils.isEmpty(user.getText().toString())) {
            showErrorMessageSheet(getSupportFragmentManager(), "Alert", "Please Enter User Name");
            user.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(pass.getText().toString())) {
            showErrorMessageSheet(getSupportFragmentManager(), "Alert", "Please Enter Password");
            pass.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(aadhar.getText().toString())) {
            showErrorMessageSheet(getSupportFragmentManager(), "Alert", "Please Enter Aadhar Card Number");
            aadhar.requestFocus();
            return false;
        }
        return true;
    }

    public void autoLogin() {
        RetrofitService.getInstance(getApplicationContext()).getAuthUser(mServiceListener);
    }

    @Override
    public void onRequestStarted(Object mObject) {
        showProgressDialog("");
    }

    @Override
    public void onResponse(Object mObject) {
        if (mObject instanceof LoginResponse) {
            LoginResponse mResponse = (LoginResponse) mObject;
            AccPref.setAccessToken(LoginActivity.this, mResponse.getAccess_token());
            RetrofitService.getInstance(getApplicationContext()).getAuthUser(mServiceListener);
        } else if (mObject instanceof UserResponse) {
            UserResponse mUserInfo = (UserResponse) mObject;
            AccPref.setUserId(getApplicationContext(), mUserInfo.getId());
            AccPref.setEmail(getApplicationContext(), mUserInfo.getEmail());
            AccPref.setFirstName(getApplicationContext(), mUserInfo.getFirst_name());
            AccPref.setLastName(getApplicationContext(), mUserInfo.getLast_login());
            AccPref.setMobile(getApplicationContext(), mUserInfo.getMobile_no());
            navigateDashboard();
            hideDialog();
        }

    }

    @Override
    public void onFailure(Object mObject, Throwable t) {
        hideDialog();
        showErrorAlert("Alert", "Invalid User Name and Password.");
    }
}
