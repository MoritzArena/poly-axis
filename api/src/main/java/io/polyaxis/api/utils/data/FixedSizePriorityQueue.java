package io.polyaxis.api.utils.data;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/// priority queue of a fixed size.
///
/// @author github.com/MoritzArena
/// @date 2025/07/05
/// @since 1.0
public final class FixedSizePriorityQueue<T> {
    
    private final T[] elements;
    
    private final Comparator<T> comparator;

    private int size;

    @SuppressWarnings("unchecked")
    public FixedSizePriorityQueue(int capacity, Comparator<T> comparator) {
        elements = (T[]) new Object[capacity];
        size = 0;
        this.comparator = comparator;
    }
    
    /// Offer queue, if queue is full and offer element is not bigger
    /// than the first element in queue, offer element will be ignored.
    ///
    /// @param element new element.
    public void offer(T element) {
        if (size == elements.length) {
            if (comparator.compare(element, elements[0]) > 0) {
                elements[0] = element;
                siftDown();
            }
        } else {
            elements[size] = element;
            siftUp(size);
            size++;
        }
    }
    
    private void siftUp(int index) {
        while (index > 0) {
            int parentIndex = (index - 1) / 2;
            if (comparator.compare(elements[index], elements[parentIndex]) > 0) {
                break;
            }
            swap(index, parentIndex);
            index = parentIndex;
        }
    }
    
    private void siftDown() {
        int index = 0;
        while (index * 2 + 1 < size) {
            int leftChild = index * 2 + 1;
            int rightChild = index * 2 + 2;
            int minChildIndex = leftChild;
            if (rightChild < size && comparator.compare(elements[rightChild], elements[leftChild]) < 0) {
                minChildIndex = rightChild;
            }
            if (comparator.compare(elements[index], elements[minChildIndex]) < 0) {
                break;
            }
            swap(index, minChildIndex);
            index = minChildIndex;
        }
    }
    
    private void swap(int i, int j) {
        T temp = elements[i];
        elements[i] = elements[j];
        elements[j] = temp;
    }
    
    /// Transfer queue to list without order.
    ///
    /// @return list
    public List<T> toList() {
        return new LinkedList<>(Arrays.asList(elements).subList(0, size));
    }
}
