package com.assessor.android.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.assessor.android.model.OptionalQuestions;
import com.assessor.android.model.PracticalQuestions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.MyViewHolder> implements View.OnClickListener {

    private List<Object> mQuestionModel;
    Context mContext;
    ItemClickListener mListener;

    private static int TYPE_IMAGE = 1;
    private static int TYPE_OPIONAL = 2;
    private static int TYPE_PRACTICAL = 3;

    public QuestionAdapter(Context context, List<Object> questionModels, ItemClickListener mDocUploadListener) {
        this.mQuestionModel = questionModels;
        this.mContext = context;
        this.mListener = mDocUploadListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_IMAGE) {
            View itemView;
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.image_question_row, parent, false);
            return new MyViewHolder(itemView);
        } else {
            View itemView;
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.question_row, parent, false);
            return new MyViewHolder(itemView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mQuestionModel.get(position) instanceof ImagebasedQuestions) {
            return TYPE_IMAGE;

        } else if (mQuestionModel.get(position) instanceof OptionalQuestions) {
            return TYPE_OPIONAL;

        } else {
            return TYPE_PRACTICAL;
        }
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_PRACTICAL) {
            final PracticalQuestions qModel = (PracticalQuestions) mQuestionModel.get(position);
            holder.txt_qno.setText(String.valueOf(qModel.getId()));
            holder.txt_question.setText(qModel.getQuestion());
            holder.radioGroup.removeAllViews();
            if (qModel.getOptions() instanceof Boolean) {
                holder.textanswer.setVisibility(View.VISIBLE);
                holder.radioGroup.setVisibility(View.GONE);
                holder.textanswer.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        qModel.setAnswer(s.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            } else {
                holder.textanswer.setVisibility(View.GONE);
                holder.radioGroup.setVisibility(View.VISIBLE);
                ArrayList<String> options = (ArrayList<String>) qModel.getOptions();

                for (int i = 0; i < options.size(); i++) {
                    try {
                        RadioButton rdbtn = new RadioButton(mContext);
                        rdbtn.setId(position * 100 + i);
                        rdbtn.setText(options.get(i));
                        final int pos = i;
                        rdbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                qModel.setAnswer(options.get(pos));
                                ((PracticalQuestions) mQuestionModel.get(position)).setAnswer(options.get(pos));
                                ((PracticalQuestions) mQuestionModel.get(position)).setAnswerPosition((pos + 1));
                            }
                        });
                        if (qModel.getAnswer() != null && qModel.getAnswer().equalsIgnoreCase(options.get(pos))) {
                            rdbtn.setSelected(true);
                            rdbtn.setChecked(true);
                        }
                        holder.radioGroup.addView(rdbtn);
                    } catch (Exception e) {
                        System.out.println("------------" + position + "-----" + i);
                        e.printStackTrace();
                    }
                }
            }
        } else if (getItemViewType(position) == TYPE_IMAGE) {
            final ImagebasedQuestions qModel = (ImagebasedQuestions) mQuestionModel.get(position);
            holder.txt_qno.setText(String.valueOf(qModel.getId()));
            Picasso.get().load(qModel.getQuestion_image_url()).into(holder.ref_img);
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
                            ((ImagebasedQuestions) mQuestionModel.get(position)).setAnswer(options[pos]);
                            ((ImagebasedQuestions) mQuestionModel.get(position)).setAnswerPosition((pos + 1));
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
        } else if (getItemViewType(position) == TYPE_OPIONAL) {
            final OptionalQuestions qModel = (OptionalQuestions) mQuestionModel.get(position);
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
                            ((OptionalQuestions) mQuestionModel.get(position)).setAnswer(options[pos]);
                            ((OptionalQuestions) mQuestionModel.get(position)).setAnswerPosition((pos + 1));
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
        }

    }

    public List<Object> getmQuestionModel() {
        return mQuestionModel;
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
        TextView textanswer;
        ImageView ref_img;
        RadioGroup radioGroup;
        RelativeLayout container;

        public MyViewHolder(View view) {
            super(view);
            txt_qno = view.findViewById(R.id.txt_qno);
            txt_question = view.findViewById(R.id.txt_question);
            ref_img = view.findViewById(R.id.ref_img);
            textanswer = view.findViewById(R.id.textanswer);


            radioGroup = view.findViewById(R.id.radiogroup);


            container = view.findViewById(R.id.container);

        }
    }
}
