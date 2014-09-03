package net.xuset.objectIO.netObj;

import net.xuset.objectIO.markupMsg.MarkupMsg;

/**
 * Created by xuset on 8/1/14.
 */
class NetObjWrapper implements NetPrototype {
    private final ISrcValue srcValue;

    NetObjWrapper(ISrcValue srcValue) {
        this.srcValue = srcValue;
    }

    static NetObjWrapper createPrototype() {
        return new NetObjWrapper(null);
    }

    @Override
    public String getId() {
        return ((NetObject) srcValue.getSrc()).getId();
    }

    @Override
    public MarkupMsg serializeToMsg() {
        return ((NetObject) srcValue.getSrc()).serializeToMsg();
    }

    @Override
    public void deserializeMsg(MarkupMsg msg) {
        ((NetObject) srcValue.getSrc()).deserializeMsg(msg);
    }

    @Override
    public boolean hasUpdates() {
        return ((NetObject) srcValue.getSrc()).hasUpdates();
    }

    @Override
    public MarkupMsg serializeUpdates() {
        return ((NetObject) srcValue.getSrc()).serializeUpdates();
    }

    @Override
    public boolean canHandle(Class<?> srcClass) {
        return NetObject.class.isAssignableFrom(srcClass);
    }

    @Override
    public NetObject prototype(String id, ISrcValue srcValue, Class<?> srcClass) {
        return new NetObjWrapper(srcValue);
    }
}
