package com.example.hfund;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ChitActivity extends AppCompatActivity {

    private ImageView members,updatePay,chitIssue,followUp,stats,history;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chit);
        members=(ImageView)findViewById(R.id.members);
        updatePay=(ImageView)findViewById(R.id.updatePay);
        chitIssue=(ImageView)findViewById(R.id.chitIssue);
        followUp=(ImageView)findViewById(R.id.followUp);
        stats=(ImageView)findViewById(R.id.stats);
        history=(ImageView)findViewById(R.id.history);
        textView=(TextView)findViewById(R.id.text);

            SharedPreferences sharedPref =getApplicationContext().getSharedPreferences("Hfund",0);
            String chitId = sharedPref.getString("chitId", "null");
            String chitName=sharedPref.getString("chitName","null");

            textView.setText("Chit Name : " + chitName);
        members.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(ChitActivity.this,MembersActivity.class);
                startActivity(intent);
            }
        });
        updatePay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(ChitActivity.this,UpdatePayActivity.class);
                startActivity(intent);
            }
        });
        chitIssue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(ChitActivity.this,ChitIssueActivity.class);
                startActivity(intent);
            }
        });
        followUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(ChitActivity.this,FollowUpActivity.class);
                startActivity(intent);
            }
        });
        stats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(ChitActivity.this,StatsActivity.class);
                startActivity(intent);
            }
        });

    }
}
