package com.assessor.android.activity.model;

public class BatchInfo {

    int id;

    String batchno;

    String ssc;

    String date;

    String teacherName;

    String accessor;

    public BatchInfo(int id, String batchno, String ssc, String date, String teacherName, String accessor) {
        this.id = id;
        this.batchno = batchno;
        this.ssc = ssc;
        this.date = date;
        this.teacherName = teacherName;
        this.accessor = accessor;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBatchno() {
        return batchno;
    }

    public void setBatchno(String batchno) {
        this.batchno = batchno;
    }

    public String getSsc() {
        return ssc;
    }

    public void setSsc(String ssc) {
        this.ssc = ssc;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getAccessor() {
        return accessor;
    }

    public void setAccessor(String accessor) {
        this.accessor = accessor;
    }
}
