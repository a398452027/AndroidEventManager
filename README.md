# AndroidEventManager（android事件总线框架）
## 目的（已完成功能）
- 实现service与activity之间，或不同线程间数据传递。
- 启动异步任务，监听任务，监听器优先级，任务延时，

## AndroidEventManager三种常用的情况

### 一.执行通知

- 定义EventCode，事件通过Code作为标识

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
}
   
```   


 - 启动通知事件， demo代码演示在MainActivity里通知Service并传递两个参数。

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

- 注意onEventRunEnd方法永远执行在主线程，所以不要在这里做耗时操作，如果要进行耗时操作，请参考后面章节启动异步任务完成。

### 二.执行异步事件
- 同样定义EventCode，事件通过Code作为标识

```
public class SampleEventCode {
    protected static int CODE_INC = 0;
    /**
     * @def 异步任务
     */
    public static int ASYNC = CODE_INC++;
}
```

- 编写任务代码,网络请求的代码可以放在这里。注意注释
```
/**
 * Created by guotengqian on 2016/10/14 17:43.
 * def 异步任务
 */
public class ASyncRunner implements EventManager.OnEventRunner{
    @Override
    public void onEventRun(Event event) throws Exception {
        //耗时代码
        doPost(event);
        
      if ((boolean) event.getReturnParamAtIndex(0)){
          //设置事件是否成功
          event.setSuccess(true);
      }else{
          event.setSuccess(false);
      }
        //注意onEventRun已经在外部catch了异常，如果要自己捕捉，需要自己添加try，catch代码
        //可以通过getFailException获取外部捕捉的异常
        //event.getFailException();
    }

    private void doPost (Event event) {
        //设置返回参数
        event.addReturnParam(true);
        event.addReturnParam("data");
    }
}
```

- 任务写好了，肯定需要注册任务，demo在Application注册（本人后期准备用注解完成绑定，暂时各位需要手动绑定一下）

```
public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        ```省略
        //注册异步任务
        AndroidEventManager.getInstance().registerEventRunner(SampleEventCode.ASYNC,new ASyncRunner());
    }
```

- 启动任务
```
    //这里传递了俩个参数给runner
    AndroidEventManager.getInstance().pushEvent(SampleEventCode.ASYNC,"start AsyncRunner",0);
    //延时1000ms启动
    //AndroidEventManager.getInstance().pushEventDelayed(SampleEventCode.ASYNC,1000,"start AsyncRunner",0);
```

- 监听，与通知相同，这里就不赘述了。流程是：启动事件----异步执行runner----监听器获得事件。任务中添加的返回参数可以通过 event.getReturnParamAtIndex()获取。

### 三.执行同步事件

- 定义code，注册任务，同异步事件一样，不再赘述。
- 执行同步任务并获取返回参数
```
        //执行同步事件
        Event syncEvent= AndroidEventManager.getInstance().runEvent(SampleEventCode.SYNC,0);
        //获取返回参数
        String reStr= (String) syncEvent.getReturnParamAtIndex(0);
```
- 注意同步事件执行在启动他的线程，不一定在主线程，所以可以在异步任务里开启同步事件，完成俩个网络请求需要先后访问的情况。

## 使用库

```
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
dependencies {
    ```省略
    compile 'com.github.a398452027:AndroidEventManager:7a1c3376f1'
}

```

## 后续改进
- 使用注解完成任务注册，Activity，Fragment自动绑定，解绑任务
- 使用Hermes实现不同进程中事件传输
- 定时任务（同一ID任务定时完成，未完成取消）















