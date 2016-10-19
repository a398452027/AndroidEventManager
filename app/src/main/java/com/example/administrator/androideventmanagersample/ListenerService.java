package com.example.administrator.androideventmanagersample;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import gtq.androideventmanager.AndroidEventManager;
import gtq.androideventmanager.Event;
import gtq.androideventmanager.EventManager;

/**
 * Created by guotengqian on 2016/10/14 17:39.
 * def 获取同步任务返回参数
 */

//实现Listener接口
public class ListenerService extends Service implements EventManager.OnEventListener {

    /**
     *@def [方法定义] EventManager.OnEventListener继承方法，用于获取事件
     * 此方法，主线程执行
     *@params [入参说明] 自动生成的Event类
    */
    @Override
    public void onEventRunEnd(Event event) {
        //判断是否为我们定义的通知事件
        if(event.getEventCode()==SampleEventCode.NOTIFY){
            //获取第一个参数
            String strParam= (String) event.getParamAtIndex(0);
            Log.i("AndroidEventManager ", "str: "+strParam);
            //获取第二个参数
            Integer intParam = (Integer) event.getParamAtIndex(1);
            Log.i("AndroidEventManager ", "intParam : "+intParam);

        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //添加监听
        //第一个参数：EventCode
        //第二个参数：监听者，这里是Service
        AndroidEventManager.getInstance().addEventListener(SampleEventCode.NOTIFY,this);
        //添加监听
        //第三个参数：为true时只会收到一次事件通知，默认为false
        AndroidEventManager.getInstance().addEventListener(SampleEventCode.NOTIFY,this,true);
        //添加监听
        //第四个参数：监听器优先级，优先级大的监听器先收到事件。优先级越大越先收到事件
        AndroidEventManager.getInstance().addEventListener(SampleEventCode.NOTIFY,this,true,0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消监听
        AndroidEventManager.getInstance().removeEventListener(SampleEventCode.NOTIFY,this);
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
