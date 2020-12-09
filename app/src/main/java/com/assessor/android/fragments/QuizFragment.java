package com.assessor.android.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
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

import com.assessor.android.BaseFragment;
import com.assessor.android.R;
import com.assessor.android.ScoreManager;
import com.assessor.android.model.ImagebasedQuestions;
import com.assessor.android.model.OptionalQuestions;
import com.assessor.android.model.PracticalQuestions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class QuizFragment extends BaseFragment {
    private static final String TAG = QuizFragment.class.getName();
    private Object question;
    private int position;
    // a variable fo type interface called scoremanger. Still a reference
    private ScoreManager scoreManager;


    public QuizFragment() {
        Log.d("QuizFragment", "QuizFragment Constructor");
        // Required empty public constructor
    }

    public static QuizFragment newInstance(Object question, ScoreManager scoreChanger, int pos) {
        Log.d("QuizFragment", "newInstance");
        QuizFragment fragment = new QuizFragment();
        // this code says the rules dictated by the interface Scoremanager will be answer in the activity method scoreChanger.
        fragment.scoreManager = scoreChanger;
        fragment.question = question;
        fragment.position = pos;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        Log.d(TAG, question.toString());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView");
        //Sets root view
        View rootView;
        if (question instanceof ImagebasedQuestions) {
            rootView = inflater.inflate(R.layout.image_question_row, container, false);
        } else {
            rootView = inflater.inflate(R.layout.question_row, container, false);
        }
        //In charge of setting up view only after rootview is returned and fragment instantiated.
        setupView(rootView);

        // There is no view until rootview is returned.
        return rootView;
    }

    public void setupView(View rootView) {
        TextView questionView = rootView.findViewById(R.id.question);
        TextView txt_qno = rootView.findViewById(R.id.txt_qno);
        TextView txt_question = rootView.findViewById(R.id.txt_question);
        ImageView ref_img = rootView.findViewById(R.id.ref_img);
        TextView textanswer = rootView.findViewById(R.id.textanswer);
        txt_qno.setText(String.valueOf(position + 1));


        RadioGroup radioGroup = rootView.findViewById(R.id.radiogroup);


        RelativeLayout container = rootView.findViewById(R.id.container);
        if (question instanceof ImagebasedQuestions) {
            final ImagebasedQuestions qModel = (ImagebasedQuestions) question;
            if(!TextUtils.isEmpty(qModel.getQuestion_image_url()))
                Picasso.get().load(qModel.getQuestion_image_url()).into(ref_img);
            txt_question.setText(qModel.getQuestion());
            String[] options = qModel.getOptions();
            radioGroup.removeAllViews();
            for (int i = 0; i < options.length; i++) {
                try {
                    RadioButton rdbtn = new RadioButton(getContext());
                    rdbtn.setId(position * 100 + i);
                    rdbtn.setText(options[i]);
                    final int pos = i;
                    rdbtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            qModel.setAnswer(options[pos]);
                            //((ImagebasedQuestions) mQuestionModel.get(position)).setAnswer(options[pos]);
                            //((ImagebasedQuestions) mQuestionModel.get(position)).setAnswerPosition((pos+1));
                            scoreManager.addToScore(position, options[pos], (pos + 1));
                        }
                    });
                    if (qModel.getAnswer() != null && qModel.getAnswer().equalsIgnoreCase(options[i])) {
                        rdbtn.setSelected(true);
                        rdbtn.setChecked(true);
                    }
                    radioGroup.addView(rdbtn);
                } catch (Exception e) {
                    System.out.println("------------" + position + "-----" + i);
                    e.printStackTrace();
                }
            }
        } else if (question instanceof OptionalQuestions) {
            final OptionalQuestions qModel = (OptionalQuestions) question;
            txt_question.setText(qModel.getQuestion());
            String[] options = qModel.getOptions();
            radioGroup.removeAllViews();
            for (int i = 0; i < options.length; i++) {
                try {
                    RadioButton rdbtn = new RadioButton(getContext());
                    rdbtn.setId(position * 100 + i);
                    rdbtn.setText(options[i]);
                    final int pos = i;
                    rdbtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            qModel.setAnswer(options[pos]);
                            //((OptionalQuestions) mQuestionModel.get(position)).setAnswer(options[pos]);
                            //((OptionalQuestions) mQuestionModel.get(position)).setAnswerPosition((pos+1));
                            scoreManager.addToScore(position, options[pos], (pos + 1));
                        }
                    });
                    if (qModel.getAnswer() != null && qModel.getAnswer().equalsIgnoreCase(options[i])) {
                        rdbtn.setSelected(true);
                        rdbtn.setChecked(true);
                    }
                    radioGroup.addView(rdbtn);
                } catch (Exception e) {
                    System.out.println("------------" + position + "-----" + i);
                    e.printStackTrace();
                }
            }

        } else {
            final PracticalQuestions qModel = (PracticalQuestions) question;
            txt_question.setText(qModel.getQuestion());
            radioGroup.removeAllViews();
            if (qModel.getOptions() instanceof Boolean) {
                textanswer.setVisibility(View.VISIBLE);
                radioGroup.setVisibility(View.GONE);
                textanswer.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        qModel.setAnswer(s.toString());
                        scoreManager.addToScore(position, s.toString(), 0);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            } else {
                textanswer.setVisibility(View.GONE);
                radioGroup.setVisibility(View.VISIBLE);
                ArrayList<String> options = (ArrayList<String>) qModel.getOptions();

                for (int i = 0; i < options.size(); i++) {
                    try {
                        RadioButton rdbtn = new RadioButton(getContext());
                        rdbtn.setId(position * 100 + i);
                        rdbtn.setText(options.get(i));
                        final int pos = i;
                        rdbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                qModel.setAnswer(options.get(pos));
                                scoreManager.addToScore(position, options.get(pos), (pos + 1));
                                //((PracticalQuestions) mQuestionModel.get(position)).setAnswer(options.get(pos));
                                //((PracticalQuestions) mQuestionModel.get(position)).setAnswerPosition((pos+1));
                            }
                        });
                        if (qModel.getAnswer() != null && qModel.getAnswer().equalsIgnoreCase(options.get(pos))) {
                            rdbtn.setSelected(true);
                            rdbtn.setChecked(true);
                        }
                        radioGroup.addView(rdbtn);
                    } catch (Exception e) {
                        System.out.println("------------" + position + "-----" + i);
                        e.printStackTrace();
                    }
                }
            }
        }
        //rootView.setBackgroundColor(ContextCompat.getColor(getContext(), question.getBackgroundColor()));


    }


}
