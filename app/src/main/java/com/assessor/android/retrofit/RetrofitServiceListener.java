package com.assessor.android.retrofit;

public interface RetrofitServiceListener {

    void onRequestStarted(Object mObject);

    void onResponse(Object mObject);

    void onFailure(Object mObject, Throwable t);

}
