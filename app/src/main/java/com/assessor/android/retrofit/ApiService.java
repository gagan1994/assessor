package com.assessor.android.retrofit;


import com.assessor.android.retrofit.request.AnswerResponse;
import com.assessor.android.retrofit.request.ExamScheduleRequest;
import com.assessor.android.retrofit.request.QuestionListRequest;
import com.assessor.android.retrofit.response.ExamListResponse;
import com.assessor.android.retrofit.response.LoginResponse;
import com.assessor.android.retrofit.response.QuestionsListResponse;
import com.assessor.android.retrofit.response.StringResponse;
import com.assessor.android.retrofit.response.UserResponse;
import com.google.gson.internal.LinkedTreeMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {

    @FormUrlEncoded
    @POST("/api/auth/login")
    Call<LoginResponse> checkLogin(@Field("email") String email, @Field("password") String password, @Field("aadhar_no") String aadhar_no);

    @POST("/api/auth/me")
    Call<UserResponse> getUserProfile();

    @POST("/api/auth/logout")
    Call<StringResponse> onLogout();

    @POST("/api/auth/get-student-details")
    Call<ExamListResponse> getExamSchedule(@Body ExamScheduleRequest request);

    @POST("/api/auth/get-exam-questions")
    Call<QuestionsListResponse> getExamQuestion(@Body QuestionListRequest request);

    @POST("api/auth/get-exam-questions")
    Call<Object> getExamQuestionString(@Body QuestionListRequest request);

    @POST("api/auth/get-student-answers")
    Call<LinkedTreeMap<String, String>> submitAnswerList(@Body AnswerResponse request);

    @Multipart
    @Headers({
            "Accept: multipart/form-data",
    })
    @POST("api/auth/upload-short-video")
    Call<Object> uploadFile(@Part("video_file") RequestBody requestBody, @Part("student_id") int studentId, @Part("exam_id") int exam_id, @Part("lat") double lat, @Part("long") double longitude);

    @Multipart
    @POST("api/auth/upload-short-video")
    Call<LinkedTreeMap<String, String>> uploadVideoFile(@Part MultipartBody.Part image,
                                                        @Part("student_id") RequestBody student_id,
                                                        @Part("exam_id") RequestBody exam_id,
                                                        @Part("lat") RequestBody lat,
                                                        @Part("long") RequestBody longitude
    );


    @Multipart
    @POST("api/auth/upload-spy-student-image")
    Call<Void> uploadImageFile(@Part MultipartBody.Part image,
                                                        @Part("student_id") RequestBody student_id,
                                                        @Part("exam_id") RequestBody exam_id,
                                                        @Part("cap_time") RequestBody capturedTIme
    );

}
