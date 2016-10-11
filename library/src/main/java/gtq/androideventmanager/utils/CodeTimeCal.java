package gtq.androideventmanager.utils;

import android.util.Log;

/**
 * 代码运行时间计算
 *
 * @author 杨鸿岚
 */
public class CodeTimeCal {

    private int calmillsec;
    private long startTime;
    private long curTime;
    public static String Time_TAG = "time_tag";

    public static CodeTimeCal newTimeCal() {
        return new CodeTimeCal();
    }

    public static CodeTimeCal newTimeCal(int millsec) {
        return new CodeTimeCal(millsec);
    }

    public CodeTimeCal() {
        calmillsec = 50;
        startTime = System.currentTimeMillis();
        curTime = startTime;
    }

    public CodeTimeCal(int millsec) {
        calmillsec = millsec;
        startTime = System.currentTimeMillis();
        curTime = startTime;
    }

    /**
     * 计算当前时间到开始时间是否超时
     *
     * @param millsec
     * @param text
     * @return
     */
    public boolean fullCal(int millsec, String text) {
        try {
            long difftime = (System.currentTimeMillis() - startTime);
            boolean result = (difftime >= millsec);
            if (result) {
                String strmsgString = String.format("fullCal:CodeTimeCal:代码执行超时：%s,超时 %d>=%d ms", text, difftime, millsec);
                Log.i("AndroidEventManager", strmsgString);
            }
            return result;
        } finally {
            curTime = System.currentTimeMillis();
        }
    }


    /**
     * 计算当前时间到上次执行时间是否超时
     *
     * @param millsec
     * @param text
     * @return
     */
    public boolean curCal(int millsec, String text) {
        try {
            long difftime = (System.currentTimeMillis() - curTime);
            boolean result = (difftime >= millsec);

            if (result) {

                String strmsgString = String.format("curCal:CodeTimeCal:代码执行超时：%s,超时 %d>=%d ms", text, difftime, millsec);
                Log.i("AndroidEventManager",strmsgString);
//                    System.out.println(strmsgString);

            }
            return result;
        } finally {
            curTime = System.currentTimeMillis();
        }
    }


    public void reset_cur() {
        curTime = System.currentTimeMillis();
    }
}
