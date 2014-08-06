package net.xuset.objectIO.netObj.list;

import net.xuset.objectIO.markupMsg.MarkupMsg;
import net.xuset.objectIO.netObj.NetObjFactory;
import net.xuset.objectIO.netObj.NetObject;

import java.util.*;

/* TODO
    when clear() is called it should remove the individual elements and
    add the individual commands to the commands list.
 */

public class NetArrayList<T> implements NetList<T> {

    private final String id;
    private final List<Node<T>> nodes;
    private final ElementFactory<T> elementFactory;
    private final EventHandler<T> eventHandler;
    private final List<MarkupMsg> commands = new ArrayList<MarkupMsg>();
    private final NetObjFactory netObjFactory = new NetObjFactory();

    public NetArrayList(String id, Class<T> elementClass) {
        this(id, new ReflectFactory<T>(elementClass));
    }

    public NetArrayList(String id, ElementFactory<T> elementFactory) {
        this.id = id;
        this.elementFactory = elementFactory;
        eventHandler = new CustomEventHandler<T>();
        nodes = new ArrayList<Node<T>>();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public MarkupMsg serializeToMsg() {
        commands.clear();
        for (Node<T> n : nodes)
            eventHandler.onAdd(n);

        MarkupMsg master = createMasterMsg(true);
        commands.clear();
        return master;
    }

    @Override
    public void deserializeMsg(MarkupMsg msg) {
        handleMasterMsg(msg);
    }

    @Override
    public boolean hasUpdates() {
        if (!commands.isEmpty())
            return true;

        for (Node<T> node : nodes) {
            if (node.netObject.hasUpdates())
                return true;
        }

        return false;
    }

    @Override
    public MarkupMsg serializeUpdates() {
        MarkupMsg master = createMasterMsg(false);
        MarkupMsg modMsg = master.getNested("mod");

        for (Node<T> n : nodes) {
            if (n.netObject.hasUpdates())
                modMsg.addNested(n.netObject.serializeUpdates());
        }
        commands.clear();
        return master;
    }

    @Override
    public int size() {
        return nodes.size();
    }

    @Override
    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return nodes.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return createIterator(0);
    }

    @Override
    public Object[] toArray() {
        return toArray(new Object[size()]);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return addAll(0, c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        ArrayList<Node<T>> tmp = new ArrayList<Node<T>>(c.size());
        for (T element : c)
            tmp.add(new Node<T>(element, netObjFactory));
        boolean returnValue = nodes.addAll(index, tmp);
        for (Node<T> node : tmp)
            eventHandler.onAdd(node);
        return returnValue;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean listChanged = false;
        for (Iterator<T> it = iterator(); it.hasNext(); ) {
            T e = it.next();
            if (!c.contains(e)) {
                it.remove();
                listChanged = true;
            }
        }
        return listChanged;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean someTrue = false;
        for (Object e : c)
            someTrue = remove(e) || someTrue;
        return someTrue;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        boolean allTrue = true;
        for (Object o : c)
            allTrue = contains(o) && allTrue;
        return allTrue && !c.isEmpty();
    }

    @Override
    public T[] toArray(Object[] a) {
        if (a.length < size())
            a = new Object[size()];

        Iterator<T> it = iterator();
        for (int i = 0; i < a.length; i++) {
            if (it.hasNext())
                a[i] = it.next();
            else
                a[i] = null;
        }
        return (T[]) a;
    }

    @Override
    public void clear() {
        while (!isEmpty())
            remove(size());
    }

    @Override
    public T get(int index) {
        return nodes.get(index).element;
    }

    @Override
    public T set(int index, T element) {
        Node<T> node = new Node<T>(element, netObjFactory);
        Node<T> old =  nodes.set(index, node);

        eventHandler.onRemove(node);
        eventHandler.onAdd(node);
        return old.element;
    }

    @Override
    public void add(int index, T element) {
        Node<T> node = new Node<T>(element, netObjFactory);
        nodes.add(index, node);
        eventHandler.onAdd(node);
    }

    @Override
    public T remove(int index) {
        Node<T> n = nodes.remove(index);
        eventHandler.onRemove(n);
        return n.element;
    }

    @Override
    public int indexOf(Object o) {
        return nodes.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return nodes.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return createIterator(0);
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return createIterator(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException(
                "It is just not feasible to support this operation. sorry.");
    }

    @Override
    public boolean add(T e) {
        Node<T> n = new Node<T>(e, netObjFactory);
        boolean retVal =  nodes.add(n);
        eventHandler.onAdd(n);
        return retVal;
    }

    @Override
    public boolean remove(Object o) {
        Node<T> n = getNodeByElement(o);
        if (n == null)
            return false;

        nodes.remove(o);
        eventHandler.onRemove(n);
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[ ");

        for (Iterator<T> it = iterator(); it.hasNext(); ) {
            builder.append(it.next());
            if (it.hasNext())
                builder.append(", ");
        }
        builder.append(" ]");
        return builder.toString();
    }

    private class CustomEventHandler<T> implements EventHandler<T> {

        @Override
        public void onAdd(Node<T> node) {
            MarkupMsg msg = new MarkupMsg();
            msg.setName("add");
            msg.addNested(node.netObject.serializeToMsg());
            commands.add(msg);
        }

        @Override
        public void onRemove(Node<T> node) {
            MarkupMsg msg = new MarkupMsg();
            msg.setName("rm");
            msg.setAttribute("id", node.netObject.getId());
            commands.add(msg);
        }
    }

    private ListIterator<T> createIterator(int index) {
        return new CustomIterator<T>(nodes.listIterator(index),
                eventHandler, netObjFactory);
    }

    private Node<T> getNodeByElement(Object o) {
        for (Node<T> n : nodes) {
            if (n.element == o)
                return n;
        }
        return null;
    }

    private Node<T> getNodeById(String id) {
        for (Node<T> n : nodes) {
            if (n.netObject.getId().equals(id))
                return n;
        }

        return null;
    }

    private MarkupMsg createMasterMsg(boolean clear) {
        MarkupMsg master = new MarkupMsg();
        master.setName(getId());
        master.setAttribute("clear", clear);

        MarkupMsg msgMod = new MarkupMsg();
        msgMod.setName("mod");

        MarkupMsg msgCmd = new MarkupMsg();
        msgCmd.setName("cmd");
        msgCmd.getNestedMsgs().addAll(commands);

        master.addNested(msgMod);
        master.addNested(msgCmd);
        return master;
    }

    private void handleMasterMsg(MarkupMsg master) {
        if (master.getAttribute("clear").getBool() == true)
            nodes.clear();

        MarkupMsg cmdMsg = master.getNested("cmd");
        MarkupMsg modMsg = master.getNested("mod");

        handleCmdMsg(cmdMsg);
        handleModMsg(modMsg);
    }

    private void handleModMsg(MarkupMsg msg) {
        for (MarkupMsg sub : msg.getNestedMsgs()) {
            Node<T> node = getNodeById(sub.getName());
            if (node != null)
                node.netObject.deserializeMsg(sub);
            else
                System.err.println("'" + sub.getName() + "' not found");
        }
    }

    private void handleCmdMsg(MarkupMsg cmd) {
        for (MarkupMsg sub : cmd.getNestedMsgs()) {
            if (sub.getName().equals("add")) {
                MarkupMsg ind = sub.getNestedMsgs().get(0);
                handleAddCmd(ind);
            } else if(sub.getName().equals("rm")) {
                handleRmCmd(sub);
            } else {
                System.err.println("Received invalid msg");
            }
        }
    }

    private void handleAddCmd(MarkupMsg cmd) {
        T element = elementFactory.create();
        NetObject netObject = netObjFactory.create(cmd.getName(), element);
        netObject.deserializeMsg(cmd);
        Node<T> node = new Node<T>(element, netObject);
        nodes.add(node);
    }

    private void handleRmCmd(MarkupMsg cmd) {
        String id = cmd.getAttribute("id").getString();
        Node<T> node = getNodeById(id);
        nodes.remove(node);
    }


}
