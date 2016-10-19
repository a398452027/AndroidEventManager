package com.example.administrator.androideventmanagersample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import gtq.androideventmanager.AndroidEventManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView= (TextView) findViewById(R.id.text);
        textView.setText("55");
        //启动同步任务
        //这里测试完成任务后通知service
        AndroidEventManager.getInstance().runEvent(SampleEventCode.NOTIFY,"hello service",0);
    }
}
