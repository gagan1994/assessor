package com.assessor.android.model;

import java.util.List;

public class QuestionModel {

    int qid;
    String question;
    List<AnswerModel> answers;

    public QuestionModel(int qid, String question, List<AnswerModel> answers) {
        this.qid = qid;
        this.question = question;
        this.answers = answers;
    }

    public int getQid() {
        return qid;
    }

    public void setQid(int qid) {
        this.qid = qid;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<AnswerModel> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerModel> answers) {
        this.answers = answers;
    }
}
