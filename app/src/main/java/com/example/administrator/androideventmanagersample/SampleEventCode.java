package com.example.administrator.androideventmanagersample;

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
