package net.xuset.objectIO.netObj;

import net.xuset.objectIO.markupMsg.MarkupMsg;
import net.xuset.objectIO.netObj.FieldHandler.BooleanHandler;
import net.xuset.objectIO.netObj.FieldHandler.ByteHandler;
import net.xuset.objectIO.netObj.FieldHandler.CharHandler;
import net.xuset.objectIO.netObj.FieldHandler.DoubleHandler;
import net.xuset.objectIO.netObj.FieldHandler.FloatHandler;
import net.xuset.objectIO.netObj.FieldHandler.IntHandler;
import net.xuset.objectIO.netObj.FieldHandler.LongHandler;
import net.xuset.objectIO.netObj.FieldHandler.NetObjectHandler;
import net.xuset.objectIO.netObj.FieldHandler.ShortHandler;
import net.xuset.objectIO.netObj.FieldHandler.StringHandler;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by xuset on 7/23/14.
 */
public class NetComp {

   /* private final List<FieldHandler> fieldHandlers =
            new ArrayList<FieldHandler>();

    private final String id;
    private final CompField[] fields;
    private final Class<?> srcClass;
    private Object srcObject;

    public NetComp(String id, Object o) {
        this.id = id;
        srcClass = o.getClass();
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
            if (f.getAnnotation(NetVar.class) != null)
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

    @Override
    public String getId() {
        return id;
    }



    private CompField getFieldByName(String name) {
        for (CompField f : fields) {
            if (f.getId().equals(name))
                return f;
        }

        throw new UnsupportedOperationException("Field with the name " +
                name + " does not exist.");
    }*/
}
