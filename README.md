# AndroidEventManager（android事件总线框架）
## 目的（已完成功能）
- 实现service与activity之间，或不同线程间数据传递。
- 启动异步任务，监听任务，监听器优先级，任务延时，

## 未来计划功能
- 使用注解完成任务绑定，Activity，Fragment自动绑定，解绑任务
- 不同进程中消息通知
- 定时任务（同一ID任务定时完成，未完成取消）

## AndroidEventManager使用

### 定义EventCode，事件通过Code作为标识
 demo里定义了三个Code作为演示

```
/**
 * Created by guotengqian on 2016/10/14 17:32.
 * def 定义EventCode
 */
public class SampleEventCode {
    protected static int CODE_INC = 0;

    /**
     * @def 通知
     */
    public static int NOTIFY = CODE_INC++;

    /**
     * @def 同步任务
     */
    public static int SYNC = CODE_INC++;

    /**
     * @def 异步任务
     */
    public static int ASYNC = CODE_INC++;
}
   
```   

### 以通知方式使用AndroidEventManager
 demo代码演示在MainActivity里通知Service并传递两个参数。
 - 启动通知事件

```
        //启动通知，并传入俩个参数，这是参数定义为Object... 能传输多个参数
        AndroidEventManager.getInstance().runEvent(SampleEventCode.NOTIFY,"hello service",0);
```
 - 添加事件监听

```
//实现Listener接口
public class ListenerService extends Service implements EventManager.OnEventListener {

    @Override
    public void onCreate() {
        super.onCreate();
        //添加监听
        //第一个参数：EventCode
        //第二个参数：监听者，这里是Service
        AndroidEventManager.getInstance().addEventListener(SampleEventCode.NOTIFY,this);
        //添加监听
        //第三个参数：为true时只会收到一次事件通知，默认为false
        //AndroidEventManager.getInstance().addEventListener(SampleEventCode.NOTIFY,this,true);
        //添加监听
        //第四个参数：监听器优先级，优先级大的监听器先收到事件。优先级越大越先收到事件
        //AndroidEventManager.getInstance().addEventListener(SampleEventCode.NOTIFY,this,true,0);
    }
    ...省略
}
```

- 获取事件，打印参数

```
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
```
