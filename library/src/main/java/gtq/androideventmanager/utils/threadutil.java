/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gtq.androideventmanager.utils;


import android.util.Log;

import java.util.Collection;

import gtq.androideventmanager.utils.bindCollection.bindCollection;
import gtq.androideventmanager.utils.vrefType.refBool;

/**
 *
 * @author laodao
 */
public class threadutil {

    private static final int max_once_sleep_time = 100;

    public static void sleepCheckRuning(long millis, refBool borun) {
        if (millis > 500) {
            int nsleep = (int) ((millis / max_once_sleep_time) + 1);
            for (int i = 0; i < nsleep; i++) {
                while (borun.get()) {
                    threadutil.sleep(max_once_sleep_time);
                }
            }
        } else {
            threadutil.sleep(millis);
        }
    }

    public static void sleepCheckStop(long millis, refBool bostop) {
        if (millis > 500) {
            int nsleep = (int) ((millis / max_once_sleep_time) + 1);
            for (int i = 0; i < nsleep; i++) {
                while (!bostop.get()) {
                    threadutil.sleep(max_once_sleep_time);
                }
            }
        } else {
            threadutil.sleep(millis);
        }
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception ex) {
            Log.w("AndroidEventManager",ex);
        }
    }

    public static void sleep(long millis, int nanos) {
        try {
            Thread.sleep(millis, nanos);
        } catch (Exception ex) {
            Log.w("AndroidEventManager",ex);
        }
    }

    public static void wait(Object coll) {
        try {
            if (coll != null) {
                synchronized (coll) {
                    boolean bwait = true;
                    if (coll instanceof Collection) {
                        bwait = ((Collection) coll).isEmpty();
                    } else if (coll instanceof bindCollection) {
                        bwait = ((bindCollection) coll).isEmpty();
                    }
                    if (bwait) {
                        coll.wait();
                    }
                }
            }
        } catch (Exception ex) {
            Log.w("AndroidEventManager",ex);
        }
    }

    public static void wait(Object coll, long millis) {
        try {
            if (coll != null) {
                synchronized (coll) {
                    boolean bwait = true;
                    if (coll instanceof Collection) {
                        bwait = ((Collection) coll).isEmpty();
                    } else if (coll instanceof bindCollection) {
                        bwait = ((bindCollection) coll).isEmpty();
                    }
                    if (bwait) {
                        coll.wait(millis);
                    }
                }
            }
        } catch (Exception ex) {
            Log.w("AndroidEventManager",ex);
        }
    }

    public static void wait(Object coll, long millis, int nanos) {
        try {
            if (coll != null) {
                synchronized (coll) {
                    boolean bwait = true;
                    if (coll instanceof Collection) {
                        bwait = ((Collection) coll).isEmpty();
                    } else if (coll instanceof bindCollection) {
                        bwait = ((bindCollection) coll).isEmpty();
                    }
                    if (bwait) {
                        coll.wait(millis, nanos);
                    }
                }
            }
        } catch (Exception ex) {
            Log.w("AndroidEventManager",ex);
        }
    }

    public static void notify(Object coll) {
        try {
            if (coll != null) {
                synchronized (coll) {
                    coll.notify();
                }
            }
        } catch (Exception ex) {
            Log.w("AndroidEventManager",ex);
        }
    }

    public static void notifyAll(Object coll) {
        try {
            if (coll != null) {
                synchronized (coll) {
                    coll.notifyAll();
                }
            }
        } catch (Exception ex) {
            Log.w("AndroidEventManager",ex);
        }
    }
}
