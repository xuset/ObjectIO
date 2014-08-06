package net.xuset.objectIO.netObj;

import net.xuset.objectIO.markupMsg.MarkupMsg;

import java.lang.reflect.Field;

/**
 * Created by xuset on 7/26/14.
 */
public interface TypeHandler {
    boolean canHandle(Class<?> srcClass);
    MarkupMsg serialize(Object src, boolean update);
    Object deserialize(Object src, MarkupMsg raw);
}
