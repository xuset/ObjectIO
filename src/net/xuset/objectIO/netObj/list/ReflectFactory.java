package net.xuset.objectIO.netObj.list;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by xuset on 7/31/14.
 */
public class ReflectFactory <T>implements ElementFactory<T>{

    private final Constructor<T> constructor;

    public ReflectFactory(Class<T> elementClass) {
        try {
            constructor = elementClass.getConstructor(new Class<?>[]{});
        } catch (NoSuchMethodException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public T create() {
        try {
            return constructor.newInstance(new Object[] { });
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
