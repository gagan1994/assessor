package com.assessor.android.retrofit;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.assessor.android.retrofit.request.AnswerResponse;
import com.assessor.android.retrofit.request.ExamScheduleRequest;
import com.assessor.android.retrofit.request.QuestionListRequest;
import com.assessor.android.retrofit.response.ExamListResponse;
import com.assessor.android.retrofit.response.ExamScheduleResponse;
import com.assessor.android.retrofit.response.LoginResponse;
import com.assessor.android.retrofit.response.QuestionsListResponse;
import com.assessor.android.retrofit.response.StringResponse;
import com.assessor.android.retrofit.response.UserResponse;
import com.google.gson.internal.LinkedTreeMap;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetrofitService {

    String sessionId;
    private static RetrofitService ourInstance;
    ApiService mApi;
    private Context mcontext;

    public static RetrofitService getInstance(Context mContext) {
        if (ourInstance == null) {
            ourInstance = new RetrofitService(mContext);
        }
        return ourInstance;
    }

    private RetrofitService(Context context) {
        //Creating an object of our api interface
        mcontext = context;
        mApi = RetroClient.getApiService(context);
    }

    public void checkLogin(String userName, String password, String aadhar,
                           final RetrofitServiceListener mListener) {
        final LoginResponse info = new LoginResponse();
        if (TextUtils.isEmpty(userName)) {
            mListener.onFailure(info, null);
            return;
        }
        mListener.onRequestStarted(null);
        Call<LoginResponse> call = mApi.checkLogin(userName, password, aadhar);
        /**
         * Enqueue Callback will be call when get response...
         */
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                if (response.isSuccessful()) {
                    /**
                     * Got Successfully
                     */
                    LoginResponse userInfo = response.body();
                    mListener.onResponse(userInfo);
                } else {
                    mListener.onFailure(null, null);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                mListener.onFailure(info, t);
            }
        });
    }

    public void getAuthUser(final RetrofitServiceListener mListener) {
        final UserResponse info = new UserResponse();

        mListener.onRequestStarted(null);
        Call<UserResponse> call = mApi.getUserProfile();
        /**
         * Enqueue Callback will be call when get response...
         */
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {

                if (response.isSuccessful()) {
                    /**
                     * Got Successfully
                     */
                    UserResponse userInfo = response.body();
                    mListener.onResponse(userInfo);
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                mListener.onFailure(info, t);
            }
        });
    }

    public void onLogOut(final RetrofitServiceListener mListener) {
        final StringResponse info = new StringResponse();

        mListener.onRequestStarted(null);
        Call<StringResponse> call = mApi.onLogout();
        /**
         * Enqueue Callback will be call when get response...
         */
        call.enqueue(new Callback<StringResponse>() {
            @Override
            public void onResponse(Call<StringResponse> call, Response<StringResponse> response) {

                if (response.isSuccessful()) {
                    /**
                     * Got Successfully
                     */
                    StringResponse message = response.body();
                    mListener.onResponse(message);
                }
            }

            @Override
            public void onFailure(Call<StringResponse> call, Throwable t) {
                mListener.onFailure(info, t);
            }
        });
    }

    public void getExamSchedule(ExamScheduleRequest request, final RetrofitServiceListener mListener) {
        //request.setStudent_id(15);
        final ExamScheduleResponse info = new ExamScheduleResponse();
        mApi = RetroClient.getApiServiceAPI(mcontext);
        mListener.onRequestStarted(null);
        Call<ExamListResponse> call = mApi.getExamSchedule(request);
        /**
         * Enqueue Callback will be call when get response...
         */
        call.enqueue(new Callback<ExamListResponse>() {
            @Override
            public void onResponse(Call<ExamListResponse> call, Response<ExamListResponse> response) {

                if (response.isSuccessful()) {
                    /**
                     * Got Successfully
                     */
                    ExamListResponse examScheduleModelList = response.body();
                    // ExamListResponse responseModel = new ExamScheduleResponse(examScheduleModelList);
                    mListener.onResponse(examScheduleModelList);
                }
            }

            @Override
            public void onFailure(Call<ExamListResponse> call, Throwable t) {
                mListener.onFailure(info, t);
            }
        });
    }

    public void getQuestionList(QuestionListRequest request, final RetrofitServiceListener mListener) {
        final ExamScheduleResponse info = new ExamScheduleResponse();
        mApi = RetroClient.getApiServiceAPI(mcontext);
        mListener.onRequestStarted(null);
        Call<QuestionsListResponse> call = mApi.getExamQuestion(request);
        /**
         * Enqueue Callback will be call when get response...
         */
        call.enqueue(new Callback<QuestionsListResponse>() {
            @Override
            public void onResponse(Call<QuestionsListResponse> call, Response<QuestionsListResponse> response) {

                if (response.isSuccessful()) {
                    /**
                     * Got Successfully
                     */
                    QuestionsListResponse questions = response.body();
                    mListener.onResponse(questions);
                }
            }

            @Override
            public void onFailure(Call<QuestionsListResponse> call, Throwable t) {
                mListener.onFailure(info, t);
                getQuestionString(request, mListener);
            }
        });
    }

    public void getQuestionString(QuestionListRequest request, final RetrofitServiceListener mListener) {
        final ExamScheduleResponse info = new ExamScheduleResponse();
        mApi = RetroClient.getApiServiceAPI(mcontext);
        mListener.onRequestStarted(null);
        Call<Object> call = mApi.getExamQuestionString(request);
        /**
         * Enqueue Callback will be call when get response...
         */
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {

                if (response.isSuccessful()) {
                    /**
                     * Got Successfully
                     */
                    Object questions = response.body();

                    mListener.onResponse(questions);
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                mListener.onFailure(info, t);
            }
        });
    }

    public void submitAnswerList(AnswerResponse request, final RetrofitServiceListener mListener) {
        final Object info = new Object();
        mApi = RetroClient.getApiServiceAPI(mcontext);
        mListener.onRequestStarted(null);
        Call<LinkedTreeMap<String, String>> call = mApi.submitAnswerList(request);
        /**
         * Enqueue Callback will be call when get response...
         */
        call.enqueue(new Callback<LinkedTreeMap<String, String>>() {
            @Override
            public void onResponse(Call<LinkedTreeMap<String, String>> call, Response<LinkedTreeMap<String, String>> response) {

                if (response.isSuccessful()) {
                    /**
                     * Got Successfully
                     */
                    LinkedTreeMap<String, String> questions = response.body();
                    if (questions.containsKey("message")) {
                        String ansResponse = questions.get("message");
                        mListener.onResponse(ansResponse);
                    } else {
                        mListener.onFailure(info, null);
                    }
                }else {
                    mListener.onFailure(info, null);
                }
            }

            @Override
            public void onFailure(Call<LinkedTreeMap<String, String>> call, Throwable t) {
                mListener.onFailure(info, t);
            }
        });
    }

    public void updateFile(File file, int studentId, int examId, double lat, double longitude, final RetrofitServiceListener mListener) {
        final Object info = new Object();

        mApi = RetroClient.getApiServiceAPI(mcontext);
        mListener.onRequestStarted(null);
        MediaType MEDIA_TYPE = MediaType.parse("video/mp4");

        //File file = new File("/storage/emulated/0/Pictures/MyApp/test.png");
        RequestBody fileBody = RequestBody.create(MEDIA_TYPE, file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), fileBody);
        Call<Object> call = mApi.uploadFile(fileBody, studentId, examId, lat, longitude);
        /**
         * Enqueue Callback will be call when get response...
         */
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {

                if (response.isSuccessful()) {
                    /**
                     * Got Successfully
                     */
                    Object res = response.body();
                    mListener.onResponse(res);
//                    if(questions.containsKey("message")) {
//                        String ansResponse = questions.get("message");
//                        mListener.onResponse(ansResponse);
//                    }else{
//                        mListener.onFailure(info,null);
//                    }
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                mListener.onFailure(info, t);
            }
        });
    }


//    public void upload(String path){
//        Uri fileUri = ... // from a file chooser or a camera intent
//
//// create upload service client
//        /*ApiService service =
//                ApiService.createService(ApiService.class);*/
//
//// create part for file (photo, video, ...)
//        MultipartBody.Part body = prepareFilePart("photo", fileUri);
//
//// create a map of data to pass along
//        RequestBody description = createPartFromString("hello, this is description speaking");
//        RequestBody place = createPartFromString("Magdeburg");
//        RequestBody time = createPartFromString("2016");
//
//        HashMap<String, RequestBody> map = new HashMap<>();
//        map.put("description", description);
//        map.put("place", place);
//        map.put("time", time);
//
//// finally, execute the request
//        Call<ResponseBody> call = mApi.uploadFileWithPartMap(map, body);
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//            }
//        });
//    }

    private RequestBody createPartFromString(String value) {
        return RequestBody.create(MediaType.parse("multipart/form-data"), value);
    }

//    @NonNull
//    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
//        // https://github.com/iPaulPro/aFileChooser/blob/master/aFileChooser/src/com/ipaulpro/afilechooser/utils/FileUtils.java
//        // use the FileUtils to get the actual file by uri
//        File file = FileUtils.getFile(this, fileUri);
//
//        // create RequestBody instance from file
//        RequestBody requestFile =
//                RequestBody.create(
//                        MediaType.parse(mcontext.getContentResolver().getType(fileUri)),
//                        file
//                );
//
//        // MultipartBody.Part is used to send also the actual file name
//        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
//    }


    public void uploadFile(File file, int sid, int eid, double lat, double lon, RetrofitServiceListener listener) {
        // Map is used to multipart the file using okhttp3.RequestBody
        //File file = new File(mediaPath);

        if (listener != null) {
            listener.onRequestStarted(null);
        }
        // Parsing any Media type file
        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("video_file", file.getName(), requestBody);
        //RequestBody studentId = RequestBody.create(MediaType.parse("text/plain"), "12");
        RequestBody studentId = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(sid));
        RequestBody examId = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(eid));
        RequestBody latitude = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(lat));
        RequestBody longitude = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(lon));
        //  RequestBody longitude = RequestBody.create(MediaType.parse("text/plain"), "11");

        mApi = RetroClient.getApiServiceAPI(mcontext);
        //ApiConfig getResponse = AppConfig.getRetrofit().create(ApiConfig.class);
        Call<LinkedTreeMap<String, String>> call = mApi.uploadVideoFile(fileToUpload, studentId, examId, latitude, longitude);
        call.enqueue(new Callback<LinkedTreeMap<String, String>>() {
            @Override
            public void onResponse(Call<LinkedTreeMap<String, String>> call, Response<LinkedTreeMap<String, String>> response) {
                Object serverResponse = response.body();
                if (response.code() == 409) {
                    listener.onFailure("Video already Uploaded...", null);
                } else if (serverResponse != null) {
                    if (response.code() == 409) {
                        listener.onFailure("Video already Uploaded...", null);
                    } else {
                        //SUCCESS
                        listener.onResponse("Video already Uploaded...");
                    }
                    /*if (serverResponse.getSuccess()) {
                        Toast.makeText(getApplicationContext(), serverResponse.getMessage(),Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), serverResponse.getMessage(),Toast.LENGTH_SHORT).show();
                    }*/
                } else {
                    listener.onFailure("Something went wrong, Please try again later", null);
                    //assert serverResponse != null;
                    //Log.v("Response", serverResponse.toString());
                }

            }

            @Override
            public void onFailure(Call<LinkedTreeMap<String, String>> call, Throwable t) {
                Log.d("TAG", "ad");
                listener.onFailure(t.getLocalizedMessage(), null);
            }
        });
    }



    public void uploadImageFile(File file, int sid, int eid, String capturedTime, RetrofitServiceListener listener) {
        // Map is used to multipart the file using okhttp3.RequestBody
        //File file = new File(mediaPath);


        // Parsing any Media type file
        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("exam_id", file.getName(), requestBody);
        //RequestBody studentId = RequestBody.create(MediaType.parse("text/plain"), "12");
        RequestBody studentId = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(sid));
        RequestBody examId = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(eid));
        RequestBody capTime = RequestBody.create(MediaType.parse("multipart/form-data"), capturedTime);

        //  RequestBody longitude = RequestBody.create(MediaType.parse("text/plain"), "11");

        mApi = RetroClient.getApiServiceAPI(mcontext);
        //ApiConfig getResponse = AppConfig.getRetrofit().create(ApiConfig.class);
        Call<Void> call = mApi.uploadImageFile(fileToUpload, studentId, examId,capTime);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Object serverResponse = response.body();
                /*if (response.code() == 409) {
                    listener.onFailure("File already Uploaded...", null);
                } else if (serverResponse != null) {
                    if (response.code() == 409) {
                        listener.onFailure("File already Uploaded...", null);
                    } else {
                        //SUCCESS
                        listener.onResponse("File already Uploaded...");
                    }
                    *//*if (serverResponse.getSuccess()) {
                        Toast.makeText(getApplicationContext(), serverResponse.getMessage(),Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), serverResponse.getMessage(),Toast.LENGTH_SHORT).show();
                    }*//*
                } else {
                    listener.onFailure("Something went wrong, Please try again later", null);
                    //assert serverResponse != null;
                    //Log.v("Response", serverResponse.toString());
                }*/

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("TAG", "ad");
                listener.onFailure(t.getLocalizedMessage(), null);
            }
        });
    }


}
