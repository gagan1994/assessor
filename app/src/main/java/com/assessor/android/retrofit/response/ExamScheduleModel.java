package com.assessor.android.retrofit.response;

public class ExamScheduleModel {

    int id;
    String exam_name;
    String exam_date;
    String exam_time;
    String exam_duration;
    String batch_name;
    String set_name;
    String ssc;
    boolean isEnroll;
    String currentDateTime;

    public ExamScheduleModel(int id, String exam_name, String exam_date, String exam_time, String exam_duration, String batch_name, String set_name) {
        this.id = id;
        this.exam_name = exam_name;
        this.exam_date = exam_date;
        this.exam_time = exam_time;
        this.exam_duration = exam_duration;
        this.batch_name = batch_name;
        this.set_name = set_name;
    }

    public String getSsc() {
        return ssc;
    }

    public void setSsc(String ssc) {
        this.ssc = ssc;
    }

    public String getCurrentDateTime() {
        return currentDateTime;
    }

    public void setCurrentDateTime(String currentDateTime) {
        this.currentDateTime = currentDateTime;
    }

    public boolean isEnroll() {
        return isEnroll;
    }

    public void setEnroll(boolean enroll) {
        isEnroll = enroll;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getExam_name() {
        return exam_name;
    }

    public void setExam_name(String exam_name) {
        this.exam_name = exam_name;
    }

    public String getExam_date() {
        return exam_date;
    }

    public void setExam_date(String exam_date) {
        this.exam_date = exam_date;
    }

    public String getExam_time() {
        return exam_time;
    }

    public void setExam_time(String exam_time) {
        this.exam_time = exam_time;
    }

    public String getExam_duration() {
        return exam_duration;
    }

    public void setExam_duration(String exam_duration) {
        this.exam_duration = exam_duration;
    }

    public String getBatch_name() {
        return batch_name;
    }

    public void setBatch_name(String batch_name) {
        this.batch_name = batch_name;
    }

    public String getSet_name() {
        return set_name;
    }

    public void setSet_name(String set_name) {
        this.set_name = set_name;
    }
}
