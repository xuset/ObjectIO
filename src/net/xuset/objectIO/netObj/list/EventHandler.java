package net.xuset.objectIO.netObj.list;

import net.xuset.objectIO.netObj.NetObject;

/**
 * Created by xuset on 7/31/14.
 */
interface EventHandler<T> {
    void onAdd(Node<T> node);
    void onRemove(Node<T> node);
}
