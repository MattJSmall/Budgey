package mjsma5.budgey;

/**
 * Created by Matts on 1/05/2017.
 */


import java.util.LinkedList;

class Queue<E> {
    private LinkedList<E> list = new LinkedList<E>();
    public void enqueue(E item) {
        list.addLast(item);
    }
    public E dequeue() {
        return list.poll();
    }
    public boolean hasItems() {
        return !list.isEmpty();
    }
    public int size() {
        return list.size();
    }
    public void addItems(Queue<? extends E> q) {
        while (q.hasItems()) list.addLast(q.dequeue());
    }
}