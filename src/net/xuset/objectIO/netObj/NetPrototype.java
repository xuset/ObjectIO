package net.xuset.objectIO.netObj;

import java.lang.reflect.Field;

/**
 * Created by xuset on 7/29/14.
 */
interface NetPrototype extends NetObject {
    boolean canHandle(Class<?> srcClass);

    NetObject prototype(String id, ISrcValue srcValue, Class<?> srcClass);
}
