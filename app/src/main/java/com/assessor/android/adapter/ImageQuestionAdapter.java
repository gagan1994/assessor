package com.assessor.android.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.assessor.android.R;
import com.assessor.android.iface.ItemClickListener;
import com.assessor.android.model.ImagebasedQuestions;

import java.util.List;

public class ImageQuestionAdapter extends RecyclerView.Adapter<ImageQuestionAdapter.MyViewHolder> implements View.OnClickListener {

    private List<ImagebasedQuestions> mQuestionModel;
    Context mContext;
    ItemClickListener mListener;

    public ImageQuestionAdapter(Context context, List<ImagebasedQuestions> questionModels, ItemClickListener mDocUploadListener) {
        this.mQuestionModel = questionModels;
        this.mContext = context;
        this.mListener = mDocUploadListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_question_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        final ImagebasedQuestions qModel = mQuestionModel.get(position);
        holder.txt_qno.setText(String.valueOf(qModel.getId()));
        holder.txt_question.setText(qModel.getQuestion());
        String[] options = qModel.getOptions();
        holder.radioGroup.removeAllViews();
        for (int i = 0; i < options.length; i++) {
            try {
                RadioButton rdbtn = new RadioButton(mContext);
                rdbtn.setId(position * 100 + i);
                rdbtn.setText(options[i]);
                final int pos = i;
                rdbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        qModel.setAnswer(options[pos]);
                        mQuestionModel.get(position).setAnswer(options[pos]);
                    }
                });
                if (qModel.getAnswer() != null && qModel.getAnswer().equalsIgnoreCase(options[i])) {
                    rdbtn.setSelected(true);
                    rdbtn.setChecked(true);
                }
                holder.radioGroup.addView(rdbtn);
            } catch (Exception e) {
                System.out.println("------------" + position + "-----" + i);
                e.printStackTrace();
            }
        }
//       holder.ans1.setText(qModel.getAnswers().get(0).getAnswer());
//       holder.ans2.setText(qModel.getAnswers().get(1).getAnswer());
//       holder.ans3.setText(qModel.getAnswers().get(2).getAnswer());
//       holder.ans4.setText(qModel.getAnswers().get(3).getAnswer());


    }

    @Override
    public int getItemCount() {
        return mQuestionModel.size();
    }

    @Override
    public void onClick(View v) {
        Log.d("AdapterQuestion", " Name " + ((RadioButton) v).getText() + " Id is " + v.getId());
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_qno, txt_question;
        RadioGroup radioGroup;
        RelativeLayout container;
        ImageView refImage;

        public MyViewHolder(View view) {
            super(view);
            txt_qno = view.findViewById(R.id.txt_qno);
            refImage = view.findViewById(R.id.ref_img);
            txt_question = view.findViewById(R.id.txt_question);

            radioGroup = view.findViewById(R.id.radiogroup);


            container = view.findViewById(R.id.container);

        }
    }
}
