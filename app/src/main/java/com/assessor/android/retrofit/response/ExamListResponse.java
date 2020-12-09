package com.assessor.android.retrofit.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class ExamListResponse {

    @SerializedName("current-datetime")
    @Expose
    String currentDate;

    @SerializedName("student_exam_list")
    @Expose
    List<ExamScheduleModel> list;

    @SerializedName("exam-status")
    @Expose
    Map<String, String> examStatus;

    public ExamListResponse(String currentDate, List<ExamScheduleModel> list, Map<String, String> examStatus) {
        this.currentDate = currentDate;
        this.list = list;
        this.examStatus = examStatus;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public List<ExamScheduleModel> getList() {
        return list;
    }

    public void setList(List<ExamScheduleModel> list) {
        this.list = list;
    }

    public Map<String, String> getExamStatus() {
        return examStatus;
    }

    public void setExamStatus(Map<String, String> examStatus) {
        this.examStatus = examStatus;
    }
}
