package com.assessor.android.model;

public class ImagebasedQuestions {
    private String question;

    private String[] options;

    private String question_image_url;

    private String id;

    private long startTime;
    private long endTime;
    private String haltTime;
    private long durationinMiles;
    public void setDurationinMiles(long durationinMiles) {
        this.durationinMiles = durationinMiles;
    }



    public void init() {
        this.question = "Test Question";
        this.options = new String[]{"A","B","C","D"};
        this.question_image_url = "";
        this.id = "1";
        this.tc_name = "Teacher Name";
        this.answer = "";
        this.answerPosition = 1;
    }

    public long getDurationinMiles() {
        return durationinMiles;
    }
    private String tc_name;
    private String answer;
    private int answerPosition = 0;

    public void setAnswerPosition(int answerPosition) {
        this.answerPosition = answerPosition;
    }

    public int getAnswerPosition() {
        return answerPosition;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String[] getOptions() {
        return options;
    }

    public void setOptions(String[] options) {
        this.options = options;
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

    @Override
    public String toString() {
        return "ClassPojo [question = " + question + ", options = " + options + ", question_image_url = " + question_image_url + ", id = " + id + ", tc_name = " + tc_name + "]";
    }
}
