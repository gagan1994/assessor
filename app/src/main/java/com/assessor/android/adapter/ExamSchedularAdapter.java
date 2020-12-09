package com.assessor.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.assessor.android.BuildConfig;
import com.assessor.android.R;
import com.assessor.android.iface.ItemClickListener;
import com.assessor.android.retrofit.response.ExamScheduleModel;
import com.assessor.android.utility.AccPref;
import com.assessor.android.utility.Utility;

import java.util.Date;
import java.util.List;

import cn.iwgang.countdownview.CountdownView;

public class ExamSchedularAdapter extends RecyclerView.Adapter<ExamSchedularAdapter.MyViewHolder> {

    private List<ExamScheduleModel> mScheduleList;
    Context mContext;
    ItemClickListener mListener;

    public ExamSchedularAdapter(Context context, List<ExamScheduleModel> vList, ItemClickListener itemClickListener) {
        this.mScheduleList = vList;
        this.mContext = context;
        this.mListener = itemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.batch_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        final ExamScheduleModel scheduleModel = mScheduleList.get(position);
        holder.txt_ssc.setText("" + scheduleModel.getSsc());
        holder.txt_batchno.setText(scheduleModel.getBatch_name());
        Date startDate = Utility.getDateTime(scheduleModel.getExam_date() + " " + scheduleModel.getExam_time());

        holder.txt_date.setText(Utility.formateDateTime(startDate));
        holder.txt_evendate.setText(scheduleModel.getExam_name());
        holder.txt_tcname.setText(scheduleModel.getSet_name());
        holder.txt_duration.setText(scheduleModel.getExam_duration());
        Date currentDate = Utility.getCurrentDateTime(scheduleModel.getCurrentDateTime());
        if (currentDate == null) {
            currentDate = new Date();
        }

        Date endDate = Utility.getEndDateTime(scheduleModel.getExam_date() + " " + scheduleModel.getExam_time(), scheduleModel.getExam_duration());

        if (scheduleModel.isEnroll()) {
            holder.action_start.setText("APPEARED");
        }else if (AccPref.isExamCompleted(mContext, scheduleModel.getId())) {
            holder.cv_countdownView.setVisibility(View.GONE);
                holder.action_start.setText("FINISHED");
        }else if (BuildConfig.DEBUG || currentDate.after(startDate) && currentDate.before(endDate)) {
            //LIVE
            holder.action_start.setBackgroundResource(R.color.live);
            holder.cv_countdownView.setVisibility(View.GONE);
            //event.setStatus(ACTION_LIVE);
            holder.action_start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onItemClickListener(scheduleModel);
                    }
                }
            });
        } else if (currentDate.after(endDate)) {
            holder.cv_countdownView.setVisibility(View.GONE);
            //event.setStatus(ACTION_COMPLETED);
            if (scheduleModel.isEnroll()) {
                holder.action_start.setText("FINISHED");
            } else {
                holder.action_start.setText("MISSED");
            }
            holder.action_start.setBackgroundResource(R.color.finished);
        } else if (currentDate.before(startDate)) {
            holder.cv_countdownView.setVisibility(View.VISIBLE);
            holder.action_start.setVisibility(View.GONE);
            holder.cv_countdownView.start(Utility.getMilisecondLeft(startDate));
            holder.action_start.setText("START EXAM");
            holder.action_start.setBackgroundResource(R.color.live);

        } else if (!Utility.getDate(currentDate).equalsIgnoreCase(Utility.getDate(endDate))) {
            holder.cv_countdownView.setVisibility(View.GONE);
            //event.setStatus("COMPLETED");
            if (scheduleModel.isEnroll()) {
                holder.action_start.setText("FINISHED");
            } else {
                holder.action_start.setText("MISSED");
            }
            holder.action_start.setBackgroundResource(R.color.finished);
        } else {
            holder.action_start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onItemClickListener(scheduleModel);
                    }
                }
            });
        }
        holder.cv_countdownView.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
            @Override
            public void onEnd(CountdownView cv) {
                holder.action_start.setText("Start Exam");
                holder.action_start.setVisibility(View.VISIBLE);
                holder.cv_countdownView.setVisibility(View.GONE);
                holder.action_start.setBackgroundResource(R.color.live);
                holder.action_start.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            mListener.onItemClickListener(scheduleModel);
                        }
                    }
                });
            }
        });


    }

    @Override
    public int getItemCount() {
        return mScheduleList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_ssc, txt_date, txt_batchno, txt_tcname, action_start, txt_evendate, txt_duration;
        RelativeLayout container;
        CountdownView cv_countdownView;

        public MyViewHolder(View view) {
            super(view);
            txt_ssc = view.findViewById(R.id.txt_ssc);
            txt_date = view.findViewById(R.id.txt_date);
            txt_duration = view.findViewById(R.id.txt_duration);
            txt_batchno = view.findViewById(R.id.txt_batchno);
            txt_tcname = view.findViewById(R.id.txt_tcname);
            action_start = view.findViewById(R.id.action_start);
            txt_evendate = view.findViewById(R.id.txt_evendate);
            container = view.findViewById(R.id.container);
            cv_countdownView = view.findViewById(R.id.cv_countdownView);

        }
    }
}
