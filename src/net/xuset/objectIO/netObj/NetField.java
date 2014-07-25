package net.xuset.objectIO.netObj;

import net.xuset.objectIO.markupMsg.MarkupMsg;

import java.lang.reflect.Field;

/**
 * Created by xuset on 7/24/14.
 */
public class NetField implements NetObject {

    private final FieldHandler fieldHandler;
    private final Field field;
    private Object oldValue;
    private Object src;

    public NetField(Field field, FieldHandler fieldHandler) {
        this.field = field;
        this.fieldHandler = fieldHandler;
    }

    @Override
    public String getId() {
        return field.getName();
    }

    @Override
    public MarkupMsg serializeToMsg() {
        return serialize(false);
    }

    @Override
    public void deserializeMsg(MarkupMsg msg) {
        try {
            fieldHandler.applyToField(field, src, msg);
            oldValue = getCurrentValue();
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean hasUpdates() {
        return false;
    }

    @Override
    public MarkupMsg serializeUpdates() {
        return serialize(true);
    }

    void setSrcObject(Object src) {
        this.src = src;
    }

    private Object getCurrentValue() {
        try {
            Object value = field.get(src);
            return value != oldValue && !value.equals(oldValue);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

    private MarkupMsg serialize(boolean asUpdate) {
        try {
            oldValue = getCurrentValue();
            MarkupMsg msg = fieldHandler.fieldToMsg(field, src, asUpdate);
            return msg;
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }
}
