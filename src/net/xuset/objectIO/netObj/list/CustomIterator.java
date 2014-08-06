package net.xuset.objectIO.netObj.list;

import net.xuset.objectIO.netObj.NetObjFactory;

import java.util.ListIterator;

class CustomIterator<T> implements ListIterator<T> {
    private final NetObjFactory factory;
    private final ListIterator<Node<T>> iterator;
    private final EventHandler<T> eventHandler;
    private Node<T> lastNode = null;

    CustomIterator(ListIterator<Node<T>> iterator,
                           EventHandler<T> eventHandler,
                           NetObjFactory factory) {

        this.iterator = iterator;
        this.eventHandler = eventHandler;
        this.factory = factory;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public T next() {
        lastNode = iterator.next();
        return lastNode.element;
    }

    @Override
    public boolean hasPrevious() {
        return iterator.hasPrevious();
    }

    @Override
    public T previous() {
        lastNode = iterator.previous();
        return lastNode.element;
    }

    @Override
    public int nextIndex() {
        return iterator.nextIndex();
    }

    @Override
    public int previousIndex() {
        return iterator.previousIndex();
    }

    @Override
    public void remove() {
        iterator.remove();
        eventHandler.onRemove(lastNode);
    }

    @Override
    public void set(T t) {
        Node<T> newNode = new Node<T>(t, factory);
        iterator.set(newNode);
        eventHandler.onRemove(lastNode);
        eventHandler.onAdd(newNode);
    }

    @Override
    public void add(T t) {
        Node<T> newNode = new Node<T>(t, factory);
        iterator.add(newNode);
        eventHandler.onAdd(newNode);
    }
}
