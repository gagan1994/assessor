package com.assessor.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.assessor.android.R;
import com.assessor.android.activity.model.BatchInfo;
import com.assessor.android.iface.ItemClickListener;

import java.util.List;

public class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.MyViewHolder> {

    private List<BatchInfo> mBatchList;
    Context mContext;
    ItemClickListener mListener;

    public StudentListAdapter(Context context, List<BatchInfo> vList, ItemClickListener mDocUploadListener) {
        this.mBatchList = vList;
        this.mContext = context;
        this.mListener = mDocUploadListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stud_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final BatchInfo batchInfo = mBatchList.get(position);
        holder.action_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClickListener("IMAGE_CLICK");
            }
        });
       /*final BatchInfo batchInfo = mBatchList.get(position);
       holder.txt_ssc.setText(batchInfo.getSsc());
       holder.txt_batchno.setText(batchInfo.getBatchno());
       holder.txt_date.setText(batchInfo.getDate());
       holder.txt_accessor.setText(batchInfo.getAccessor());
       holder.txt_tcname.setText(batchInfo.getTeacherName());
       holder.container.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(mListener!=null){
                   mListener.onItemClickListener(batchInfo);
               }
           }
       });*/

    }

    @Override
    public int getItemCount() {
        return mBatchList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView action_upload, txt_date, txt_batchno, txt_tcname, txt_accessor;
        RelativeLayout container;

        public MyViewHolder(View view) {
            super(view);
            action_upload = view.findViewById(R.id.action_upload);
            /*txt_date = (TextView) view.findViewById(R.id.txt_date);
            txt_batchno = (TextView) view.findViewById(R.id.txt_batchno);
            txt_tcname = (TextView) view.findViewById(R.id.txt_tcname);
            txt_accessor = (TextView) view.findViewById(R.id.txt_accessor);
            container = view.findViewById(R.id.container);*/

        }
    }
}
