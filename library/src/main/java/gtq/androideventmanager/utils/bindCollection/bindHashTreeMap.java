/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gtq.androideventmanager.utils.bindCollection;

import android.util.Log;


import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 */
public class bindHashTreeMap<K, V> implements bindCollection {

    private final Map<K, V> objmap;
    private TreeMap<V, Integer> objTreeMap = null;
    private final ReentrantLock lockobj = new ReentrantLock();

    public bindHashTreeMap(Map<K, V> pamap, Comparator<? super V> pacomparator) throws Exception {
        if (pamap == null || !pamap.isEmpty()) {
            throw new Exception("must need (new Map) and (Map is Empty)");
        }
        this.objmap = pamap;
        objTreeMap = new TreeMap<V, Integer>(pacomparator);
    }

    public bindHashTreeMap(Comparator<? super V> comparator) {
        this.objmap = new HashMap<K, V>();
        objTreeMap = new TreeMap<V, Integer>(comparator);
    }

    public ReentrantLock getLockobj() {
        return lockobj;
    }

    public boolean check_put(K k, V new_v) {
        V oldv = objmap.get(k);
        if (oldv == null) {
            objmap.put(k, new_v);
            objTreeMap.put(new_v, 0);
        } else {
            objmap.put(k, new_v);
            treemap_remove_obj(oldv);
            objTreeMap.put(new_v, 0);
        }
        return true;
    }

    private boolean treemap_remove_obj(V ov) {
        Integer removev = objTreeMap.remove(ov);
        if (removev == null) {
            Log.i("AndroidEventManager","bindHashTreeMap size diff error");
        }
        return true;
    }

    public V getByKey(K k) {
        V oldv = objmap.get(k);
        if (oldv != null) {
            return oldv;
        }
        return null;
    }

    public boolean remove(K k) {
        V oldv = objmap.remove(k);
        if (oldv != null) {
            treemap_remove_obj(oldv);
        }
        return true;
    }

    public V poll(K k, V v) {
        V oldv = objTreeMap.firstKey();
        if (oldv == v) {
            objTreeMap.pollFirstEntry();
            objmap.remove(k);
            return v;
        }
        return null;
    }

    public V peek() {
        V oldv = objTreeMap.firstKey();
        if (oldv != null) {
            return oldv;
        }
        return null;
    }

    @Override
    public int size() {
        if (objmap.size() != objTreeMap.size()) {
            Log.i("AndroidEventManager","bindHashTreeMap size diff error");
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
        if (objTreeMap != null) {
            objTreeMap.clear();
        }
    }
}
