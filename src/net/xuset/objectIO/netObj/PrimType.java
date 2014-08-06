package net.xuset.objectIO.netObj;

import net.xuset.objectIO.markupMsg.MarkupMsg;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class PrimType {

    static final List<TypeHandler> initHandlers =
            Collections.unmodifiableList(Arrays.asList((TypeHandler)
                new IntHandler(),
                new StringHandler()
    ));

    static abstract class ToStringHandler implements TypeHandler {

        @Override
        public MarkupMsg serialize(Object src, boolean update) {
            MarkupMsg msg = new MarkupMsg();
            msg.setContent(src.toString());
            return msg;
        }
    }

    static class CompositeHandler implements TypeHandler {
        private final List<TypeHandler> handlers;

        CompositeHandler(List<TypeHandler> handlers) {
            this.handlers = handlers;
        }

        CompositeHandler() {
            this(initHandlers);
        }

        public TypeHandler getHandler(Class<?> srcClass) {
            for (TypeHandler th : handlers) {
                if (th.canHandle(srcClass))
                    return th;
            }

            throw new UnsupportedOperationException(
                    "'" + srcClass + "' is not supported");
        }

        @Override
        public boolean canHandle(Class<?> srcClass) {
            for (TypeHandler th : handlers) {
                if (th.canHandle(srcClass))
                    return true;
            }
            return false;
        }

        @Override
        public MarkupMsg serialize(Object src, boolean update) {
            for (TypeHandler th : handlers) {
                if (th.canHandle(src.getClass()))
                    return th.serialize(src, update);
            }

            throw new UnsupportedOperationException(
                    "'" + src.getClass() + "' is not supported");
        }

        @Override
        public Object deserialize(Object src, MarkupMsg raw) {
            for (TypeHandler th : handlers) {
                if (th.canHandle(src.getClass()))
                    return th.deserialize(src, raw);
            }

            throw new UnsupportedOperationException(
                    "'" + src.getClass() + "' is not supported");
        }
    }

    /*static class ShortHandler extends ToStringHandler {

        @Override
        public boolean canHandle(Class<?> srcClass) {
            return srcClass.equals(short.class) ||
                    srcClass.equals(Short.class);
        }

        @Override
        protected void applyToField(Field field, ISrcValue srcValue,
                                    MarkupMsg value) {
            srcValue.setSrc(Short.parseShort(value.getContent()));
        }
    }*/

    static class IntHandler extends ToStringHandler {

        @Override
        public boolean canHandle(Class<?> srcClass) {
            return srcClass.equals(int.class) ||
                    srcClass.equals(Integer.class);
        }

        @Override
        public Object deserialize(Object src, MarkupMsg raw) {
            return Integer.parseInt(raw.getContent());
        }
    }

    /*static class LongHandler extends ToStringHandler {

        @Override
        public boolean canHandle(Class<?> srcClass) {
            return field.getType().equals(long.class) ||
                    field.getType().equals(Long.class);
        }

        @Override
        protected void applyToField(Field field, ISrcValue srcValue,
                                    MarkupMsg value) {
            srcValue.setSrc(Long.parseLong(value.getContent()));
        }
    }

    static class FloatHandler extends ToStringHandler {

        @Override
        public boolean canHandle(Class<?> srcClass) {
            return field.getType().equals(float.class) ||
                    field.getType().equals(Float.class);
        }

        @Override
        protected void applyToField(Field field, ISrcValue srcValue,
                                    MarkupMsg value) {
            srcValue.setSrc(Float.parseFloat(value.getContent()));
        }
    }

    static class DoubleHandler extends ToStringHandler {

        @Override
        public boolean canHandle(Class<?> srcClass) {
            return field.getType().equals(double.class) ||
                    field.getType().equals(Double.class);
        }

        @Override
        protected void applyToField(Field field, ISrcValue srcValue,
                                    MarkupMsg value) {
            srcValue.setSrc(Double.parseDouble(value.getContent()));
        }
    }

    static class BooleanHandler extends ToStringHandler {

        @Override
        public boolean canHandle(Class<?> srcClass) {
            return field.getType().equals(boolean.class) ||
                    field.getType().equals(Boolean.class);
        }

        @Override
        protected void applyToField(Field field, ISrcValue srcValue,
                                    MarkupMsg value) {
            srcValue.setSrc(Boolean.parseBoolean(value.getContent()));
        }
    }

    static class ByteHandler extends ToStringHandler {

        @Override
        public boolean canHandle(Class<?> srcClass) {
            return field.getType().equals(byte.class) ||
                    field.getType().equals(Byte.class);
        }

        @Override
        protected void applyToField(Field field, ISrcValue srcValue,
                                    MarkupMsg value) {
            srcValue.setSrc(Byte.parseByte(value.getContent()));
        }
    }

    static class CharHandler extends ToStringHandler {

        @Override
        public boolean canHandle(Class<?> srcClass) {
            return field.getType().equals(char.class) ||
                    field.getType().equals(Character.class);
        }

        @Override
        protected void applyToField(Field field, ISrcValue srcValue,
                                    MarkupMsg value) {
            srcValue.setSrc(value.getContent().charAt(0));
        }
    }*/

    static class StringHandler extends ToStringHandler {

        @Override
        public boolean canHandle(Class<?> srcClass) {
            return srcClass.equals(String.class);
        }

        @Override
        public Object deserialize(Object src, MarkupMsg raw) {
            return raw.getContent();
        }
    }

    /*static class NetObjectHandler extends ToStringHandler {

        @Override
        public boolean canHandle(Class<?> srcClass) {
            return srcClass.isAssignableFrom(NetObject.class);
        }

        @Override
        public MarkupMsg serialize(Object src, boolean update) {
            if (update)
                return ((NetObject) src).serializeToMsg();
            else
                return ((NetObject) src).serializeUpdates();
        }

        @Override
        public Object deserialize(Object src, MarkupMsg raw) {
            ((NetObject) src).deserializeMsg(raw);
            return src;
        }
    }*/
}
