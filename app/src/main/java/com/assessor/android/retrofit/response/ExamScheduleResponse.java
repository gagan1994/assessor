package com.assessor.android.retrofit.response;

import java.util.List;

public class ExamScheduleResponse {

    List<ExamScheduleModel> examScheduleModelList;

    public ExamScheduleResponse() {
    }

    public ExamScheduleResponse(List<ExamScheduleModel> examScheduleModelList) {
        this.examScheduleModelList = examScheduleModelList;
    }

    public List<ExamScheduleModel> getExamScheduleModelList() {
        return examScheduleModelList;
    }

    public void setExamScheduleModelList(List<ExamScheduleModel> examScheduleModelList) {
        this.examScheduleModelList = examScheduleModelList;
    }
}
