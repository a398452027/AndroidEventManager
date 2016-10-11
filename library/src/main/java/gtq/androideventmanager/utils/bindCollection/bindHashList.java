/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gtq.androideventmanager.utils.bindCollection;

import android.util.Log;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;


/**
 *
 */
public class bindHashList<K, V> implements bindCollection {

    private Map<K, V> objmap = null;
    private List<V> objList = null;
    private final ReentrantLock lockobj = new ReentrantLock();
    private Comparator<? super V> comparator = null;

    public bindHashList(Map<K, V> pamap, List<V> list, Comparator<? super V> pacomparator) throws Exception {
        if (pamap == null || !pamap.isEmpty() || list == null || !list.isEmpty()) {
            throw new Exception("must need (new Map and new List) and (Map and List is Empty)");
        }
        this.objmap = pamap;
        this.objList = list;
        this.comparator = pacomparator;
    }

    public bindHashList(Map<K, V> pamap, Comparator<? super V> pacomparator) throws Exception {
        if (pamap == null || !pamap.isEmpty()) {
            throw new Exception("must need (new Map) and (Map is Empty)");
        }
        this.objmap = pamap;
        this.objList = new ArrayList<V>();
        this.comparator = pacomparator;
    }

    public bindHashList(List<V> list, Comparator<? super V> pacomparator) throws Exception {
        if (list == null || !list.isEmpty()) {
            throw new Exception("must need (new List) and (List is Empty)");
        }
        this.objList = list;
        this.objmap = new HashMap<K, V>();
        this.comparator = pacomparator;
    }

    public bindHashList(Comparator<? super V> pacomparator) {
        this.objmap = new HashMap<K, V>();
        this.objList = new ArrayList<V>();
        this.comparator = pacomparator;
    }

    public bindHashList() {
        this.objmap = new HashMap<K, V>();
        this.objList = new ArrayList<V>();
        this.comparator = null;
    }

    public ReentrantLock getLockobj() {
        return lockobj;
    }

    public boolean check_put(K k, V new_v) {
        V oldv = objmap.get(k);
        if (oldv == null) {
            objmap.put(k, new_v);
            objList.add(new_v);
        } else if (oldv == new_v) {
        } else {
            objmap.put(k, new_v);
            objList.remove(oldv);
            objList.add(new_v);
        }
        return true;
    }

    public boolean check_put(K k, V new_v, int pos) {
        V oldv = objmap.get(k);
        if (oldv == null) {
            objmap.put(k, new_v);
            objList.add(pos, new_v);
        } else if (oldv == new_v) {
        } else {
            objmap.put(k, new_v);
            objList.remove(oldv);
            objList.add(pos, new_v);
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
            objList.remove(oldv);
        }
        return true;
    }

    public void sort(Comparator<? super V> pacomparator) {
        if (pacomparator == null) {
            pacomparator = this.comparator;
        }
        if (pacomparator != null) {
            Collections.sort(objList, pacomparator);
        }
    }

    public V getByIndex(int i) {
        if (i >= 0 && i < objList.size()) {
            return objList.get(i);
        }
        return null;
    }

    public V[] copyArr(V[] a) {
    	V[] arr = objList.toArray(a);
        return arr;
    }

    @Override
    public int size() {
        if (objmap.size() != objList.size()) {
            Log.w("AndroidEventManager","bindHashList size diff error");

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
        if (objList != null) {
            objList.clear();
        }
    }
}
