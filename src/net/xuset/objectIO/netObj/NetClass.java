package net.xuset.objectIO.netObj;

import net.xuset.objectIO.markupMsg.MarkupMsg;

import java.util.List;


/**
 * Created by xuset on 7/24/14.
 */
public class NetClass implements NetObject {
    private final String id;

    private final NetField[] fields;

    private final FieldHandler.CompositeHandler fieldHandler =
            new FieldHandler.CompositeHandler();

    private final Class<?> srcClass;

    public NetClass(String id, Object src) {
        this.id = id;

        srcClass = src.getClass();
        fields = NetClassHelper.createDeclaredFields(srcClass, fieldHandler);
        setSrcObject(src);
    }

    public List<FieldHandler> getFieldHandlers() {
        return fieldHandler.getHandlers();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public MarkupMsg serializeToMsg() {
        MarkupMsg msg = new MarkupMsg();
        msg.setName(getId());
        for (NetField f : fields)
            msg.addNested(f.serializeToMsg());
        return msg;
    }

    @Override
    public void deserializeMsg(MarkupMsg msg) {
        for (MarkupMsg sub : msg.getNestedMsgs()) {
            NetObject obj = getNetObjById(sub.getName());
            obj.deserializeMsg(sub);
        }
    }

    @Override
    public boolean hasUpdates() {
        return false;
    }

    @Override
    public MarkupMsg serializeUpdates() {
        return null;
    }

    void setSrcObject(Object src) {
        if (!src.getClass().isAssignableFrom(srcClass))
            throw new IllegalArgumentException(
                    "src must be of the same type is the original");

        for (NetField f : fields)
            f.setSrcObject(src);
    }

    private NetObject getNetObjById(String id) {
        for (NetField f : fields) {
            if (f.getId().equals(id))
                return f;
        }

        throw new UnsupportedOperationException("'" + id + "' is not found");
    }
}
