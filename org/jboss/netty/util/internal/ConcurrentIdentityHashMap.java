package org.jboss.netty.util.internal;

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

public final class ConcurrentIdentityHashMap<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V> {
    static final int DEFAULT_CONCURRENCY_LEVEL = 16;
    static final int DEFAULT_INITIAL_CAPACITY = 16;
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    static final int MAXIMUM_CAPACITY = 1073741824;
    static final int MAX_SEGMENTS = 65536;
    static final int RETRIES_BEFORE_LOCK = 2;
    Set<Entry<K, V>> entrySet;
    Set<K> keySet;
    final int segmentMask;
    final int segmentShift;
    final Segment<K, V>[] segments;
    Collection<V> values;

    final class EntrySet extends AbstractSet<Entry<K, V>> {
        EntrySet() {
        }

        public Iterator<Entry<K, V>> iterator() {
            return new EntryIterator();
        }

        public boolean contains(Object o) {
            if (!(o instanceof Entry)) {
                return false;
            }
            Entry<?, ?> e = (Entry) o;
            V v = ConcurrentIdentityHashMap.this.get(e.getKey());
            if (v == null || !v.equals(e.getValue())) {
                return false;
            }
            return true;
        }

        public boolean remove(Object o) {
            if (!(o instanceof Entry)) {
                return false;
            }
            Entry<?, ?> e = (Entry) o;
            return ConcurrentIdentityHashMap.this.remove(e.getKey(), e.getValue());
        }

        public int size() {
            return ConcurrentIdentityHashMap.this.size();
        }

        public boolean isEmpty() {
            return ConcurrentIdentityHashMap.this.isEmpty();
        }

        public void clear() {
            ConcurrentIdentityHashMap.this.clear();
        }
    }

    static final class HashEntry<K, V> {
        final int hash;
        final Object key;
        final HashEntry<K, V> next;
        volatile Object value;

        HashEntry(K key, int hash, HashEntry<K, V> next, V value) {
            this.hash = hash;
            this.next = next;
            this.key = key;
            this.value = value;
        }

        K key() {
            return this.key;
        }

        V value() {
            return this.value;
        }

        void setValue(V value) {
            this.value = value;
        }

        static <K, V> HashEntry<K, V>[] newArray(int i) {
            return new HashEntry[i];
        }
    }

    abstract class HashIterator {
        K currentKey;
        HashEntry<K, V>[] currentTable;
        HashEntry<K, V> lastReturned;
        HashEntry<K, V> nextEntry;
        int nextSegmentIndex;
        int nextTableIndex = -1;

        HashIterator() {
            this.nextSegmentIndex = ConcurrentIdentityHashMap.this.segments.length - 1;
            advance();
        }

        public void rewind() {
            this.nextSegmentIndex = ConcurrentIdentityHashMap.this.segments.length - 1;
            this.nextTableIndex = -1;
            this.currentTable = null;
            this.nextEntry = null;
            this.lastReturned = null;
            this.currentKey = null;
            advance();
        }

        public boolean hasMoreElements() {
            return hasNext();
        }

        final void advance() {
            HashEntry hashEntry;
            if (this.nextEntry != null) {
                hashEntry = this.nextEntry.next;
                this.nextEntry = hashEntry;
                if (hashEntry != null) {
                    return;
                }
            }
            while (this.nextTableIndex >= 0) {
                HashEntry[] hashEntryArr = this.currentTable;
                int i = this.nextTableIndex;
                this.nextTableIndex = i - 1;
                hashEntry = hashEntryArr[i];
                this.nextEntry = hashEntry;
                if (hashEntry != null) {
                    return;
                }
            }
            while (this.nextSegmentIndex >= 0) {
                Segment[] segmentArr = ConcurrentIdentityHashMap.this.segments;
                i = this.nextSegmentIndex;
                this.nextSegmentIndex = i - 1;
                Segment<K, V> seg = segmentArr[i];
                if (seg.count != 0) {
                    this.currentTable = seg.table;
                    for (int j = this.currentTable.length - 1; j >= 0; j--) {
                        hashEntry = this.currentTable[j];
                        this.nextEntry = hashEntry;
                        if (hashEntry != null) {
                            this.nextTableIndex = j - 1;
                            return;
                        }
                    }
                    continue;
                }
            }
        }

        public boolean hasNext() {
            while (this.nextEntry != null) {
                if (this.nextEntry.key() != null) {
                    return true;
                }
                advance();
            }
            return false;
        }

        HashEntry<K, V> nextEntry() {
            while (this.nextEntry != null) {
                this.lastReturned = this.nextEntry;
                this.currentKey = this.lastReturned.key();
                advance();
                if (this.currentKey != null) {
                    return this.lastReturned;
                }
            }
            throw new NoSuchElementException();
        }

        public void remove() {
            if (this.lastReturned == null) {
                throw new IllegalStateException();
            }
            ConcurrentIdentityHashMap.this.remove(this.currentKey);
            this.lastReturned = null;
        }
    }

    final class KeySet extends AbstractSet<K> {
        KeySet() {
        }

        public Iterator<K> iterator() {
            return new KeyIterator();
        }

        public int size() {
            return ConcurrentIdentityHashMap.this.size();
        }

        public boolean isEmpty() {
            return ConcurrentIdentityHashMap.this.isEmpty();
        }

        public boolean contains(Object o) {
            return ConcurrentIdentityHashMap.this.containsKey(o);
        }

        public boolean remove(Object o) {
            return ConcurrentIdentityHashMap.this.remove(o) != null;
        }

        public void clear() {
            ConcurrentIdentityHashMap.this.clear();
        }
    }

    static final class Segment<K, V> extends ReentrantLock {
        private static final long serialVersionUID = 5207829234977119743L;
        volatile transient int count;
        final float loadFactor;
        int modCount;
        volatile transient HashEntry<K, V>[] table;
        int threshold;

        Segment(int initialCapacity, float lf) {
            this.loadFactor = lf;
            setTable(HashEntry.newArray(initialCapacity));
        }

        static <K, V> Segment<K, V>[] newArray(int i) {
            return new Segment[i];
        }

        private static boolean keyEq(Object src, Object dest) {
            return src == dest;
        }

        void setTable(HashEntry<K, V>[] newTable) {
            this.threshold = (int) (((float) newTable.length) * this.loadFactor);
            this.table = newTable;
        }

        HashEntry<K, V> getFirst(int hash) {
            HashEntry<K, V>[] tab = this.table;
            return tab[(tab.length - 1) & hash];
        }

        HashEntry<K, V> newHashEntry(K key, int hash, HashEntry<K, V> next, V value) {
            return new HashEntry(key, hash, next, value);
        }

        V readValueUnderLock(HashEntry<K, V> e) {
            lock();
            try {
                V value = e.value();
                return value;
            } finally {
                unlock();
            }
        }

        V get(Object key, int hash) {
            if (this.count != 0) {
                HashEntry<K, V>[] tab = this.table;
                HashEntry<K, V> e = tab[(tab.length - 1) & hash];
                if (tab != this.table) {
                    return get(key, hash);
                }
                while (e != null) {
                    if (e.hash == hash && keyEq(key, e.key())) {
                        V opaque = e.value();
                        if (opaque == null) {
                            return readValueUnderLock(e);
                        }
                        return opaque;
                    }
                    e = e.next;
                }
            }
            return null;
        }

        boolean containsKey(Object key, int hash) {
            if (this.count != 0) {
                HashEntry<K, V>[] tab = this.table;
                HashEntry<K, V> e = tab[(tab.length - 1) & hash];
                if (tab != this.table) {
                    return containsKey(key, hash);
                }
                while (e != null) {
                    if (e.hash == hash && keyEq(key, e.key())) {
                        return true;
                    }
                    e = e.next;
                }
            }
            return false;
        }

        boolean containsValue(Object value) {
            if (this.count != 0) {
                HashEntry<K, V>[] tab = this.table;
                for (HashEntry<K, V> e : tab) {
                    for (HashEntry<K, V> e2 = arr$[i$]; e2 != null; e2 = e2.next) {
                        V v;
                        V opaque = e2.value();
                        if (opaque == null) {
                            v = readValueUnderLock(e2);
                        } else {
                            v = opaque;
                        }
                        if (value.equals(v)) {
                            return true;
                        }
                    }
                }
                if (this.table != tab) {
                    return containsValue(value);
                }
            }
            return false;
        }

        boolean replace(K key, int hash, V oldValue, V newValue) {
            lock();
            try {
                HashEntry<K, V> e = getFirst(hash);
                while (e != null && (e.hash != hash || !keyEq(key, e.key()))) {
                    e = e.next;
                }
                boolean replaced = false;
                if (e != null && oldValue.equals(e.value())) {
                    replaced = true;
                    e.setValue(newValue);
                }
                unlock();
                return replaced;
            } catch (Throwable th) {
                unlock();
            }
        }

        V replace(K key, int hash, V newValue) {
            lock();
            try {
                HashEntry<K, V> e = getFirst(hash);
                while (e != null && (e.hash != hash || !keyEq(key, e.key()))) {
                    e = e.next;
                }
                V oldValue = null;
                if (e != null) {
                    oldValue = e.value();
                    e.setValue(newValue);
                }
                unlock();
                return oldValue;
            } catch (Throwable th) {
                unlock();
            }
        }

        V put(K key, int hash, V value, boolean onlyIfAbsent) {
            lock();
            try {
                HashEntry<K, V>[] tab;
                int index;
                HashEntry<K, V> first;
                HashEntry<K, V> e;
                V oldValue;
                int i = this.count;
                int c = i + 1;
                if (i > this.threshold) {
                    int reduced = rehash();
                    if (reduced > 0) {
                        i = c - reduced;
                        this.count = i - 1;
                        tab = this.table;
                        index = hash & (tab.length - 1);
                        first = tab[index];
                        e = first;
                        while (e != null && (e.hash != hash || !keyEq(key, e.key()))) {
                            e = e.next;
                        }
                        if (e == null) {
                            oldValue = e.value();
                            if (!onlyIfAbsent) {
                                e.setValue(value);
                            }
                        } else {
                            oldValue = null;
                            this.modCount++;
                            tab[index] = newHashEntry(key, hash, first, value);
                            this.count = i;
                        }
                        unlock();
                        return oldValue;
                    }
                }
                i = c;
                tab = this.table;
                index = hash & (tab.length - 1);
                first = tab[index];
                e = first;
                while (e != null) {
                    e = e.next;
                }
                if (e == null) {
                    oldValue = null;
                    this.modCount++;
                    tab[index] = newHashEntry(key, hash, first, value);
                    this.count = i;
                } else {
                    oldValue = e.value();
                    if (onlyIfAbsent) {
                        e.setValue(value);
                    }
                }
                unlock();
                return oldValue;
            } catch (Throwable th) {
                unlock();
            }
        }

        int rehash() {
            HashEntry<K, V>[] oldTable = this.table;
            int oldCapacity = oldTable.length;
            if (oldCapacity >= ConcurrentIdentityHashMap.MAXIMUM_CAPACITY) {
                return 0;
            }
            HashEntry<K, V>[] newTable = HashEntry.newArray(oldCapacity << 1);
            this.threshold = (int) (((float) newTable.length) * this.loadFactor);
            int sizeMask = newTable.length - 1;
            int reduce = 0;
            for (HashEntry<K, V> e : oldTable) {
                if (e != null) {
                    HashEntry<K, V> next = e.next;
                    int idx = e.hash & sizeMask;
                    if (next == null) {
                        newTable[idx] = e;
                    } else {
                        int k;
                        HashEntry<K, V> lastRun = e;
                        int lastIdx = idx;
                        for (HashEntry<K, V> last = next; last != null; last = last.next) {
                            k = last.hash & sizeMask;
                            if (k != lastIdx) {
                                lastIdx = k;
                                lastRun = last;
                            }
                        }
                        newTable[lastIdx] = lastRun;
                        for (HashEntry<K, V> p = e; p != lastRun; p = p.next) {
                            K key = p.key();
                            if (key == null) {
                                reduce++;
                            } else {
                                k = p.hash & sizeMask;
                                newTable[k] = newHashEntry(key, p.hash, newTable[k], p.value());
                            }
                        }
                    }
                }
            }
            this.table = newTable;
            Arrays.fill(oldTable, null);
            return reduce;
        }

        V remove(Object key, int hash, Object value, boolean refRemove) {
            lock();
            try {
                int c = this.count - 1;
                HashEntry<K, V>[] tab = this.table;
                int index = hash & (tab.length - 1);
                HashEntry<K, V> first = tab[index];
                HashEntry<K, V> e = first;
                while (e != null && key != e.key && (refRemove || hash != e.hash || !keyEq(key, e.key()))) {
                    e = e.next;
                }
                V oldValue = null;
                if (e != null) {
                    V v = e.value();
                    if (value == null || value.equals(v)) {
                        oldValue = v;
                        this.modCount++;
                        HashEntry<K, V> newFirst = e.next;
                        for (HashEntry<K, V> p = first; p != e; p = p.next) {
                            K pKey = p.key();
                            if (pKey == null) {
                                c--;
                            } else {
                                newFirst = newHashEntry(pKey, p.hash, newFirst, p.value());
                            }
                        }
                        tab[index] = newFirst;
                        this.count = c;
                    }
                }
                unlock();
                return oldValue;
            } catch (Throwable th) {
                unlock();
            }
        }

        void clear() {
            if (this.count != 0) {
                lock();
                try {
                    HashEntry<K, V>[] tab = this.table;
                    for (int i = 0; i < tab.length; i++) {
                        tab[i] = null;
                    }
                    this.modCount++;
                    this.count = 0;
                } finally {
                    unlock();
                }
            }
        }
    }

    static class SimpleEntry<K, V> implements Entry<K, V> {
        private final K key;
        private V value;

        public SimpleEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public SimpleEntry(Entry<? extends K, ? extends V> entry) {
            this.key = entry.getKey();
            this.value = entry.getValue();
        }

        public K getKey() {
            return this.key;
        }

        public V getValue() {
            return this.value;
        }

        public V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        public boolean equals(Object o) {
            if (!(o instanceof Entry)) {
                return false;
            }
            Entry e = (Entry) o;
            if (eq(this.key, e.getKey()) && eq(this.value, e.getValue())) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            int i = 0;
            int hashCode = this.key == null ? 0 : this.key.hashCode();
            if (this.value != null) {
                i = this.value.hashCode();
            }
            return hashCode ^ i;
        }

        public String toString() {
            return this.key + "=" + this.value;
        }

        private static boolean eq(Object o1, Object o2) {
            if (o1 == null) {
                return o2 == null;
            } else {
                return o1.equals(o2);
            }
        }
    }

    final class Values extends AbstractCollection<V> {
        Values() {
        }

        public Iterator<V> iterator() {
            return new ValueIterator();
        }

        public int size() {
            return ConcurrentIdentityHashMap.this.size();
        }

        public boolean isEmpty() {
            return ConcurrentIdentityHashMap.this.isEmpty();
        }

        public boolean contains(Object o) {
            return ConcurrentIdentityHashMap.this.containsValue(o);
        }

        public void clear() {
            ConcurrentIdentityHashMap.this.clear();
        }
    }

    final class EntryIterator extends HashIterator implements ReusableIterator<Entry<K, V>> {
        EntryIterator() {
            super();
        }

        public Entry<K, V> next() {
            HashEntry<K, V> e = nextEntry();
            return new WriteThroughEntry(e.key(), e.value());
        }
    }

    final class KeyIterator extends HashIterator implements ReusableIterator<K>, Enumeration<K> {
        KeyIterator() {
            super();
        }

        public K next() {
            return nextEntry().key();
        }

        public K nextElement() {
            return nextEntry().key();
        }
    }

    final class ValueIterator extends HashIterator implements ReusableIterator<V>, Enumeration<V> {
        ValueIterator() {
            super();
        }

        public V next() {
            return nextEntry().value();
        }

        public V nextElement() {
            return nextEntry().value();
        }
    }

    final class WriteThroughEntry extends SimpleEntry<K, V> {
        WriteThroughEntry(K k, V v) {
            super(k, v);
        }

        public V setValue(V value) {
            if (value == null) {
                throw new NullPointerException();
            }
            V v = super.setValue(value);
            ConcurrentIdentityHashMap.this.put(getKey(), value);
            return v;
        }
    }

    private static int hash(int h) {
        h += (h << 15) ^ -12931;
        h ^= h >>> 10;
        h += h << 3;
        h ^= h >>> 6;
        h += (h << 2) + (h << 14);
        return (h >>> 16) ^ h;
    }

    Segment<K, V> segmentFor(int hash) {
        return this.segments[(hash >>> this.segmentShift) & this.segmentMask];
    }

    private static int hashOf(Object key) {
        return hash(System.identityHashCode(key));
    }

    public ConcurrentIdentityHashMap(int initialCapacity, float loadFactor, int concurrencyLevel) {
        if (loadFactor <= 0.0f || initialCapacity < 0 || concurrencyLevel <= 0) {
            throw new IllegalArgumentException();
        }
        if (concurrencyLevel > 65536) {
            concurrencyLevel = 65536;
        }
        int sshift = 0;
        int ssize = 1;
        while (ssize < concurrencyLevel) {
            sshift++;
            ssize <<= 1;
        }
        this.segmentShift = 32 - sshift;
        this.segmentMask = ssize - 1;
        this.segments = Segment.newArray(ssize);
        if (initialCapacity > MAXIMUM_CAPACITY) {
            initialCapacity = MAXIMUM_CAPACITY;
        }
        int c = initialCapacity / ssize;
        if (c * ssize < initialCapacity) {
            c++;
        }
        int cap = 1;
        while (cap < c) {
            cap <<= 1;
        }
        for (int i = 0; i < this.segments.length; i++) {
            this.segments[i] = new Segment(cap, loadFactor);
        }
    }

    public ConcurrentIdentityHashMap(int initialCapacity, float loadFactor) {
        this(initialCapacity, loadFactor, 16);
    }

    public ConcurrentIdentityHashMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR, 16);
    }

    public ConcurrentIdentityHashMap() {
        this(16, DEFAULT_LOAD_FACTOR, 16);
    }

    public ConcurrentIdentityHashMap(Map<? extends K, ? extends V> m) {
        this(Math.max(((int) (((float) m.size()) / DEFAULT_LOAD_FACTOR)) + 1, 16), DEFAULT_LOAD_FACTOR, 16);
        putAll(m);
    }

    public boolean isEmpty() {
        int i;
        Segment<K, V>[] segments = this.segments;
        int[] mc = new int[segments.length];
        int mcsum = 0;
        for (i = 0; i < segments.length; i++) {
            if (segments[i].count != 0) {
                return false;
            }
            int i2 = segments[i].modCount;
            mc[i] = i2;
            mcsum += i2;
        }
        if (mcsum != 0) {
            i = 0;
            while (i < segments.length) {
                if (segments[i].count != 0 || mc[i] != segments[i].modCount) {
                    return false;
                }
                i++;
            }
        }
        return true;
    }

    public int size() {
        Segment<K, V>[] segments = this.segments;
        long sum = 0;
        long check = 0;
        int[] mc = new int[segments.length];
        for (int k = 0; k < 2; k++) {
            int i;
            check = 0;
            sum = 0;
            int mcsum = 0;
            for (i = 0; i < segments.length; i++) {
                sum += (long) segments[i].count;
                int i2 = segments[i].modCount;
                mc[i] = i2;
                mcsum += i2;
            }
            if (mcsum != 0) {
                for (i = 0; i < segments.length; i++) {
                    check += (long) segments[i].count;
                    if (mc[i] != segments[i].modCount) {
                        check = -1;
                        break;
                    }
                }
            }
            if (check == sum) {
                break;
            }
        }
        if (check != sum) {
            sum = 0;
            for (Segment<K, V> segment : segments) {
                segment.lock();
            }
            for (Segment<K, V> segment2 : segments) {
                sum += (long) segment2.count;
            }
            for (Segment<K, V> segment22 : segments) {
                segment22.unlock();
            }
        }
        if (sum > 2147483647L) {
            return Integer.MAX_VALUE;
        }
        return (int) sum;
    }

    public V get(Object key) {
        int hash = hashOf(key);
        return segmentFor(hash).get(key, hash);
    }

    public boolean containsKey(Object key) {
        int hash = hashOf(key);
        return segmentFor(hash).containsKey(key, hash);
    }

    public boolean containsValue(Object value) {
        if (value == null) {
            throw new NullPointerException();
        }
        Segment<K, V>[] segments = this.segments;
        int[] mc = new int[segments.length];
        for (int k = 0; k < 2; k++) {
            int i;
            int mcsum = 0;
            for (i = 0; i < segments.length; i++) {
                int i2 = segments[i].modCount;
                mc[i] = i2;
                mcsum += i2;
                if (segments[i].containsValue(value)) {
                    return true;
                }
            }
            boolean cleanSweep = true;
            if (mcsum != 0) {
                for (i = 0; i < segments.length; i++) {
                    if (mc[i] != segments[i].modCount) {
                        cleanSweep = false;
                        break;
                    }
                }
            }
            if (cleanSweep) {
                return false;
            }
        }
        for (Segment<K, V> segment : segments) {
            segment.lock();
        }
        boolean found = false;
        try {
            for (Segment<K, V> segment2 : segments) {
                if (segment2.containsValue(value)) {
                    found = true;
                    break;
                }
            }
            for (Segment<K, V> segment22 : segments) {
                segment22.unlock();
            }
            return found;
        } catch (Throwable th) {
            for (Segment<K, V> segment222 : segments) {
                segment222.unlock();
            }
        }
    }

    public boolean contains(Object value) {
        return containsValue(value);
    }

    public V put(K key, V value) {
        if (value == null) {
            throw new NullPointerException();
        }
        int hash = hashOf(key);
        return segmentFor(hash).put(key, hash, value, false);
    }

    public V putIfAbsent(K key, V value) {
        if (value == null) {
            throw new NullPointerException();
        }
        int hash = hashOf(key);
        return segmentFor(hash).put(key, hash, value, true);
    }

    public void putAll(Map<? extends K, ? extends V> m) {
        for (Entry<? extends K, ? extends V> e : m.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    public V remove(Object key) {
        int hash = hashOf(key);
        return segmentFor(hash).remove(key, hash, null, false);
    }

    public boolean remove(Object key, Object value) {
        int hash = hashOf(key);
        if (value == null || segmentFor(hash).remove(key, hash, value, false) == null) {
            return false;
        }
        return true;
    }

    public boolean replace(K key, V oldValue, V newValue) {
        if (oldValue == null || newValue == null) {
            throw new NullPointerException();
        }
        int hash = hashOf(key);
        return segmentFor(hash).replace(key, hash, oldValue, newValue);
    }

    public V replace(K key, V value) {
        if (value == null) {
            throw new NullPointerException();
        }
        int hash = hashOf(key);
        return segmentFor(hash).replace(key, hash, value);
    }

    public void clear() {
        for (Segment<K, V> segment : this.segments) {
            segment.clear();
        }
    }

    public Set<K> keySet() {
        Set<K> ks = this.keySet;
        if (ks != null) {
            return ks;
        }
        ks = new KeySet();
        this.keySet = ks;
        return ks;
    }

    public Collection<V> values() {
        Collection<V> vs = this.values;
        if (vs != null) {
            return vs;
        }
        vs = new Values();
        this.values = vs;
        return vs;
    }

    public Set<Entry<K, V>> entrySet() {
        Set<Entry<K, V>> es = this.entrySet;
        if (es != null) {
            return es;
        }
        es = new EntrySet();
        this.entrySet = es;
        return es;
    }

    public Enumeration<K> keys() {
        return new KeyIterator();
    }

    public Enumeration<V> elements() {
        return new ValueIterator();
    }
}
