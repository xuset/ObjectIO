package net.xuset.objectIO.netObj;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuset on 7/24/14.
 */
class NetClassHelper {

    public static NetField[] createDeclaredFields(Class<?> src, FieldHandler fh) {
        List<NetField> tmpFields = new ArrayList<NetField>();

        Class<?> current = src;
        while (current != null) {
            addDeclaredFields(current, fh, tmpFields);
            current = current.getSuperclass();
        }

        return tmpFields.toArray(new NetField[tmpFields.size()]);
    }

    private static void addDeclaredFields(Class<?> clazz, FieldHandler fh,
                                          List<NetField> tmpFields) {

        for (Field f : clazz.getDeclaredFields()) {
            if (isNetField(f))
                tmpFields.add(new NetField(f, fh));
            if (isNetField(f) || isNetClass(f))
                f.setAccessible(true);
        }
    }

    private static boolean isNetField(Field f) {
        NetVar nv = f.getAnnotation(NetVar.class);
        Class<?> type = f.getType();
        return nv != null &&
                (!nv.recursive());
    }

    private static boolean isNetClass(Field f) {
        NetVar nv = f.getAnnotation(NetVar.class);
        Class<?> type = f.getType();
        return nv != null &&
                (nv.recursive()) && false;
    }
}
