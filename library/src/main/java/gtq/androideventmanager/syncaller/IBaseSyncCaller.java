/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gtq.androideventmanager.syncaller;



/**
 *
 */
public interface IBaseSyncCaller {

    public static final int CALLER_WAIT_EXEC = 0;
    public static final int CALLER_EXECING = 1;
    public static final int CALLER_WAIT_CALLBACK = 2;
    public static final int CALLER_CALLBACK = 3;
    public static final int CALLER_WAIT_REMOVE = 4;
    public static final int CALLER_REMOVED = 5;
    public static final long SYNCTYPE_DEFAULT = 0xffffffffffffffffL;

    public int exec();
    
    /** 异步执行的方法
     * @return
     */
    public int syncexec();

    public int callerback(int execret);

    public int getState();

    //public int setState(int state_param);
    public int getExecode();

    //public int setExecode(int execode_param);
    public ICallbackThread getCaller();

    public String getHandleName();

    public String getSyncKey();

    public long getSyncType();

    public long getExecTime();

    public void setExecTime(long exectime);
    
    public void reCalExecTime(IBaseSyncCaller older);

    public int getDelayExec();

    public void setDelayExec(int delayExec);

    public String getCmpKey();

    public void setCmpKey(String cmpKey);
}
