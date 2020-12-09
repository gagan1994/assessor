package com.assessor.android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.assessor.android.R;
import com.assessor.android.adapter.ImageQuestionAdapter;
import com.assessor.android.model.ImagebasedQuestions;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PracticalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PracticalFragment extends Fragment {


    public static int questionCounter = 0;
    ImageQuestionAdapter questionAdapter;
    List<ImagebasedQuestions> currentQuestion = new ArrayList<>();


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView mRecycler;

    public PracticalFragment() {
        // Required empty public constructor
    }

    public void init() {

        mRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        currentQuestion.clear();
        questionAdapter = new ImageQuestionAdapter(getActivity(), currentQuestion, null);
        mRecycler.setAdapter(questionAdapter);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PracticalFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PracticalFragment newInstance(String param1, String param2) {
        PracticalFragment fragment = new PracticalFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_practical, container, false);
        mRecycler = view.findViewById(R.id.recycler);
        return view;
    }
}