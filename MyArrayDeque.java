import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
public class MyArrayDeque<E> implements MyDeque<E> {

    private E[] data;
    private int head; // index of first element
    private int tail; // index one past last element
    private int size;

    @SuppressWarnings("unchecked")
    public MyArrayDeque() {
        data = (E[]) new Object[4]; // power of two
        head = 0;
        tail = 0;
        size = 0;
    }

    //TASK 2: ADDFIRST
    //PRE:  accepts an item to add 
    //POST: if list is full, resize
    //      calculate head index (using circular logic)
    //      add item to location of head
    //      update size

    //SIMILAR:  boolean offerFirst(E e);
    //SIMILAR:  void push(E e);    
    public void addFirst(E item) {
        if(size == data.length){
            resize();
        }
        head = (head -1 + data.length) % data.length;
        data[head] = item;
        size++;
    }

    //PRE:  accepts new element
    //POST: if list is full, resize
    //      set tail location to new eleemnt
    //      update tail & size
    //SIMILAR:  boolean offerLast(E e);
    public void addLast(E item) {
        if (size == data.length) {
            resize();
        }

        data[tail] = item;
        tail = (tail + 1) % data.length;
        size++;
    }
    //implements Deque for add
    public boolean offerFirst(E e) {
        addFirst(e);
        return true;
    }

    public boolean offerLast(E e) {
        addLast(e);
        return true;
    }
    
    //PRE:  none
    //POST: if list is empty - throw exception
    //      else save the first position (to return)
    //      update head & size, return saved item
    //SIMILAR: E pollFirst();
    //SIMILAR: E pop(); 
    public E removeFirst() {
        if (isEmpty()) {
            throw new IllegalStateException("Deque is empty");
        }

        E item = (E) data[head];
        data[head] = null; // avoid memory leak
        head = (head + 1) % data.length;
        size--;

        return item;
    }
    
    //TASK 3: REMOVELAST
    //PRE: none
    //POST: if list is not empty, reset the tail 
    //      (using circular queue logic)
    //      save item to return, decrement size
    //      return saved item

    //SIMILAR: E pollLast();
    //tail points 1 past the last element
    public E removeLast(){
        if (isEmpty()) {
            throw new IllegalStateException("Deque is empty");
        }

        tail = (tail - 1 + data.length) % data.length;
        E item = data[tail];
        data[tail] = null;
        size--;
        return item;
    }
    //implements Deque for remove
    public E pollFirst() {
        return isEmpty() ? null : removeFirst();
    }

    public E pollLast() {
        return isEmpty() ? null : removeLast();
    }
    
    public E pop() {
        return removeFirst();
    }

    //PRE:  none
    //POST: if empty, throws illegal state
    //      else returns the first element
    // Peek front
    //E peekFirst();
    public E getFirst() {
        if (isEmpty()) {
            throw new IllegalStateException("Deque is empty");
        }
        return (E)data[head];
    }
        

    //PRE:  none
    //POST: if empty, throws illegal state
    //      else returns the last element
    // Peek back
    //SIMILAR: E peekLast();
    public E getLast() {
        if (isEmpty()) {
            throw new IllegalStateException("Deque is empty");
        }
        int lastIndex = (tail -1 + data.length) % data.length;
        return data[lastIndex];
    }
    //implements Deque for get
    public E peekFirst() {
        return isEmpty() ? null : getFirst();
    }

    public E peekLast() {
        return isEmpty() ? null : getLast();
    }

    public void push(E e) {
        addFirst(e);
    }

    @SuppressWarnings("unchecked")
    //TASK 1
    //PRE:  existing array is full
    //POST: allocate new array with 2*the current capacity
    //      for items up to size (current capacity)      
    //         set location [0] to the value at head
    //         until all items are copied
    //      reset head & rear positions
    //      reset data to new array

    private void resize() {
        E[] newData = (E[]) new Object[data.length * 2];

        for (int i = 0; i < size; i++){
            int index = (head + i) % data.length;
            newData[i] = data[index];
        }

        data = newData;
        head = 0;
        tail = size;
    }

    //PRE:  none
    //POST: returns the size of the arraydeque
    public int size() {
        return size;
    }

    //PRE:  none
    //POST: returns true if the arraydeque is empty
    //      false if not
    public boolean isEmpty() {
        return size == 0;
    }

    //PRE:  none
    //POST: returns a string representing the
    //      arraydeque contents
    @Override
    public String toString() {
        if (isEmpty()) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for (int i = 0; i < size; i++) {
            int index = (head + i) % data.length;
            sb.append(data[index]);

            if (i < size - 1) {
                sb.append(", ");
            }
        }

        sb.append("]");
        return sb.toString();
    }
    //implements Queue
    @Override
    public boolean add(E e) {
        addLast(e);
        return true;
    }

    @Override
    public boolean offer(E e) {
        return offerLast(e);
    }

    @Override
    public E remove() {
        return removeFirst();
    }

    @Override
    public E poll() {
        return pollFirst();
    }

    @Override
    public E element() {
        return getFirst();
    }

    @Override
    public E peek() {
        return peekFirst();
    }


    // Checks whether the deque contains an element
    @Override
    public boolean contains(Object o) {
        for (int i = 0; i < size; i++) {
            int index = (head + i) % data.length;

            if (java.util.Objects.equals(data[index], o)) {
                return true;
            }
        }

        return false;
    }

    // Returns an iterator that traverses from front to back
    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private int cursor = 0;
            private int lastReturned = -1;

            @Override
            public boolean hasNext() {
                return cursor < size;
            }

            @Override
            public E next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                int index = (head + cursor) % data.length;
                E item = data[index];

                lastReturned = cursor;
                cursor++;

                return item;
            }

            @Override
            public void remove() {
                if (lastReturned == -1) {
                    throw new IllegalStateException();
                }

                removeAt(lastReturned);
                cursor = lastReturned;
                lastReturned = -1;
            }
        };
    }

    // Returns all elements in an Object array
    @Override
    public Object[] toArray() {
        Object[] result = new Object[size];

        for (int i = 0; i < size; i++) {
            int index = (head + i) % data.length;
            result[i] = data[index];
        }

        return result;
    }

    // Returns all elements in an array of the requested type
    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        if (a.length < size) {
            a = (T[]) java.lang.reflect.Array.newInstance(
                a.getClass().getComponentType(), size
            );
        }

        for (int i = 0; i < size; i++) {
            int index = (head + i) % data.length;
            a[i] = (T) data[index];
        }

        if (a.length > size) {
            a[size] = null;
        }

        return a;
    }

    // Removes the first matching element
    @Override
    public boolean remove(Object o) {
        for (int i = 0; i < size; i++) {
            int index = (head + i) % data.length;

            if (java.util.Objects.equals(data[index], o)) {
                removeAt(i);
                return true;
            }
        }

        return false;
    }

    // Checks whether all elements of another collection exist here
    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object item : c) {
            if (!contains(item)) {
                return false;
            }
        }

        return true;
    }

    // Adds every element from another collection
    @Override
    @SuppressWarnings("unchecked")
    public boolean addAll(Collection<? extends E> c) {
        Object[] elements = c.toArray();
        boolean changed = false;

        for (Object item : elements) {
            addLast((E) item);
            changed = true;
        }

        return changed;
    }

    // Removes all elements that also exist in another collection
    @Override
    public boolean removeAll(Collection<?> c) {
        boolean changed = false;
        Iterator<E> it = iterator();

        while (it.hasNext()) {
            if (c.contains(it.next())) {
                it.remove();
                changed = true;
            }
        }

        return changed;
    }

    // Keeps only elements that exist in another collection
    @Override
    public boolean retainAll(Collection<?> c) {
        boolean changed = false;
        Iterator<E> it = iterator();

        while (it.hasNext()) {
            if (!c.contains(it.next())) {
                it.remove();
                changed = true;
            }
        }

        return changed;
    }

    // Removes every element from the deque
    @Override
    public void clear() {
        for (int i = 0; i < size; i++) {
            int index = (head + i) % data.length;
            data[index] = null;
        }

        head = 0;
        tail = 0;
        size = 0;
    }

    // Helper method: removes an element at a logical position
    private void removeAt(int logicalIndex) {
        if (logicalIndex < 0 || logicalIndex >= size) {
            throw new IndexOutOfBoundsException();
        }

        if (logicalIndex < size / 2) {
            // Shift elements before the removed element forward
            for (int i = logicalIndex; i > 0; i--) {
                int current = (head + i) % data.length;
                int previous = (head + i - 1) % data.length;
                data[current] = data[previous];
            }

            data[head] = null;
            head = (head + 1) % data.length;
        } else {
            // Shift elements after the removed element backward
            for (int i = logicalIndex; i < size - 1; i++) {
                int current = (head + i) % data.length;
                int next = (head + i + 1) % data.length;
                data[current] = data[next];
            }

            tail = (tail - 1 + data.length) % data.length;
            data[tail] = null;
        }

        size--;
    }


}