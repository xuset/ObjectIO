package net.xuset.objectIO.netObj;

import net.xuset.objectIO.markupMsg.MarkupMsg;

import java.lang.reflect.Field;

/**
 * Created by xuset on 7/27/14.
 */
public class NetComp implements NetPrototype{

    private final String id;
    private final ISrcValue srcGetter;
    private final TypeHandler typeHandler;
    private Object oldValue;

    public NetComp(String id, ISrcValue srcGetter, TypeHandler typeHandler) {
        this.id = id;
        this.srcGetter = srcGetter;
        this.typeHandler = typeHandler;
    }

    static NetComp createPrototype() {
        return new NetComp("", null, new PrimType.CompositeHandler());
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public MarkupMsg serializeToMsg() {
        return serialize(false);
    }

    @Override
    public void deserializeMsg(MarkupMsg msg) {
        srcGetter.setSrc(typeHandler.deserialize(srcGetter.getSrc(), msg));
        oldValue = srcGetter.getSrc();
    }

    @Override
    public boolean hasUpdates() {
        Object current = srcGetter.getSrc();

        if (current == oldValue)
            return false;
        if (current == null ^ oldValue == null)
            return true;
        return !current.equals(oldValue);
    }

    @Override
    public MarkupMsg serializeUpdates() {
        return serialize(true);
    }

    @Override
    public boolean canHandle(Class<?> srcClass) {
        return typeHandler.canHandle(srcClass);
    }

    @Override
    public NetObject prototype(String id, ISrcValue srcValue, Class<?> srcClass) {
        TypeHandler th = typeHandler;
        if (th instanceof PrimType.CompositeHandler)
            th = ((PrimType.CompositeHandler) th).getHandler(srcClass);
        return new NetComp(id, srcValue, th);
    }

    private MarkupMsg serialize(boolean asUpdate) {
        oldValue = srcGetter.getSrc();
        MarkupMsg msg = typeHandler.serialize(srcGetter.getSrc(), asUpdate);
        msg.setName(getId());
        return msg;
    }
}
