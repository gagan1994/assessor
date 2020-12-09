package com.assessor.android.retrofit;

import android.content.Context;
import android.text.TextUtils;

import com.assessor.android.utility.AccPref;

import java.io.IOException;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroClient {

    //private static final String ROOT_URL = "http://www.ranganedu.in/";
    private static final String ROOT_URL = "http://jemengg.com/";


    private static Retrofit getRetrofitInstance(final Context context) {


        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(40, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        TimeZone tz = TimeZone.getDefault();
                        String token = AccPref.getAccessToken(context);
                        Request request = null;
                        if (TextUtils.isEmpty(token)) {
                            request = chain.request().newBuilder()
                                    .addHeader("tz_id", tz.getID()).addHeader("tz_name", tz.getDisplayName(false, TimeZone.SHORT)).build();
                        } else {
                            request = chain.request().newBuilder()
                                    .addHeader("Authorization", "Bearer " + token)
                                    .addHeader("tz_id", tz.getID()).addHeader("tz_name", tz.getDisplayName(false, TimeZone.SHORT)).build();
                        }

                        return chain.proceed(request);
                    }
                })
                .build();

        return new Retrofit.Builder()
                .baseUrl(ROOT_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }


    private static Retrofit getRetrofitInstanceAPI(final Context context) {


        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(40, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        TimeZone tz = TimeZone.getDefault();
                        String token = AccPref.getAccessToken(context);
                        Request request = null;
                        if (TextUtils.isEmpty(token)) {
                            request = chain.request().newBuilder()
                                    .addHeader("tz_id", tz.getID()).addHeader("tz_name", tz.getDisplayName(false, TimeZone.SHORT)).build();
                        } else {
                            request = chain.request().newBuilder()
                                    .addHeader("Authorization", "Bearer " + token)
                                    .addHeader("tz_id", tz.getID()).addHeader("tz_name", tz.getDisplayName(false, TimeZone.SHORT)).build();
                        }

                        return chain.proceed(request);
                    }
                })
                .build();

        return new Retrofit.Builder()
                .baseUrl(ROOT_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }


    /**
     * Get API Service
     *
     * @return API Service
     */
    public static ApiService getApiService(Context context) {
        return getRetrofitInstance(context).create(ApiService.class);
    }

    /**
     * Get API Service
     *
     * @return API Service
     */
    public static ApiService getApiServiceAPI(Context context) {
        return getRetrofitInstanceAPI(context).create(ApiService.class);
    }


}
