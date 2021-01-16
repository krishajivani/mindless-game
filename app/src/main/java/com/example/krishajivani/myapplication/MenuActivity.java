package com.example.krishajivani.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MenuActivity extends AppCompatActivity {

    private TextView scoreView;
    private Button restart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        restart = findViewById(R.id.restart); //restart button
        scoreView = (TextView)findViewById(R.id.scoreView); //score textView

        float score = getIntent().getFloatExtra("score", 0); //gets score value from other activity
        scoreView.setText("YOUR SCORE: " + score);

        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); //exits out of this activity back into the parent activity
            }
        });
    }
}
