/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gtq.androideventmanager.syncaller;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public abstract class BaseSyncCaller implements IBaseSyncCaller, ICommand {

    private int state = BaseSyncCaller.CALLER_WAIT_EXEC;
    private int exec_code = 0;
    private ICallbackThread backcaller;
    private String handleName;
    private long syncType = IBaseSyncCaller.SYNCTYPE_DEFAULT;
    private String sycnKey;
    private long exectime = 0;
    private int delayExec = 0;
    private String cmpKey = null;
    //
    private Map paramMaps = null;
    private Map returnMaps = null;

    public IBaseSyncCaller appendParam(String pname, Object p) {
        if (paramMaps == null) {
            paramMaps = new HashMap<String, Object>();
        }
        if (paramMaps != null) {
            paramMaps.put(pname, p);
        }
        return this;
    }

    public Object getParam(String pname) {
        if (paramMaps != null) {
            return paramMaps.get(pname);
        }
        return null;
    }

    public IBaseSyncCaller appendReturn(String pname, Object p) {
        if (returnMaps == null) {
            returnMaps = new HashMap<String, Object>();
        }
        if (returnMaps != null) {
            returnMaps.put(pname, p);
        }
        return this;
    }

    public Object getReturn(String pname) {
        if (returnMaps != null) {
            return returnMaps.get(pname);
        }
        return null;
    }

    @Override
    public String getCmpKey() {
        return cmpKey;
    }

    @Override
    public void setCmpKey(String cmpKey) {
        this.cmpKey = cmpKey;
    }

    @Override
    public int getDelayExec() {
        return delayExec;
    }

    @Override
    public void setDelayExec(int delayExec) {
        this.delayExec = delayExec;
    }

    @Override
    public long getExecTime() {
        return exectime;
    }

    @Override
    public void setExecTime(long exectime) {
        this.exectime = exectime;
    }

    public BaseSyncCaller(String phandleName, ICallbackThread curthread) {
        handleName = phandleName;
        if ((curthread != null) && (curthread instanceof ICallbackThread)) {
            this.backcaller = curthread;
        } else {
            throw new RuntimeException("create BaseSyncCaller need ICallBackThread");
        }
    }

    /* (non-Javadoc)
     * 
     * @see com.xhhd.common.syncaller.IBaseSyncCaller#callerback(int)
     */
    @Override
    public int callerback(int execret) {
        if (backcaller != null && (backcaller instanceof ICallbackThread)) {
            backcaller.addCommand(this);
        } else {
            //this.action();
        }
        return 0;
    }

    @Override
    public final synchronized int getState() {
        return state;
    }

    public final synchronized int setState(int state_param) {
        int old = state;
        state = state_param;
        return old;
    }

    @Override
    public synchronized int getExecode() {
        return exec_code;
    }

    public synchronized int setExecode(int execode_param) {
        int old = exec_code;
        exec_code = execode_param;
        return old;
    }

    @Override
    public ICallbackThread getCaller() {
        return backcaller;
    }

    @Override
    public String getSyncKey() {
        return sycnKey;
    }

    @Override
    public long getSyncType() {
        return syncType;
    }

    final void setSyncKey(String szkey) {
        this.sycnKey = szkey;
    }

    final void setSyncType(long ntype) {
        this.syncType = ntype;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String getHandleName() {
        return handleName;
    }

    /* (non-Javadoc)
     * 加入队列后在主线程执行
     * @see com.xhhd.common.syncaller.IBaseSyncCaller#exec()
     */
    @Override
    public int exec() {
        return 0;
    }

    @Override
    public void reCalExecTime(IBaseSyncCaller older) {
        return;
    }

}
