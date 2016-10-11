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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;


/**
 *
 */
public class bindSetList<V> implements bindCollection {

    private Set<V> objset = null;
    private List<V> objList = null;
    private final ReentrantLock lockobj = new ReentrantLock();
    private Comparator<? super V> comparator = null;

    public bindSetList(Set<V> pset, List<V> list, Comparator<? super V> pacomparator) throws Exception {
        if (pset == null || !pset.isEmpty() || list == null || !list.isEmpty()) {
            throw new Exception("must need (new Set and new List) and (Set and List is Empty)");
        }
        this.objset = pset;
        this.objList = list;
        this.comparator = pacomparator;
    }

    public bindSetList(Set<V> pset, Comparator<? super V> pacomparator) throws Exception {
        if (pset == null || !pset.isEmpty()) {
            throw new Exception("must need (new Set) and (Set is Empty)");
        }
        this.objset = pset;
        this.objList = new ArrayList<V>();
        this.comparator = pacomparator;
    }

    public bindSetList(List<V> list, Comparator<? super V> pacomparator) throws Exception {
        if (list == null || !list.isEmpty()) {
            throw new Exception("must need (new List) and (List is Empty)");
        }
        this.objList = list;
        this.objset = new HashSet<V>();
        this.comparator = pacomparator;
    }

    public bindSetList(Comparator<? super V> pacomparator) {
        this.objset = new HashSet<V>();
        this.objList = new ArrayList<V>();
        this.comparator = pacomparator;
    }

    public bindSetList() {
        this.objset = new HashSet<V>();
        this.objList = new ArrayList<V>();
        this.comparator = null;
    }

    public ReentrantLock getLockobj() {
        return lockobj;
    }

    public boolean check_put(V v) {
        if (objset.contains(v)) {
        } else {
            objset.add(v);
            objList.add(v);
        }
        return true;
    }

    public boolean check_put(V v, int pos) {
        if (objset.contains(v)) {
        } else {
            objset.add(v);
            objList.add(pos, v);
        }
        return true;
    }

    public boolean contains(V v) {
        return objset.contains(v);
    }

    public boolean remove(V v) {
        if (objset.contains(v)) {
            objset.remove(v);
            objList.remove(v);
        } else {
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
        if (objset.size() != objList.size()) {
            Log.i("AndroidEventManager","bindSetList size diff error");

        }
        return objset.size();
    }

    @Override
    public boolean isEmpty() {
        return objset.isEmpty();
    }

    @Override
    public void clear() {
        if (objset != null) {
            objset.clear();
        }
        if (objList != null) {
            objList.clear();
        }
    }
}
