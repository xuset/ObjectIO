package net.xuset.objectIO.netObj;

import net.xuset.objectIO.markupMsg.MarkupMsg;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by xuset on 7/24/14.
 */
public class NetClass implements NetPrototype {

    private final String id;
    private final ISrcValue srcValue;
    private final List<NetObject> fields = new ArrayList<NetObject>();
    private final NetObjFactory factory = new NetObjFactory();

    private boolean needsRestructure = true;

    public NetClass(String id, Object obj) {
        this(id, new ISrcValue.SrcValueRoot(obj));
    }

    NetClass(String id, ISrcValue srcValue) {
        this.id = id;
        this.srcValue = srcValue;
        restructureClass();
    }

    static NetClass createPrototype() {
        return new NetClass("", new ISrcValue.SrcValueRoot(null));
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public MarkupMsg serializeToMsg() {
        checkForRestructure();
        MarkupMsg msg = new MarkupMsg();
        msg.setName(getId());
        for (NetObject f : fields)
            msg.addNested(f.serializeToMsg());
        return msg;
    }

    @Override
    public void deserializeMsg(MarkupMsg msg) {
        checkForRestructure();
        for (MarkupMsg sub : msg.getNestedMsgs()) {
            NetObject obj = getNetObjById(sub.getName());
            if (obj != null)
                obj.deserializeMsg(sub);
        }
    }

    @Override
    public boolean hasUpdates() {
       checkForRestructure();
        for (NetObject f : fields) {
            if (f.hasUpdates())
                return true;
        }
        return false;
    }

    @Override
    public MarkupMsg serializeUpdates() {
        checkForRestructure();
        MarkupMsg msg = new MarkupMsg();
        msg.setName(getId());
        for (NetObject f : fields) {
            if (f.hasUpdates())
                msg.addNested(f.serializeUpdates());
        }
        return msg;
    }

    @Override
    public boolean canHandle(Class<?> srcClass) {
        return srcClass.getAnnotation(NetVar.class) != null;
    }

    @Override
    public NetObject prototype(String id, ISrcValue srcValue, Class<?> srcClass) {
        return new NetClass(id, srcValue);
    }

    private NetObject getNetObjById(String id) {
        for (NetObject f : fields) {
            if (f.getId().equals(id))
                return f;
        }

        System.err.println("'" + id + "' is not found");
        return null;
    }

    private void checkForRestructure() {
        Object src = srcValue.getSrc();
        if (needsRestructure ^ src == null)
            restructureClass();

    }

    private void restructureClass() {
        Object src = srcValue.getSrc();
        needsRestructure = src == null;
        fields.clear();

        if (src != null)
            addAllFields();
    }

    private void addAllFields() {
        Class<?> current = srcValue.getSrc().getClass();
        while (current != null) {
            addDeclaredFields(current);
            current = current.getSuperclass();
        }
    }

    private void addDeclaredFields(Class<?> clazz) {
        for (Field f : clazz.getDeclaredFields()) {
            NetVar nv = f.getAnnotation(NetVar.class);
            if (nv == null)
                continue;
            f.setAccessible(true);
            ISrcValue fieldGetter = new ISrcValue.SrcValueChain(srcValue, f);
            fields.add(factory.create(f, fieldGetter));
        }
    }
}
