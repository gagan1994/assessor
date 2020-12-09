package com.assessor.android.retrofit.response;

import com.assessor.android.model.ImagebasedQuestions;
import com.assessor.android.model.OptionalQuestions;
import com.assessor.android.model.PracticalQuestions;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class QuestionsListResponse {

    @SerializedName("practical")
    @Expose
    ArrayList<PracticalQuestions> practical;

    @SerializedName("image-based")
    @Expose
    ArrayList<ImagebasedQuestions> imagebased;

    @SerializedName("optional")
    @Expose
    ArrayList<OptionalQuestions> optional;

    public QuestionsListResponse(ArrayList<PracticalQuestions> practical, ArrayList<ImagebasedQuestions> imagebased, ArrayList<OptionalQuestions> optional) {
        this.practical = practical;
        this.imagebased = imagebased;
        this.optional = optional;
    }

    public List<PracticalQuestions> getPractical() {
        return practical;
    }

    public void setPractical(ArrayList<PracticalQuestions> practical) {
        this.practical = practical;
    }

    public List<ImagebasedQuestions> getImagebased() {
        return imagebased;
    }

    public void setImagebased(ArrayList<ImagebasedQuestions> imagebased) {
        this.imagebased = imagebased;
    }

    public List<OptionalQuestions> getOptional() {
        return optional;
    }

    public void setOptional(ArrayList<OptionalQuestions> optional) {
        this.optional = optional;
    }
}
