package com.assessor.android.retrofit.request;

import com.assessor.android.model.QuestionAnswerModel;

import java.util.List;

public class AnswerResponse {
    int userId;
    int exam_id;

    public int getExam_id() {
        return exam_id;
    }

    public void setExam_id(int exam_id) {
        this.exam_id = exam_id;
    }

    List<QuestionAnswerModel> answerList;

    public AnswerResponse(int userId, int exam_id, List<QuestionAnswerModel> answerList) {
        this.userId = userId;
        this.exam_id = exam_id;
        this.answerList = answerList;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<QuestionAnswerModel> getAnswerList() {
        return answerList;
    }

    public void setAnswerList(List<QuestionAnswerModel> answerList) {
        this.answerList = answerList;
    }
}
