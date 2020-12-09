package com.assessor.android.model;

public class AnswerModel {

    int anwserId;
    int answerType;
    String answer;

    public AnswerModel(int anwserId, int answerType, String answer) {
        this.anwserId = anwserId;
        this.answerType = answerType;
        this.answer = answer;
    }

    public int getAnwserId() {
        return anwserId;
    }

    public void setAnwserId(int anwserId) {
        this.anwserId = anwserId;
    }

    public int getAnswerType() {
        return answerType;
    }

    public void setAnswerType(int answerType) {
        this.answerType = answerType;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
