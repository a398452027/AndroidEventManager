/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gtq.androideventmanager.utils.bindCollection;


import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 */
public class bindMapQueue<K, V> implements bindCollection {

    private final Map<K, bindObj2<V, Integer>> objmap;
    private final LinkedBlockingQueue<bindObj2<V, Integer>> objQueue = new LinkedBlockingQueue<bindObj2<V, Integer>>();
    private final ReentrantLock lockobj = new ReentrantLock();

    public bindMapQueue(Map<K, bindObj2<V, Integer>> pamap) throws Exception {
        if (pamap == null || !pamap.isEmpty()) {
            throw new Exception("must need (new Map and new List) and (Map and List is Empty)");
        }
        this.objmap = pamap;
    }

    public bindMapQueue() {
        this.objmap = new HashMap<K, bindObj2<V, Integer>>();
    }

    public ReentrantLock getLockobj() {
        return lockobj;
    }

    public boolean check_put(K k, V new_v) {
        bindObj2<V, Integer> oldv = objmap.get(k);
        if (oldv == null) {
            oldv = new bindObj2<V, Integer>(new_v, 0);
            objmap.put(k, oldv);
            objQueue.add(oldv);
        } else {
            oldv.setObj1(new_v);
            oldv.setObj2(0);
        }
        return true;
    }

    public V getByKey(K k) {
        bindObj2<V, Integer> oldv = objmap.get(k);
        if (oldv != null) {
            return oldv.getObj1();
        }
        return null;
    }

    public boolean remove(K k) {
        bindObj2<V, Integer> oldv = objmap.remove(k);
        if (oldv != null) {
            objQueue.remove(oldv);
        }
        return true;
    }

    public V poll(K k, V v) {
        bindObj2<V, Integer> oldv = objQueue.peek();
        if (oldv.getObj1() == v) {
            objQueue.poll();
            objmap.remove(k);
            return v;
        }
        return null;
    }

    public V peek() {
        bindObj2<V, Integer> oldv = objQueue.peek();
        if (oldv != null) {
            return oldv.getObj1();
        }
        return null;
    }

    @Override
    public int size() {
        if (objmap.size() != objQueue.size()) {
            Log.i("AndroidEventManager","bindMapQueue size diff error");
        }
        return objmap.size();
    }

    @Override
    public boolean isEmpty() {
        return objmap.isEmpty();
    }

    @Override
    public void clear() {
        if (objmap != null) {
            objmap.clear();
        }
        if (objQueue != null) {
            objQueue.clear();
        }
    }
}
