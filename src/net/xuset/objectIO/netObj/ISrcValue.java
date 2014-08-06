package net.xuset.objectIO.netObj;

import java.lang.reflect.Field;

interface ISrcValue {

    Object getSrc();
    void setSrc(Object src);

    static class SrcValueRoot implements ISrcValue {
        private final Object src;

        public SrcValueRoot(Object src) {
            this.src = src;
        }

        @Override
        public Object getSrc() {
            return src;
        }

        @Override
        public void setSrc(Object src) {
            throw new UnsupportedOperationException("Cannot change source");
        }
    }

    static class SrcValueChain implements ISrcValue {
        private final ISrcValue base;
        private final Field field;

        SrcValueChain(ISrcValue base, Field field) {
            this.base = base;
            this.field = field;
        }

        @Override
        public Object getSrc() {
            try {
                return field.get(base.getSrc());
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        public void setSrc(Object value) {
            try {
                field.set(base.getSrc(), value);
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }
    }
}
