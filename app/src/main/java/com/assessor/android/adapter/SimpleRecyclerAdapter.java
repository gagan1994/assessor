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

public class SimpleRecyclerAdapter extends RecyclerView.Adapter<SimpleRecyclerAdapter.MyViewHolder> {

    private List<BatchInfo> mBatchList;
    Context mContext;
    ItemClickListener mListener;

    public SimpleRecyclerAdapter(Context context, List<BatchInfo> vList, ItemClickListener mDocUploadListener) {
        this.mBatchList = vList;
        this.mContext = context;
        this.mListener = mDocUploadListener;
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

        final BatchInfo batchInfo = mBatchList.get(position);
        holder.txt_ssc.setText(batchInfo.getSsc());
        holder.txt_batchno.setText(batchInfo.getBatchno());
        holder.txt_date.setText(batchInfo.getDate());
        holder.txt_tcname.setText(batchInfo.getTeacherName());
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClickListener(batchInfo);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mBatchList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_ssc, txt_date, txt_batchno, txt_tcname;
        RelativeLayout container;

        public MyViewHolder(View view) {
            super(view);
            txt_ssc = view.findViewById(R.id.txt_ssc);
            txt_date = view.findViewById(R.id.txt_date);
            txt_batchno = view.findViewById(R.id.txt_batchno);
            txt_tcname = view.findViewById(R.id.txt_tcname);
            container = view.findViewById(R.id.container);

        }
    }
}
