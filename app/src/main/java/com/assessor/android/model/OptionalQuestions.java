package com.assessor.android.model;

public class OptionalQuestions {
    private String question;

    private String[] options;

    private String question_image_url;

    private String id;

    private String tc_name;

    private long startTime;
    private long endTime;
    private String haltTime;
    String answer;
    private long durationinMiles;
    public void setDurationinMiles(long durationinMiles) {
        this.durationinMiles = durationinMiles;
    }

    public long getDurationinMiles() {
        return durationinMiles;
    }
    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getHaltTime() {
        return haltTime;
    }

    public void setHaltTime(String haltTime) {
        this.haltTime = haltTime;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAnswer() {
        return answer;
    }

    public String getQuestion() {
        return question;
    }

    public String[] getOptions() {
        return options;
    }

    public void setOptions(String[] options) {
        this.options = options;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTc_name() {
        return tc_name;
    }

    public void setTc_name(String tc_name) {
        this.tc_name = tc_name;
    }

    int answerPosition;

    public void setAnswerPosition(int pos) {
        this.answerPosition = pos;
    }

    public int getAnswerPosition() {
        return answerPosition;
    }
}
