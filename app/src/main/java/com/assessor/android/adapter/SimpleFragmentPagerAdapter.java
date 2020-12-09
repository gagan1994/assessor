package com.assessor.android.adapter;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.assessor.android.ScoreManager;
import com.assessor.android.fragments.QuizFragment;

import java.util.List;

/**
 * Created by kdillon on 2018-01-09.
 */

public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {
    private List<Object> questions;
    private ScoreManager scoreChanger;

    // Where the parameters of fragment instantiation live.
    // now the fragment once created must reference the interface scoremanager.
    public SimpleFragmentPagerAdapter(FragmentManager fm, ScoreManager scoreChanger, List<Object> questions) {
        super(fm);
        this.questions = questions;
        this.scoreChanger = scoreChanger;
    }

    public SimpleFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }


    @Override
    public Fragment getItem(int position) {
        return QuizFragment.newInstance(questions.get(position), scoreChanger, position);
    }

    @Override
    public int getCount() {
        return questions.size();
    }
}

