package com.assessor.android.activity;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.assessor.android.R;
import com.assessor.android.utility.AccPref;

public class VerificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        ImageView mImgCheck = findViewById(R.id.imageView);
        ((Animatable) mImgCheck.getDrawable()).start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), ExamSchedule.class));
                finish();
                /*Intent intent = new Intent(getApplicationContext(), QuizActivity.class);
                intent.putExtra("exam_id", 1);
                intent.putExtra("ssc", "ssc");
                intent.putExtra("end_time", System.currentTimeMillis()+100000);
                startActivity(intent);*/
            }
        }, 2000);
    }
}