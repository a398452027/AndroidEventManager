package com.example.administrator.androideventmanagersample;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.antfortune.freeline.FreelineCore;

import gtq.androideventmanager.AndroidEventManager;

/**
 * Created by guotengqian on 2016/10/14 17:46.
 * def
 */

public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        registerRunner();
        Intent intent = new Intent(this, ListenerService.class);
        startService(intent);
    }
    /**
     *@def [方法定义] 注册任务
     *@params [入参说明]
     *@reture [返回参数说明]
     *@time 2016/10/14 17:47
     *@user guotengqian
    */
    private void registerRunner(){
        //注册同步任务
        AndroidEventManager.getInstance().registerEventRunner(SampleEventCode.SYNC,new SyncRunner());
        //注册异步任务
        AndroidEventManager.getInstance().registerEventRunner(SampleEventCode.ASYNC,new ASyncRunner());
        //通知没有任务，无需注册
    }
}
