package com.example.administrator.androideventmanagersample;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import gtq.androideventmanager.Event;
import gtq.androideventmanager.EventManager;

/**
 * Created by guotengqian on 2016/10/14 17:39.
 * def 获取同步任务返回参数
 */

public class ListenerService extends Service implements EventManager.OnEventListener {

    /**
     *@def [方法定义]
     *@params [入参说明]
     *@reture [返回参数说明]
     *@time 2016/10/14 17:40
     *@user guotengqian
    */
    @Override
    public void onEventRunEnd(Event event) {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
