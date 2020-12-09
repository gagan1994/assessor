package com.assessor.android.model;

import android.text.TextUtils;

public class QuestionAnswerModel {

    String qid;
    int answerId;
    String textAnswer;
    String haltTime;

    public String getHaltTime() {
        return haltTime;
    }

    public void setHaltTime(String haltTime) {
        this.haltTime = haltTime;
    }

    public QuestionAnswerModel(String qid, int answerId, String textAnswer,String time) {
        this.qid = qid;
        this.answerId = answerId;
        this.haltTime = time;
        if (TextUtils.isEmpty(textAnswer)) {
            this.textAnswer = "";
        } else {
            this.textAnswer = textAnswer;
        }
    }

    public int getAnswerId() {
        return answerId;
    }

    public void setAnswerId(int answerId) {
        this.answerId = answerId;
    }

    public String getQid() {
        return qid;
    }

    public void setQid(String qid) {
        this.qid = qid;
    }

    public String getTextAnswer() {
        return textAnswer;
    }

    public void setTextAnswer(String textAnswer) {
        this.textAnswer = textAnswer;
    }
}
