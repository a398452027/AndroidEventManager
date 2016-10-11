/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gtq.androideventmanager.syncaller;



import android.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import gtq.androideventmanager.utils.CodeTimeCal;
import gtq.androideventmanager.utils.bindCollection.bindHashTreeMap;
import gtq.androideventmanager.utils.bindCollection.bindMapQueue;
import gtq.androideventmanager.utils.bindCollection.bindObj2;


/**
 *
 */
public class BaseSyncCallerManage {
    //==
    //private static final Object objbindcallerslock = new Object();

    private final HashMap<Integer, bindObj2<bindMapQueue<String, IBaseSyncCaller>, bindHashTreeMap<String, IBaseSyncCaller>>> callerbindthreadmaps = new HashMap<Integer, bindObj2<bindMapQueue<String, IBaseSyncCaller>, bindHashTreeMap<String, IBaseSyncCaller>>>();
    private final ArrayList<Thread> callerbindthreads = new ArrayList<Thread>();
    private final HashMap<Runnable, Thread> bindthreads_runnable = new HashMap<Runnable, Thread>();
    //==
    private boolean isstop = false;
    private final String nameString;
    //==
    public int runtime_max_list_count = 0;
    public int runtime_bind_max_list_count = 0;
    
    public BaseSyncCallerManage(String name) {
        nameString = name;
    }
    private static final Comparator<IBaseSyncCaller> delaycallerComp = new Comparator<IBaseSyncCaller>() {
        @Override
        public int compare(IBaseSyncCaller o1, IBaseSyncCaller o2) {
            if (o1.getCmpKey() == null) {
                o1.setCmpKey(o1.getExecTime() + "_" + o1.getSyncKey());
            }
            if (o2.getCmpKey() == null) {
                o2.setCmpKey(o2.getExecTime() + "_" + o2.getSyncKey());
            }
            return o1.getCmpKey().compareTo(o2.getCmpKey());
        }
    };
    
    private int execWaitCaller(BaseSyncCaller caller) {
        if (caller != null) {
            if (caller.getState() == IBaseSyncCaller.CALLER_EXECING) {
                int nsyncexecret = caller.syncexec();
                caller.setExecode(nsyncexecret);
                {
                    //CALLER_WAIT_CALLBACK可能会被其他线程执行，所以要立即改变状态为CALLER_CALLBACK
                    caller.setState(IBaseSyncCaller.CALLER_WAIT_CALLBACK);
                    //立即处理 CALLER_CALLBACK,CALLER_CALLBACK 状态其他线程是不会获得执行权
                    caller.setState(IBaseSyncCaller.CALLER_CALLBACK);
                }
            }
            
            if (caller.getState() == IBaseSyncCaller.CALLER_CALLBACK) {
                int nexitcode = caller.getExecode();
                caller.callerback(nexitcode);
                {
                    caller.setState(IBaseSyncCaller.CALLER_REMOVED);
                }
                return IBaseSyncCaller.CALLER_REMOVED;
            } else {
                {
                    caller.setState(IBaseSyncCaller.CALLER_REMOVED);
                }
                //不能识别的状态
                return IBaseSyncCaller.CALLER_REMOVED;
            }
        }
        return IBaseSyncCaller.CALLER_REMOVED;
    }
    
    private void dothreadrunsleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception e) {
        }
    }
    private static int SELEEP_COUNT = 50;
    
    private void startcallbindthread(int threadcount) {
        int istart = callerbindthreads.size();
        if (istart > 0) {
            return;
        }
        final BaseSyncCallerManage fcallermanage = this;
        for (int i = istart; i < threadcount; i++) {
            final int fi = callerbindthreads.size();
            Runnable thdrunRunnable = new Runnable() {
                private BaseSyncCallerManage ownermanage = fcallermanage;
                public int bindid = fi;
                
                @Override
                public void run() {
                    Thread thisthd = bindthreads_runnable.get(this);
                    bindthreads_runnable.remove(this);
                    int thdcode = thisthd.hashCode();
                    thisthd = null;
                    int nexec_count = 0;
                    //ArrayList<IBaseSyncCaller> nextCallerList = new ArrayList<IBaseSyncCaller>();
                    CodeTimeCal codeTimeCal = CodeTimeCal.newTimeCal(100);
                    while (true) {
                        BaseSyncCaller caller = null;
                        try {
                            long BindType = 0;
                            //nextCallerList.clear();
                            int nlistsizeI = 0;
                            codeTimeCal.curCal(100,"startcallbindthread.before.getWaitCaller");
                            bindObj2<bindMapQueue<String, IBaseSyncCaller>, bindHashTreeMap<String, IBaseSyncCaller>> queues = ownermanage.callerbindthreadmaps.get(thdcode);
                            if (queues != null) {
                                bindMapQueue<String, IBaseSyncCaller> callers = queues.getObj1();
                                bindHashTreeMap<String, IBaseSyncCaller> delaycallers = queues.getObj2();
                                if (callers != null && delaycallers != null && (!callers.isEmpty() || !delaycallers.isEmpty())) {
                                    try {
                                        callers.getLockobj().lock();
                                        nlistsizeI = callers.size();
                                        codeTimeCal.curCal(100,"startcallbindthread.callers.before.getWaitCaller");
                                        BindType = this.bindid;
                                        //优先取不延迟的任务
                                        caller = (BaseSyncCaller) callers.peek();
                                        if (caller != null) {
                                            caller = (BaseSyncCaller) callers.poll(caller.getSyncKey(), caller);
                                            if (caller != null) {
                                                nexec_count++;
                                                caller.setState(IBaseSyncCaller.CALLER_EXECING);
                                            }
                                        }
                                    } finally {
                                        callers.getLockobj().unlock();
                                    }
                                    
                                    if (caller == null) {
                                        try {
                                            //取延迟的任务
                                            delaycallers.getLockobj().lock();
                                            nlistsizeI += delaycallers.size();
                                            caller = (BaseSyncCaller) delaycallers.peek();
                                            if (caller != null) {
                                                if (System.currentTimeMillis() > caller.getExecTime()) {
                                                    delaycallers.poll(caller.getSyncKey(), caller);
                                                    nexec_count++;
                                                    caller.setState(IBaseSyncCaller.CALLER_EXECING);
                                                } else {
                                                    caller = null;
                                                }
                                            }
                                            codeTimeCal.curCal(100,"startcallbindthread.callers.after.getWaitCaller");
                                        } finally {
                                            delaycallers.getLockobj().unlock();
                                        }
                                    }
                                }
                                
                                if (isstop && (caller == null) && (callers == null || callers.isEmpty()) && (delaycallers == null || delaycallers.isEmpty())) {
                                    break;
                                }
                                codeTimeCal.curCal(100,"startcallbindthread.after.getWaitCaller");
                                if (caller != null && ownermanage.execWaitCaller(caller) == IBaseSyncCaller.CALLER_REMOVED) {
                                    //删除日志
                                   Log.i("AndroidEventManager",Thread.currentThread().getName() + ":remove:" + caller + ":" + ((caller
                                           .getCaller() == null) ? "" : caller.getCaller().getName()) + "=size=" + nlistsizeI + "=handle=" + caller.getHandleName() + "=type=" + caller.getSyncType() + "=key=" + caller.getSyncKey() + "=state=" + caller.getState() + "=maxlist=" + runtime_max_list_count + "=bindmaxlist=" + runtime_bind_max_list_count);
                                }
                            }
                        } catch (Exception e) {
                            Log.w("AndroidEventManager",e);
                        } catch (Throwable ex) {
                            Log.w("AndroidEventManager",ex);
                        }
                        codeTimeCal.curCal(100,"startcallbindthread.next==" + (caller == null ? "NULL" : caller
                                .getSyncKey()));
                        if (!isstop) {
                            if (caller != null) {
                                caller = null;
                                if (nexec_count > SELEEP_COUNT) {
                                    nexec_count = 0;
                                    dothreadrunsleep(50);
                                } else {
                                    dothreadrunsleep(1);
                                }
                            } else {
                                dothreadrunsleep(200);
                            }
                        }
                        codeTimeCal.reset_cur();
                    }
                }
            };
            Thread thd = new Thread(thdrunRunnable, nameString + "-bind-sync-caller-" + i);
            callerbindthreads.add(thd);
            bindthreads_runnable.put(thdrunRunnable, thd);
            callerbindthreadmaps.put(thd.hashCode(), new bindObj2<bindMapQueue<String, IBaseSyncCaller>, bindHashTreeMap<String, IBaseSyncCaller>>(new bindMapQueue<String, IBaseSyncCaller>(), new bindHashTreeMap<String, IBaseSyncCaller>(delaycallerComp)));
        }
        for (int i = istart; i < callerbindthreads.size(); i++) {
            Thread thread = callerbindthreads.get(i);
            if (thread != null) {
                thread.start();
            }
        }
    }
    
    /**启动线程
     * @param threadcount 线程数量
     * @return
     */
    public boolean start(int threadcount) {
        return start(threadcount, threadcount);
    }
    
    public boolean start(int threadcount, int bindthreadcount) {
        isstop = false;
        //startcallthread(threadcount);
        startcallbindthread(bindthreadcount);
        return true;
    }
    
    public boolean stop() {
        return stop(10 * 1000);
    }
    
    public boolean stop(long waitTime) {
        isstop = true;
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
        }
        //==
        try {
            for (Map.Entry<Runnable, Thread> entry : bindthreads_runnable.entrySet()) {
                Thread mspthd = entry.getValue();
                if (mspthd != null) {
                    bindMapQueue<String, IBaseSyncCaller> arrayList = callerbindthreadmaps.get(mspthd.hashCode()).getObj1();
                    IBaseSyncCaller caller = arrayList.peek();
                    if (caller != null) {
                        Log.i("AndroidEventManager",String.format("%s thdname:%s  size:%d  handlename:%s key:%s", nameString, mspthd.getName(), arrayList.size(), caller.getHandleName(), caller.getSyncKey()));
                    } else {
                        Log.i("AndroidEventManager",String.format("%s thdname:%s  size:%d", nameString, mspthd.getName(), arrayList.size()));
                    }
                }
            }
        } catch (Exception e) {
        }
        for (int i = 0; i < callerbindthreads.size(); i++) {
            Thread thread = callerbindthreads.get(i);
            if (thread != null) {
                try {
                    Log.i("AndroidEventManager",String.format("%s thdname:%s close.join", nameString, thread.getName()));
                    thread.join(waitTime);
                    if (thread.isAlive()) {
                        thread.interrupt();
                        thread.join(1000 * 5);
                    }
                } catch (Exception e) {
                }
            }
        }
        bindthreads_runnable.clear();
        callerbindthreads.clear();
        callerbindthreadmaps.clear();
        //==
        return true;
    }
    
    /**
     * @param bindthreadidx 线程标识
     * @param key 任务标识 （队列去重）
     * @param caller
     * @param delay 延时
     * @return
     */
    public boolean add_set_bindsync(long bindthreadidx, String key, BaseSyncCaller caller, int delay) {
        return add_set_bindsync(bindthreadidx, key, caller, delay, false);
    }
    
    /**
     * @param bindthreadidx
     * @param key
     * @param caller
     * @param delay
     * @param repeatExec
     * @return
     */
    public boolean add_set_bindsync(long bindthreadidx, String key, BaseSyncCaller caller, int delay, boolean repeatExec) {
        if (bindthreadidx < 0) {
            bindthreadidx = Math.abs(bindthreadidx);
        }
        CodeTimeCal codeTimeCal = CodeTimeCal.newTimeCal(1000);
        try {
            if (caller.getSyncKey() == null || caller.getSyncKey().isEmpty()) {
                key = caller.getHandleName() + "_" + key;
            }
            if (!callerbindthreads.isEmpty()) {
                int nidx = (int) (bindthreadidx % callerbindthreads.size());
                Thread thd = callerbindthreads.get(nidx);
                bindObj2<bindMapQueue<String, IBaseSyncCaller>, bindHashTreeMap<String, IBaseSyncCaller>> queues = callerbindthreadmaps.get(thd.hashCode());
                bindMapQueue<String, IBaseSyncCaller> callers = queues.getObj1();
                bindHashTreeMap<String, IBaseSyncCaller> delaycallers = queues.getObj2();
                if (callers != null && delaycallers != null) {
                    codeTimeCal.curCal(0,"add_set_bindsync.subarrlist.before.lock");
                    
                    IBaseSyncCaller findcer = null;
                    try {
                        callers.getLockobj().lock();
                        codeTimeCal.curCal(0,"add_set_bindsync.subarrlist.lock");
                        findcer = callers.getByKey(key);
                    } finally {
                        callers.getLockobj().unlock();
                    }
                    if (findcer == null) {
                        try {
                            delaycallers.getLockobj().lock();
                            findcer = delaycallers.getByKey(key);
                        } finally {
                            delaycallers.getLockobj().unlock();
                        }
                    }
                    //
                    boolean isreplace = false;
                    //==
                    caller.setState(BaseSyncCaller.CALLER_WAIT_EXEC);
                    caller.setSyncKey(key);
                    caller.setSyncType(bindthreadidx);
                    caller.setDelayExec(delay);
                    if (findcer != null) {
                        isreplace = true;
                        if (delay < 0) {
                            caller.setExecTime(0);
                            if (findcer.getExecTime() > 0) {
                                try {
                                    delaycallers.getLockobj().lock();
                                    delaycallers.remove(findcer.getSyncKey());
                                } finally {
                                    delaycallers.getLockobj().unlock();
                                }
                            }
                        } else {
                            caller.setExecTime(findcer.getExecTime());
                        }
                        caller.reCalExecTime(findcer);
                    } else if (delay > 0) {
                        caller.setExecTime(System.currentTimeMillis() + delay);
                    } else {
                        caller.setExecTime(0);
                    }
                    
                    try {
                        if (isreplace) {
                            if (repeatExec) {
                                caller.exec();
                            }
                        } else {
                            caller.exec();
                        }
                    } catch (Exception e) {
                        Log.w("AndroidEventManager",e);
                    }
                    
                    if (caller.getExecTime() > 0) {
                        try {
                            delaycallers.getLockobj().lock();
                            //延迟
                            delaycallers.check_put(key, caller);
                        } finally {
                            delaycallers.getLockobj().unlock();
                        }
                    } else {
                        try {
                            callers.getLockobj().lock();
                            //立即执行
                            callers.check_put(key, caller);
                        } finally {
                            callers.getLockobj().unlock();
                        }
                    }
                    try {
                        if ((callers.size() + delaycallers.size()) > runtime_bind_max_list_count) {
                            runtime_bind_max_list_count = callers.size() + delaycallers.size();
                        }
                    } catch (Exception e) {
                        Log.w("AndroidEventManager",e);
                    }
                    return true;
                }
            }
            return false;
        } finally {
            codeTimeCal.fullCal(0,"add_set_bindsync");
        }
    }
    
    public boolean add_set_sync(long type, String key, BaseSyncCaller caller, int delay) {
        return add_set_bindsync(type, key, caller, delay);
    }
    
    public static void main(String[] args) throws InterruptedException {
        final BaseSyncCallerManage syncCallerManage = new BaseSyncCallerManage("test");
        syncCallerManage.start(20);
        final AtomicInteger aint1 = new AtomicInteger(0);
        final AtomicInteger aint = new AtomicInteger(0);
        for (int i = 0; i < 2; i++) {
            Thread thd = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                        long thdstart = System.currentTimeMillis();
                        while ((System.currentTimeMillis() - thdstart) < 500) {
                            long loopstart = System.currentTimeMillis();
                            for (int i = 0; i < 10; i++) {
                                long start = System.currentTimeMillis();
                                long id = ((int) ((Math.random() * 1000000) % 20000));
                                
                                aint1.incrementAndGet();
                                syncCallerManage.add_set_sync(0, "373546334564587" + String.valueOf(id), new BaseSyncCaller("saveOrDeleteGuildSync", null) {
                                    @Override
                                    public int syncexec() {
                                        try {
                                            Thread.sleep(1);
                                            aint.incrementAndGet();
                                            System.out.println("syncexec " + this.getCmpKey() + ":" + aint1.get() + ":" + aint.get());
                                        } catch (Exception e) {
                                        }
                                        return 0;
                                    }
                                    
                                    @Override
                                    public void action() {
                                    }
                                }, 1000 * 20);
                                long end = System.currentTimeMillis();
                                if ((end - start) > 50) {
                                    //System.out.println("add_set_sync 超时：" + (end - start));
                                }
                            }
                            
                            for (int i = 0; i < 2; i++) {
                                long start = System.currentTimeMillis();
                                long id = ((int) ((Math.random() * 1000000) % 20000));
                                
                                aint1.incrementAndGet();
                                syncCallerManage.add_set_sync(0, "373546334564587" + String.valueOf(id), new BaseSyncCaller("saveOrDeleteGuildSync_xiao_delay", null) {
                                    @Override
                                    public int syncexec() {
                                        try {
                                            Thread.sleep(1);
                                            aint.incrementAndGet();
                                            System.out.println("syncexec " + this.getCmpKey() + ":" + aint1.get() + ":" + aint.get());
                                        } catch (Exception e) {
                                        }
                                        return 0;
                                    }
                                    
                                    @Override
                                    public void action() {
                                    }
                                }, 100 * ((int) (Math.random() * 20)));
                                long end = System.currentTimeMillis();
                                if ((end - start) > 50) {
                                    //System.out.println("add_set_sync 超时：" + (end - start));
                                }
                            }
                            
                            for (int i = 0; i < 2; i++) {
                                long start = System.currentTimeMillis();
                                long id = ((int) ((Math.random() * 1000000) % 20000));
                                
                                aint1.incrementAndGet();
                                syncCallerManage.add_set_sync(0, "373546334564587" + String.valueOf(id), new BaseSyncCaller("saveOrDeleteGuildSync_no_delay", null) {
                                    @Override
                                    public int syncexec() {
                                        try {
                                            Thread.sleep(1);
                                            aint.incrementAndGet();
                                            System.out.println("syncexec " + this.getCmpKey() + ":" + aint1.get() + ":" + aint.get());
                                        } catch (Exception e) {
                                        }
                                        return 0;
                                    }
                                    
                                    @Override
                                    public void action() {
                                    }
                                }, 0);
                                long end = System.currentTimeMillis();
                                if ((end - start) > 50) {
                                    //System.out.println("add_set_sync 超时：" + (end - start));
                                }
                            }
                            long loopend = System.currentTimeMillis();
                            if ((loopend - loopstart) > 50) {
                                //System.out.println(Thread.currentThread().getName() + " add_set_sync loop 超时：" + (loopend - loopstart));
                            } else {
                                //System.out.println(Thread.currentThread().getName() + " add_set_sync loop：" + (loopend - loopstart) + ":" + syncCallerManage.runtime_max_list_count + ":" + syncCallerManage.runtime_bind_max_list_count);
                            }
                            
                            try {
                                Thread.sleep(10);
                            } catch (Exception e) {
                            }
                        }
                        System.out.println(Thread.currentThread().getName() + " thread end");
                    } catch (Exception e) {
                        System.out.println(Thread.currentThread().getName() + e.getMessage());
                    }
                }
            });
            thd.start();
        }
        Thread.sleep(1000 * 60 * 1);
        syncCallerManage.stop();
        while (true) {
            Thread.sleep(10000);
            System.out.println(" debug end");
        }
    }
}
