package com.assessor.android.model;

public class QuestionInfo {

    String id;
    String question;
    String question_image_url;
    String tc_name;
    String[] options;

    public QuestionInfo(String id, String question, String question_image_url, String tc_name, String[] options) {
        this.id = id;
        this.question = question;
        this.question_image_url = question_image_url;
        this.tc_name = tc_name;
        this.options = options;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getQuestion_image_url() {
        return question_image_url;
    }

    public void setQuestion_image_url(String question_image_url) {
        this.question_image_url = question_image_url;
    }

    public String getTc_name() {
        return tc_name;
    }

    public void setTc_name(String tc_name) {
        this.tc_name = tc_name;
    }

    public String[] getOptions() {
        return options;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }
}
