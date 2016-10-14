package gtq.androideventmanager;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.util.SparseArray;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import gtq.androideventmanager.syncaller.BaseSyncCallerManage;
import gtq.androideventmanager.syncaller.ICallbackThread;
import gtq.androideventmanager.syncaller.ICommand;

/**
 * Created by guotengqian on 2016/10/10 16:19.
 * def 事件管理类（单例）
 */
public class AndroidEventManager extends EventManager implements
        ICallbackThread {

    public static AndroidEventManager getInstance() {
        if (sInstance == null) {
            sInstance = new AndroidEventManager();
        }
        return sInstance;
    }

    private static AndroidEventManager sInstance;

    private static final int WHAT_EVENT_NOTIFY = 1;
    private static final int WHAT_EVENT_PUSH = 2;
    private static final int WHAT_EVENT_END = 3;

    public static final int WHAT_EVENT_SYNC_CALLBACK = 4;
    private BaseSyncCallerManage baseSyncCallerManage;

    private ExecutorService mExecutorService; //线程池

    private SparseArray<List<OnEventRunner>> mMapCodeToEventRunner = new SparseArray<List<OnEventRunner>>();

    private SparseArray<List<OnEventListener>> mMapCodeToEventListener = new SparseArray<List<OnEventListener>>();
    private SparseArray<List<OnEventListener>> mMapCodeToEventListenerAddCache = new SparseArray<List<OnEventListener>>();
    private SparseArray<List<OnEventListener>> mMapCodeToEventListenerRemoveCache = new SparseArray<List<OnEventListener>>();
    private boolean mIsMapListenerLock = false;
    private SparseArray<OnEventListener> mMapListenerUseOnce = new SparseArray<OnEventListener>();
    private Map<Event, List<OnEventListener>> mMapEventToListener = new ConcurrentHashMap<Event, List<OnEventListener>>();

    private Map<Event, Event> mMapRunningEvent = new ConcurrentHashMap<Event, Event>();
    private Map<OnEventListener, Integer> mMapPriorityToListener = new ConcurrentHashMap<OnEventListener, Integer>();

    private List<Event> mListEventNotify = new LinkedList<Event>();
    private boolean mIsEventNotifying;

    private static Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            final int nWhat = msg.what;
            if (nWhat == WHAT_EVENT_END) {
                sInstance.onEventRunEnd((Event) msg.obj);
            } else if (nWhat == WHAT_EVENT_PUSH) {
                final Event event = (Event) msg.obj;
                if (!sInstance.isEventRunning(event)) {
                    sInstance.mExecutorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            sInstance.processEvent(event);
                            mHandler.sendMessage(mHandler.obtainMessage(
                                    WHAT_EVENT_END, event));
                        }
                    });
                } else {
                    sInstance.addEventListener(event.getEventCode(),
                            new OnEventListener() {
                                @Override
                                public void onEventRunEnd(Event e) {
                                    event.setResult(e);
                                    mHandler.sendMessage(mHandler
                                            .obtainMessage(WHAT_EVENT_END,
                                                    event));
                                }
                            }, true);

                }
            } else if (nWhat == WHAT_EVENT_NOTIFY) {
                sInstance.doNotify((Event) msg.obj);
            } else if (nWhat == WHAT_EVENT_SYNC_CALLBACK
                    && msg.obj instanceof ICommand) {
                ICommand command = (ICommand) msg.obj;
                command.action();
            }
        }
    };


    @Override
    public void addCommand(ICommand command) {
        Handler handler = AndroidEventManager.getInstance().getMainHandler();
        Message message = new Message();
        message.what = AndroidEventManager.WHAT_EVENT_SYNC_CALLBACK;
        message.obj = command;
        handler.sendMessage(message);
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return "AndroidEventManager";
    }

    public Handler getMainHandler() {
        return mHandler;
    }

    private AndroidEventManager() {
        mExecutorService = Executors.newFixedThreadPool(3, new ThreadFactory() {
            @Override
            public Thread newThread(final Runnable r) {
                Thread t = new Thread() {
                    @Override
                    public void run() {
                        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                        r.run();
                    }
                };
                return t;
            }
        });
        baseSyncCallerManage = new BaseSyncCallerManage("AndroidEventManager");
        baseSyncCallerManage.start(1);
    }

    public BaseSyncCallerManage getBaseSyncCallerManage() {
        return baseSyncCallerManage;
    }

    @Override
    public Event pushEvent(int eventCode, Object... params) {
        final Event event = new Event(eventCode, params);
        mHandler.sendMessage(mHandler.obtainMessage(WHAT_EVENT_PUSH, event));
        return event;
    }

    public void pushEventDelayed(int eventCode, long delayMillis,
                                 Object... params) {
        final Event event = new Event(eventCode, params);
        mHandler.sendMessageDelayed(
                mHandler.obtainMessage(WHAT_EVENT_PUSH, event), delayMillis);
    }

    public Event pushEventEx(int eventCode, OnEventListener listener,
                             Object... params) {
        Event event = new Event(eventCode, params);
        List<OnEventListener> listeners = mMapEventToListener.get(event);
        if (listeners == null) {
            listeners = new LinkedList<OnEventListener>();
            mMapEventToListener.put(event, listeners);
        }
        listeners.add(listener);

        mHandler.sendMessage(mHandler.obtainMessage(WHAT_EVENT_PUSH, event));
        return event;
    }

    @Override
    public Event runEvent(int eventCode, Object... params) {
        Event event = new Event(eventCode, params);
        processEvent(event);
        mHandler.sendMessage(mHandler.obtainMessage(WHAT_EVENT_END, event));
        return event;
    }

    public void notifyEvent(int eventCode, Object... params) {
        Event e = new Event(eventCode, params);
        e.setSuccess(true);
        mHandler.sendMessage(mHandler.obtainMessage(WHAT_EVENT_END, e));
    }

    public void cancelAllEvent() {
        mExecutorService.shutdownNow();
        mExecutorService = Executors.newCachedThreadPool();
    }

    public void submitTask(Runnable task) {
        mExecutorService.submit(task);
    }

    protected boolean processEvent(Event event) {
        if (isEventRunning(event)) {
            return false;
        }

        mMapRunningEvent.put(event, event);

        try {
            List<OnEventRunner> runners = mMapCodeToEventRunner.get(event
                    .getEventCode());
            if (runners != null) {
                for (OnEventRunner runner : runners) {
                    runner.onEventRun(event);
                }
            }
        } catch (Exception e) {
//            if (e instanceof NotPrintStackException) {
//                Log.w("AndroidEventManager NotPrintStackException",e);
//
//            } else {
            Log.w("AndroidEventManager", e);
//            }
            event.setFailException(e);
        } finally {
            mMapRunningEvent.remove(event);
        }

        return true;
    }

    @Override
    public void registerEventRunner(int eventCode, OnEventRunner runner) {
        List<OnEventRunner> runners = mMapCodeToEventRunner.get(eventCode);
        if (runners == null) {
            runners = new LinkedList<OnEventRunner>();
            mMapCodeToEventRunner.put(eventCode, runners);
        }
        runners.clear();
        runners.add(runner);
    }

    public void removeEventRunner(int eventCode, OnEventRunner runner) {
        List<OnEventRunner> runners = mMapCodeToEventRunner.get(eventCode);
        if (runners != null) {
            runners.remove(runner);
        }
    }

    public void clearAllRunners() {
        mMapCodeToEventRunner.clear();
    }

    public boolean isEventRunning(Event e) {
        return mMapRunningEvent.containsKey(e);
    }

    public boolean isEventRunning(int eventCode, Object... params) {
        return isEventRunning(new Event(eventCode, params));
    }

    public void addEventListener(int eventCode, OnEventListener listener
    ) {
        addEventListener(eventCode, listener, false);
    }

    public void addEventListener(int eventCode, OnEventListener listener,
                                 boolean bOnce) {
        addEventListener(eventCode, listener, bOnce, 0);
    }

    public void addEventListener(int eventCode, OnEventListener listener,
                                 boolean bOnce, int priority) {
        if (mIsMapListenerLock) {
            addToListenerMap(mMapCodeToEventListenerAddCache, eventCode,
                    listener);
        } else {
            addToListenerMap(mMapCodeToEventListener, eventCode, listener);
        }
        if (bOnce) {
            mMapListenerUseOnce.put(calculateHashCode(eventCode, listener),
                    listener);
        }
        mMapPriorityToListener.put(listener, priority);
    }

    public void removeEventListener(int eventCode, OnEventListener listener) {
        if (mIsMapListenerLock) {
            addToListenerMap(mMapCodeToEventListenerRemoveCache, eventCode,
                    listener);
        } else {
            List<OnEventListener> listeners = mMapCodeToEventListener
                    .get(eventCode);
            if (listeners != null) {
                listeners.remove(listener);
            }
            mMapListenerUseOnce.remove(calculateHashCode(eventCode, listener));
        }
    }

//    public void removeEventListenerEx(Event e, OnEventListener listener) {
//        final List<OnEventListener> listeners = mMapEventToListener.get(e);
//        if (listeners != null) {
//            listeners.remove(listener);
//            if (listeners.size() == 0) {
//                mMapEventToListener.remove(e);
//            }
//        }
//    }

    private int calculateHashCode(int nEventCode, OnEventListener listener) {
        if (listener != null) {
            int nResult = nEventCode;
            nResult = nResult * 29 + listener.hashCode();
            return nResult;
        }
        return nEventCode * 29;
    }

    private void addToListenerMap(SparseArray<List<OnEventListener>> map,
                                  int nEventCode, OnEventListener listener) {
        List<OnEventListener> listeners = map.get(nEventCode);
        if (listeners == null) {
            listeners = new LinkedList<OnEventListener>();
            map.put(nEventCode, listeners);
        }
        listeners.add(listener);
    }

    protected void onEventRunEnd(Event event) {
        notifyEventRunEnd(event);
    }

    private void notifyEventRunEnd(Event event) {
        if (mIsEventNotifying) {
            mListEventNotify.add(event);
        } else {
            doNotify(event);
        }
    }

    private void doNotify(Event event) {
        mIsEventNotifying = true;

        final List<OnEventListener> eventListeners = mMapEventToListener
                .get(event);


        if (eventListeners != null) {
            Collections.sort(eventListeners, new Comparator<OnEventListener>() {

                @Override
                public int compare(OnEventListener arg0, OnEventListener arg1) {
                    // TODO Auto-generated method stub
                    Integer priority0 = mMapPriorityToListener.get(arg0);
                    if (priority0 == null) {
                        priority0 = 0;
                    }
                    Integer priority1 = mMapPriorityToListener.get(arg1);
                    if (priority1 == null) {
                        priority1 = 0;
                    }
                    if (priority0 > priority1) {
                        return -1;
                    } else if (priority0 == priority1) {
                        return 1;
                    }
                    return 1;


                }

            });
            if (eventListeners.size() > 0) {
                final OnEventListener listener = eventListeners.remove(0);
                if (eventListeners.size() == 0) {
                    mMapEventToListener.remove(event);
                }
                try {
                    listener.onEventRunEnd(event);
                } catch (Exception e) {
                    Log.w("AndroidEventManager", e);
                }
            }
        }

        mIsMapListenerLock = true;
        List<OnEventListener> list = mMapCodeToEventListener.get(event
                .getEventCode());
        if (list != null) {
            System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
            Collections.sort(list, new Comparator<OnEventListener>() {

                @Override
                public int compare(OnEventListener arg0, OnEventListener arg1) {
                    // TODO Auto-generated method stub
                    Integer priority0 = mMapPriorityToListener.get(arg0);
                    if (priority0 == null) {
                        priority0 = 0;
                    }
                    Integer priority1 = mMapPriorityToListener.get(arg1);
                    if (priority1 == null) {
                        priority1 = 0;
                    }
                    if (priority0 > priority1) {
                        return -1;
                    } else if (priority0 == priority1) {
                        return 1;
                    }
                    return 1;
                }

            });

            List<OnEventListener> listNeedRemove = null;
            for (OnEventListener listener : list) {
                try {
                    listener.onEventRunEnd(event);
                } catch (Exception e) {
                    Log.w("AndroidEventManager", e);
                }
                if (listener != null) {
                    int nHashCode = calculateHashCode(event.getEventCode(),
                            listener);
                    if (mMapListenerUseOnce.get(nHashCode) != null) {
                        mMapListenerUseOnce.remove(nHashCode);
                        if (listNeedRemove == null) {
                            listNeedRemove = new ArrayList<OnEventListener>();
                        }
                        listNeedRemove.add(listener);
                    }
                }
            }
            if (listNeedRemove != null) {
                list.removeAll(listNeedRemove);
            }
        }
        mIsMapListenerLock = false;

        mIsEventNotifying = false;

        if (mMapCodeToEventListenerAddCache.size() > 0) {
            int nSize = mMapCodeToEventListenerAddCache.size();
            for (int nIndex = 0; nIndex < nSize; ++nIndex) {
                int nCode = mMapCodeToEventListenerAddCache.keyAt(nIndex);
                List<OnEventListener> listCache = mMapCodeToEventListenerAddCache
                        .get(nCode);
                if (listCache.size() > 0) {
                    List<OnEventListener> listeners = mMapCodeToEventListener
                            .get(nCode);
                    if (listeners == null) {
                        listeners = new LinkedList<OnEventListener>();
                        mMapCodeToEventListener.put(nCode, listeners);
                    }
                    listeners.addAll(listCache);
                }
            }
            mMapCodeToEventListenerAddCache.clear();
        }
        if (mMapCodeToEventListenerRemoveCache.size() > 0) {
            int nSize = mMapCodeToEventListenerRemoveCache.size();
            for (int nIndex = 0; nIndex < nSize; ++nIndex) {
                int nCode = mMapCodeToEventListenerRemoveCache.keyAt(nIndex);
                List<OnEventListener> listCache = mMapCodeToEventListenerRemoveCache
                        .get(nCode);
                if (listCache.size() > 0) {
                    List<OnEventListener> listeners = mMapCodeToEventListener
                            .get(nCode);
                    if (listeners != null) {
                        listeners.removeAll(listCache);
                    }
                    for (OnEventListener listener : listCache) {
                        mMapListenerUseOnce.remove(calculateHashCode(nCode,
                                listener));
                    }
                }
            }
            mMapCodeToEventListenerRemoveCache.clear();
        }

        if (mListEventNotify.size() > 0) {
            Event eventNotify = mListEventNotify.get(0);
            mListEventNotify.remove(0);
            mHandler.sendMessage(mHandler.obtainMessage(WHAT_EVENT_NOTIFY,
                    eventNotify));
        }
    }
}
