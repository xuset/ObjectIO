package net.xuset.objectIO.netObj.list;

import net.xuset.objectIO.netObj.NetObjFactory;
import net.xuset.objectIO.netObj.NetObject;

import java.util.Random;

/**
 * Created by xuset on 7/31/14.
 */
class Node <T> {
    private static final Random random = new Random();

    final T element;
    final NetObject netObject;

    Node(T element, NetObjFactory factory) {
        this.element = element;
        String id = String.valueOf(random.nextLong());
        netObject = factory.create(id, element);
    }

    Node(T element, NetObject netObject) {
        this.element = element;
        this.netObject = netObject;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Node<?>)
            return ((Node<?>) o).element.equals(element);
        else
            return element.equals(o);
    }

    @Override
    public int hashCode() {
        return element.hashCode();
    }

    @Override
    public String toString() {
        return element.toString();
    }
}
