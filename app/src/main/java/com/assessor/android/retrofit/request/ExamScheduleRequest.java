package com.assessor.android.retrofit.request;

public class ExamScheduleRequest {
    int student_id;

    public ExamScheduleRequest(int student_id) {
        this.student_id = student_id;
    }

    public int getStudent_id() {
        return student_id;
    }

    public void setStudent_id(int student_id) {
        this.student_id = student_id;
    }
}
