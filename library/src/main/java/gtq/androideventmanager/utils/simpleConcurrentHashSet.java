package gtq.androideventmanager.utils;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class simpleConcurrentHashSet<E> extends AbstractSet<E> implements Set<E>, java.io.Serializable {

    private static final long serialVersionUID = -8672117787651310382L;
    private static final Object PRESENT = new Object();
    private final ConcurrentHashMap<E, Object> map;

    public simpleConcurrentHashSet() {
        map = new ConcurrentHashMap<E, Object>();
    }

    public simpleConcurrentHashSet(int initialCapacity) {
        map = new ConcurrentHashMap<E, Object>(initialCapacity);
    }

    @Override
    public Iterator<E> iterator() {
        return map.keySet().iterator();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return map.containsKey(o);
    }

    @Override
    public boolean add(E e) {
        return map.put(e, PRESENT) == null;
    }

    @Override
    public boolean remove(Object o) {
        return map.remove(o) == PRESENT;
    }

    @Override
    public void clear() {
        map.clear();
    }

    //
    @Override
    public boolean removeAll(Collection<?> c) {
        if (c != null) {
            for (Object e : c) {
                this.remove(e);
            }
        }
        return true;
    }

    @Override
    public Object[] toArray() {
        return map.keySet().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return map.keySet().toArray(a);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        if (c != null) {
            for (E e : c) {
                this.add(e);
            }
        }
        return true;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}