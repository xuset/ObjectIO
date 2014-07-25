package net.xuset.objectIO.netObj;

import net.xuset.objectIO.markupMsg.MarkupMsg;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by xuset on 7/24/14.
 */
public abstract class FieldHandler {
    protected abstract boolean canHandle(Field field);

    protected  abstract void applyToField(
            Field field, Object src, MarkupMsg value)
            throws IllegalAccessException;

    protected MarkupMsg fieldToMsg(Field field, Object src, boolean update)
            throws IllegalAccessException {

        MarkupMsg msg = new MarkupMsg();
        msg.setContent(field.get(src).toString());
        msg.setName(field.getName());
        return msg;
    }

    private static final List<FieldHandler> initHandlers = Arrays.asList(
            new ShortHandler(),
            new IntHandler(),
            new LongHandler(),
            new FloatHandler(),
            new DoubleHandler(),
            new ByteHandler(),
            new BooleanHandler(),
            new CharHandler(),
            new StringHandler(),
            new NetObjectHandler()
    );


    static class ShortHandler extends FieldHandler {

        @Override
        protected boolean canHandle(Field field) {
            return field.getType().equals(short.class) ||
                    field.getType().equals(Short.class);
        }

        @Override
        protected void applyToField(Field field, Object src, MarkupMsg value)
                throws IllegalAccessException {
            field.set(src, Short.parseShort(value.getContent()));
        }
    }

    static class IntHandler extends FieldHandler {

        @Override
        protected boolean canHandle(Field field) {
            return field.getType().equals(int.class) ||
                    field.getType().equals(Integer.class);
        }

        @Override
        protected void applyToField(Field field, Object src, MarkupMsg value)
                throws IllegalAccessException {
            field.set(src, Integer.parseInt(value.getContent()));
        }
    }

    static class LongHandler extends FieldHandler {

        @Override
        protected boolean canHandle(Field field) {
            return field.getType().equals(long.class) ||
                    field.getType().equals(Long.class);
        }

        @Override
        protected void applyToField(Field field, Object src, MarkupMsg value)
                throws IllegalAccessException {
            field.set(src, Long.parseLong(value.getContent()));
        }
    }

    static class FloatHandler extends FieldHandler {

        @Override
        protected boolean canHandle(Field field) {
            return field.getType().equals(float.class) ||
                    field.getType().equals(Float.class);
        }

        @Override
        protected void applyToField(Field field, Object src, MarkupMsg value)
                throws IllegalAccessException {
            field.set(src, Float.parseFloat(value.getContent()));
        }
    }

    static class DoubleHandler extends FieldHandler {

        @Override
        protected boolean canHandle(Field field) {
            return field.getType().equals(double.class) ||
                    field.getType().equals(Double.class);
        }

        @Override
        protected void applyToField(Field field, Object src, MarkupMsg value)
                throws IllegalAccessException {
            field.set(src, Double.parseDouble(value.getContent()));
        }
    }

    static class BooleanHandler extends FieldHandler {

        @Override
        protected boolean canHandle(Field field) {
            return field.getType().equals(boolean.class) ||
                    field.getType().equals(Boolean.class);
        }

        @Override
        protected void applyToField(Field field, Object src, MarkupMsg value)
                throws IllegalAccessException {
            field.set(src, Boolean.parseBoolean(value.getContent()));
        }
    }

    static class ByteHandler extends FieldHandler {

        @Override
        protected boolean canHandle(Field field) {
            return field.getType().equals(byte.class) ||
                    field.getType().equals(Byte.class);
        }

        @Override
        protected void applyToField(Field field, Object src, MarkupMsg value)
                throws IllegalAccessException {
            field.set(src, Byte.parseByte(value.getContent()));
        }
    }

    static class CharHandler extends FieldHandler {

        @Override
        protected boolean canHandle(Field field) {
            return field.getType().equals(char.class) ||
                    field.getType().equals(Character.class);
        }

        @Override
        protected void applyToField(Field field, Object src, MarkupMsg value)
                throws IllegalAccessException {
            field.set(src, value.getContent().charAt(0));
        }
    }

    static class StringHandler extends FieldHandler {

        @Override
        protected boolean canHandle(Field field) {
            return field.getType().equals(String.class);
        }

        @Override
        protected void applyToField(Field field, Object src, MarkupMsg value)
                throws IllegalAccessException {
            field.set(src, value.getContent());
        }
    }

    static class NetObjectHandler extends FieldHandler {

        @Override
        protected boolean canHandle(Field field) {
            return field.getType().isAssignableFrom(NetObject.class);
        }

        @Override
        protected MarkupMsg fieldToMsg(Field field, Object src, boolean update)
                throws IllegalAccessException {
            if (update)
                return ((NetObject) field.get(src)).serializeToMsg();
            else
                return ((NetObject) field.get(src)).serializeUpdates();
        }

        @Override
        protected void applyToField(Field field, Object src, MarkupMsg value)
                throws IllegalAccessException {

            ((NetObject) field.get(src)).deserializeMsg(value);
        }
    }

    static class CompositeHandler extends FieldHandler {

        private final List<FieldHandler> handlers =
                new ArrayList<FieldHandler>();

        public CompositeHandler() {
            handlers.addAll(initHandlers);
        }

        public List<FieldHandler> getHandlers() {
            return handlers;
        }

        @Override
        protected boolean canHandle(Field field) {
            for (FieldHandler fh : handlers) {
                if (fh.canHandle(field))
                    return true;
            }
            return false;
        }

        @Override
        protected void applyToField(Field field, Object src, MarkupMsg value)
                throws IllegalAccessException {

            for (FieldHandler fh : handlers) {
                if (fh.canHandle(field)) {
                    fh.applyToField(field, src, value);
                    return;
                }
            }

            throw new UnsupportedOperationException(
                    field.getType().getName() + " is not supported");
        }

        @Override
        protected MarkupMsg fieldToMsg(Field field, Object src, boolean update)
                throws IllegalAccessException {

            for (FieldHandler fh : handlers) {
                if (fh.canHandle(field))
                    return fh.fieldToMsg(field, src, update);
            }

            throw new UnsupportedOperationException(
                    field.getType().getName() + " is not supported");
        }
    }

    /*static class ClassHandler extends FieldHandler {

        private Object oldValue;
        private final Field field;
        private final List<NetField> fields = new ArrayList<NetField>();

        public ClassHandler(Field field) {
            this.field = field;
        }

        @Override
        protected boolean canHandle(Field field) {
            return (field.equals(this.field));
        }

        @Override
        protected MarkupMsg fieldToMsg(Field f, Object src, boolean update)
                throws IllegalAccessException {

            return null;
        }

        @Override
        protected void applyToField(Field f, Object src, MarkupMsg value)
                throws IllegalAccessException {

            if (isValueOutdated(src))
                updateStructure(src);

        }

        private void  updateStructure(Object src) {
            fields.clear();
        }

        private boolean isValueOutdated(Object src) {
            Object o = getCurrentValue(src);
            return (o != oldValue && !o.equals(oldValue));
        }


        private Object getCurrentValue(Object src) {
            try {
                return field.get(src);
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }
    }*/
}
