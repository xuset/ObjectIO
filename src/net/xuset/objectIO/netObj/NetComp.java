package net.xuset.objectIO.netObj;

import net.xuset.objectIO.markupMsg.MarkupMsg;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by xuset on 7/23/14.
 */
public class NetComp extends AbstractNetObject {
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

    private final List<FieldHandler> fieldHandlers =
            new ArrayList<FieldHandler>();

    private final Object srcObject;
    private final CompField[] fields;

    public NetComp(String id, Object o) {
        super(id);
        srcObject = o;
        fieldHandlers.addAll(initHandlers);

        ArrayList<CompField> tmpFields = new ArrayList<CompField>();
        Class<?> tmpClass = o.getClass();
        while (tmpClass != null) {
            addCompFields(tmpClass, srcObject, tmpFields);
            tmpClass = tmpClass.getSuperclass();

        }
        fields = tmpFields.toArray(new CompField[tmpFields.size()]);
    }

    private void addCompFields(Class<?> clazz, Object srcObject,
                                      List<CompField> tmpFields)  {
        for (Field f : clazz.getDeclaredFields()) {
            if (f.getAnnotation(NetPrim.class) != null)
                constructCompField(f, srcObject, tmpFields);
        }
    }

    private void constructCompField(Field field, Object srcObject,
                                    List<CompField> tmpFields) {

        tmpFields.add(new CompField(field, srcObject, fieldHandlers));
    }

    public List<FieldHandler> getFieldHandlers() {
        return fieldHandlers;
    }

    @Override
    public MarkupMsg serializeToMsg() {
        MarkupMsg msg = new MarkupMsg();
        msg.setName(getId());

        for (CompField f : fields)
            msg.addNested(f.serializeToMsg());
        return msg;
    }

    @Override
    public void deserializeMsg(MarkupMsg msg) {
        for (MarkupMsg nested : msg.getNestedMsgs()) {
            CompField f = getFieldByName(nested.getName());
            f.deserializeMsg(nested);
        }
    }

    @Override
    public boolean hasUpdates() {
        for (CompField f : fields) {
            if (f.hasUpdates())
                return true;
        }
        return false;
    }

    @Override
    public MarkupMsg serializeUpdates() {
        MarkupMsg msg = new MarkupMsg();
        msg.setName(getId());

        for (CompField f : fields)  {
            if (f.hasUpdates())
                msg.addNested(f.serializeUpdates());
        }

        return msg;
    }

    public static abstract class FieldHandler {
        protected abstract boolean canHandle(Field field);

        protected  abstract void applyToField(
                Field field, Object src, MarkupMsg value)
                throws IllegalAccessException;

        protected MarkupMsg fieldToMsg(Field field, Object src, boolean update)
                throws IllegalAccessException {

            MarkupMsg msg = new MarkupMsg();
            msg.setContent(field.get(src).toString());
            return msg;
        }
    }

    private CompField getFieldByName(String name) {
        for (CompField f : fields) {
            if (f.getId().equals(name))
                return f;
        }

        throw new UnsupportedOperationException("Field with the name " +
                name + " does not exist.");
    }

    private static class CompField implements NetObject {
        private final List<FieldHandler> fieldHandlers;
        private final Field field;
        private final Object srcObject;
        private Object value;

        public CompField(Field field, Object srcObject,
                         List<FieldHandler> fieldHandlers) {

            this.fieldHandlers = fieldHandlers;
            this.field = field;
            this.srcObject = srcObject;

            field.setAccessible(true);
            value = getCurrentValue();
        }


        @Override
        public String getId() {
            return field.getName();
        }

        @Override
        public MarkupMsg serializeToMsg() {
            return toMsg(false);
        }

        @Override
        public void deserializeMsg(MarkupMsg msg) {
            FieldHandler fh = getFieldHandler();
            try {
                fh.applyToField(field, srcObject, msg);
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }

        @Override
        public boolean hasUpdates() {
            Object current = getCurrentValue();
            return current != value && !current.equals(value);
        }

        @Override
        public MarkupMsg serializeUpdates() {
            return toMsg(true);
        }

        private MarkupMsg toMsg(boolean update) {
            MarkupMsg msg = getMsgValue(update);
            msg.setName(getId());

            value = getCurrentValue();
            return msg;
        }

        private Object getCurrentValue() {
            try {
                return field.get(srcObject);
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }

        private FieldHandler getFieldHandler() {
            for (FieldHandler fh : fieldHandlers) {
                if (fh.canHandle(field))
                    return fh;
            }

            throw new UnsupportedOperationException(
                    field.getType() + " is not supported");
        }

        private MarkupMsg getMsgValue(boolean update) {
            FieldHandler fh = getFieldHandler();
            try {
                return fh.fieldToMsg(field, srcObject, update);
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private static class ShortHandler extends FieldHandler {

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

    private static class IntHandler extends FieldHandler {

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

    private static class LongHandler extends FieldHandler {

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

    private static class FloatHandler extends FieldHandler {

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

    private static class DoubleHandler extends FieldHandler {

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

    private static class BooleanHandler extends FieldHandler {

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

    private static class ByteHandler extends FieldHandler {

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

    private static class CharHandler extends FieldHandler {

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

    private static class StringHandler extends FieldHandler {

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

    private static class NetObjectHandler extends FieldHandler {

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
}
