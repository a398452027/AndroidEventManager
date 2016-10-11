package gtq.androideventmanager.utils.bindCollection;

import java.util.Arrays;
import java.util.Comparator;

/**
 *
 */
public class SimpleSortArray<T extends SimpleSortArray.SortObject> {

    private final int rankCount;
    private final T[] array;
    private final Comparator<T> comparator;
    private T threshold;
    private int size;

    public SimpleSortArray(T[] array, Comparator<T> comparator) {
        this.array = array;
        this.comparator = comparator;

        rankCount = array.length;
    }

    public int size() {
        return size;
    }

    public T get(int i) {
        return array[i];
    }

    public void add(T obj) {

        // 先看一下是否大于阀值，如果是，则插入到列表中

        if (threshold != null) {
            int result = comparator.compare(threshold, obj);
            if (result <= 0) {
                // 小于阀值
                if (size < rankCount) {
                    // 列表还未满，则放入末尾
                    array[size++] = threshold = obj;
                }
                return;
            }
        } else {
            array[0] = threshold = obj;
            size = 1;
            return;
        }

        // 插入到中间
        int insertPoint = Arrays.binarySearch(array, 0, size, obj, comparator);

        if (insertPoint < 0) {
            insertPoint = -insertPoint - 1;
        }

        if (insertPoint >= rankCount) {
            return; // 防御性
        }
        // 将insertPoint位置开始，将数组整体往后移一位
        int lastIndex = Math.min(size - 1, rankCount - 2);

        for (int i = lastIndex; i >= insertPoint; i--) {
            array[i + 1] = array[i]; // 往后移动一位
        }

        // 将新加入的值放进去
        array[insertPoint] = obj;

        if (size < rankCount) {
            threshold = array[size++];
        } else {
            threshold = array[rankCount - 1];
        }
    }

    public void clear() {
        threshold = null;
        size = 0;
        for (int i = 0; i < array.length; i++) {
            array[i] = null;
        }
    }

    //倒序，大的在前
    public static <T extends SortObject> Comparator<T> getDescDefaultComparator() {
        return new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                return o2.amount - o1.amount; // 大的在前
            }
        };
    }

    //正序
    public static <T extends SortObject> Comparator<T> getAscDefaultComparator() {
        return new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                return o1.amount - o2.amount; // 小的在前
            }
        };
    }

    public static class SortObject {

        protected final long id;
        protected final byte[] name;
        // for rank
        protected int amount;

        public SortObject(long id, byte[] name) {
            this.id = id;
            this.name = name;
        }

        public final void setAmount(int amount) {
            this.amount = amount;
        }

        public long getId() {
            return id;
        }
    }
}
