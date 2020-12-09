package com.assessor.android.retrofit.request;

public class QuestionListRequest {
    int exam_id;

    public QuestionListRequest(int exam_id) {
        this.exam_id = exam_id;
    }

    public int getExam_id() {
        return exam_id;
    }

    public void setExam_id(int exam_id) {
        this.exam_id = exam_id;
    }
}
