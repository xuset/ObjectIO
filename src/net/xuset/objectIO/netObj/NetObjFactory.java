package net.xuset.objectIO.netObj;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by xuset on 7/30/14.
 */
public class NetObjFactory {

    private static final List<NetPrototype> prototypeList =
            Collections.unmodifiableList(Arrays.asList(
                NetObjWrapper.createPrototype(),
                NetComp.createPrototype(),
                NetClass.createPrototype()
    ));

    public NetObject create(String id, Object obj) {
        ISrcValue srcValue = new ISrcValue.SrcValueRoot(obj);
        return createFromPrototype(id, srcValue, obj.getClass());
    }

    public NetObject create(Field field, ISrcValue srcValue) {
        String id = field.getName();

        NetVar nv = field.getAnnotation(NetVar.class);
        if (nv != null && !nv.id().equals(""))
            id = nv.id();
        return createFromPrototype(id, srcValue, field.getType());
    }

    protected NetObject createFromPrototype(String id, ISrcValue srcValue,
                                           Class<?> srcClass) {

        return getPrototype(srcClass).prototype(id, srcValue, srcClass);
    }

    private NetPrototype getPrototype(Class<?> srcClass) {
        for (NetPrototype pt : prototypeList) {
            if (pt.canHandle(srcClass))
                return pt;
        }

        throw new UnsupportedOperationException("'" + srcClass.getName() +
                "' is an unsupported type");
    }
}
