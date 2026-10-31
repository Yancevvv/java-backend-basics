package org.example;

import java.util.Objects;

/**
 * Реализация HashMap с разрешением коллизий.
 *
 * @param <K> тип ключа
 * @param <V> тип значения
 */
public class MyHashMap<K, V> {

    private static final int INITIAL_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;

    private static class Node<K, V> {
        final int hash;
        final K key;
        V value;
        Node<K, V> next;

        Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    @SuppressWarnings("unchecked")
    private Node<K, V>[] table;
    private int size;
    private int threshold;

    @SuppressWarnings("unchecked")
    public MyHashMap() {
        this.table = (Node<K, V>[]) new Node[INITIAL_CAPACITY];
        this.threshold = (int) (INITIAL_CAPACITY * LOAD_FACTOR);
        this.size = 0;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private static int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    private int indexFor(int hash, int length) {
        return hash & (length - 1);
    }

    public V put(K key, V value) {
        int hash = hash(key);
        int index = indexFor(hash, table.length);

        for (Node<K, V> node = table[index]; node != null; node = node.next) {
            if (node.hash == hash && Objects.equals(node.key, key)) {
                V oldValue = node.value;
                node.value = value;
                return oldValue;
            }
        }

        table[index] = new Node<>(hash, key, value, table[index]);
        size++;

        if (size > threshold) {
            resize();
        }
        return null;
    }

    public V get(K key) {
        int hash = hash(key);
        int index = indexFor(hash, table.length);

        for (Node<K, V> node = table[index]; node != null; node = node.next) {
            if (node.hash == hash && Objects.equals(node.key, key)) {
                return node.value;
            }
        }
        return null;
    }

    public V remove(K key) {
        int hash = hash(key);
        int index = indexFor(hash, table.length);

        Node<K, V> prev = null;
        Node<K, V> current = table[index];

        while (current != null) {
            if (current.hash == hash && Objects.equals(current.key, key)) {
                V removedValue = current.value;
                if (prev == null) {
                    table[index] = current.next;
                } else {
                    prev.next = current.next;
                }
                size--;
                return removedValue;
            }
            prev = current;
            current = current.next;
        }
        return null;
    }

    public boolean containsKey(K key) {
        int hash = hash(key);
        int index = indexFor(hash, table.length);
        for (Node<K, V> node = table[index]; node != null; node = node.next) {
            if (node.hash == hash && Objects.equals(node.key, key)) {
                return true;
            }
        }
        return false;
    }

    public void clear() {
        for (int i = 0; i < table.length; i++) {
            table[i] = null;
        }
        size = 0;
    }

    /**
     * Удваиваем ёмкость и перераспределяем элементы.
     * Оптимизированная версия: сохраняет порядок элементов
     * в бакете и избегает повторного вычисления хеш-индекса.
     */
    @SuppressWarnings("unchecked")
    private void resize() {
        int oldCapacity = table.length;
        int newCapacity = oldCapacity << 1;
        Node<K, V>[] oldTable = table;
        Node<K, V>[] newTable = (Node<K, V>[]) new Node[newCapacity];

        for (int i = 0; i < oldCapacity; i++) {
            Node<K, V> node = oldTable[i];
            if (node != null) {
                if (node.next == null) {
                    newTable[node.hash & (newCapacity - 1)] = node;
                } else {
                    // lo (low) - элементы, которые остаются на старом индексе
                    // hi (high) - элементы, которые смещаются на oldCapacity
                    Node<K, V> loHead = null, loTail = null;
                    Node<K, V> hiHead = null, hiTail = null;
                    Node<K, V> next;

                    do {
                        next = node.next;
                        if ((node.hash & oldCapacity) == 0) {
                            if (loTail == null) loHead = node;
                            else loTail.next = node;
                            loTail = node;
                        } else {
                            if (hiTail == null) hiHead = node;
                            else hiTail.next = node;
                            hiTail = node;
                        }
                    } while ((node = next) != null);

                    if (loTail != null) {
                        loTail.next = null;
                        newTable[i] = loHead;
                    }
                    if (hiTail != null) {
                        hiTail.next = null;
                        newTable[i + oldCapacity] = hiHead;
                    }
                }
            }
        }

        table = newTable;
        threshold = (int) (newCapacity * LOAD_FACTOR);
    }
}