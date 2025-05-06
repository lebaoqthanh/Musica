package assign11;

import java.util.Arrays;

/**
 * This class represents a better dynamic array of AudioEvents, doubling the
 * length of the backing array when more space is needed and never shrinking.
 *
 * @author Prof. Parker, Prof. Heisler, and Thanh Le
 * @version 11/7/2024
 * @assign07 DynamicArray
 */
public class BetterDynamicArray<T> {

    private T[] elements; // the backing array
    private int elementCount; // the number of elements

    // Constructor with generic type array
    @SuppressWarnings("unchecked")
    public BetterDynamicArray() {
        elements = (T[]) new Object[10]; // Create an array of type T
        elementCount = 0;
    }

    public void add(T value) {
        insert(elementCount, value);
    }

    public void insert(int index, T value) {
        if (index < 0 || index > elementCount) {
            throw new IndexOutOfBoundsException();
        }

        // If there are no free spaces in the backing array, expand.
        if (elementCount == elements.length) {
            doubleBackingArray();
        }

        // Shift elements to the right to make space
        for (int i = elementCount; i > index; i--) {
            elements[i] = elements[i - 1];
        }

        elements[index] = value;
        elementCount++;
    }

    private void doubleBackingArray() {
        T[] largerArray = (T[]) new Object[elements.length * 2];
        for (int i = 0; i < elements.length; i++) {
            largerArray[i] = elements[i];
        }
        elements = largerArray;
    }

    public T get(int index) {
        if (index < 0 || index >= elementCount) {
            throw new IndexOutOfBoundsException();
        }
        return elements[index];
    }

    public int size() {
        return elementCount;
    }

    public void set(int index, T value) {
        if (index < 0 || index >= elementCount) {
            throw new IndexOutOfBoundsException();
        }
        elements[index] = value;
    }

    public void remove(int index) {
        if (index < 0 || index >= elementCount) {
            throw new IndexOutOfBoundsException();
        }
        for (int i = index; i < elementCount - 1; i++) {
            elements[i] = elements[i + 1];
        }
        elements[elementCount - 1] = null;
        elementCount--;
    }

    public void remove(T value) {
        if (value == null) {
            for (int i = 0; i < elementCount; i++) {
                if (elements[i] == null) {
                    remove(i);
                    return;
                }
            }
        } else {
            for (int i = 0; i < elementCount; i++) {
                if (value.equals(elements[i])) {
                    remove(i);
                    return;
                }
            }
        }
    }

    public void clear() {
        elementCount = 0;
        for (int i=0; i<elementCount; i++) {
            remove(elements[i]);
        }
    }

    public void sort() {
        Arrays.sort(elements, 0, elementCount);
    }

    public String toString() {
        String result = "[";
        if (size() > 0) {
            result += get(0);
        }
        for (int i = 1; i < size(); i++) {
            result += ", " + get(i);
        }
        return result + "] backing array length: " + elements.length;
    }
}