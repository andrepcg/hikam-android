package com.jwkj.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class HashList<K, V> {
    private List<K> keyArr = new ArrayList();
    private KeySort<K, V> keySort;
    private HashMap<K, List<V>> map = new HashMap();

    public HashList(KeySort<K, V> keySort) {
        this.keySort = keySort;
    }

    public K getKey(V v) {
        return this.keySort.getKey(v);
    }

    public void sortKeyComparator(Comparator<K> comparator) {
        Collections.sort(this.keyArr, comparator);
    }

    public K getKeyIndex(int key) {
        return this.keyArr.get(key);
    }

    public List<V> getValueListIndex(int key) {
        return (List) this.map.get(getKeyIndex(key));
    }

    public V getValueIndex(int key, int value) {
        return getValueListIndex(key).get(value);
    }

    public int size() {
        return this.keyArr.size();
    }

    public void clear() {
        for (Object remove : this.map.keySet()) {
            this.map.remove(remove);
        }
    }

    public boolean contains(Object object) {
        return false;
    }

    public boolean isEmpty() {
        return false;
    }

    public Object remove(int location) {
        return null;
    }

    public boolean remove(Object object) {
        return false;
    }

    public boolean removeAll(Collection arg0) {
        return false;
    }

    public boolean retainAll(Collection arg0) {
        return false;
    }

    public Object set(int location, Object object) {
        return this.keyArr.set(location, object);
    }

    public List subList(int start, int end) {
        return this.keyArr.subList(start, end);
    }

    public Object[] toArray() {
        return this.keyArr.toArray();
    }

    public Object[] toArray(Object[] array) {
        return this.keyArr.toArray(array);
    }

    public boolean add(Object object) {
        V v = object;
        K key = getKey(v);
        if (this.map.containsKey(key)) {
            ((List) this.map.get(key)).add(v);
        } else {
            List<V> list = new ArrayList();
            list.add(v);
            this.keyArr.add(key);
            this.map.put(key, list);
        }
        return false;
    }

    public int indexOfKey(K k) {
        return this.keyArr.indexOf(k);
    }
}
