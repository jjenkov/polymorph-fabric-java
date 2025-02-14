package com.plmph.pde.obj;

import com.plmph.pde.PdeFieldTypes;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PdeObjectWriterImpl<T> implements PdeObjectWriter<T> {

    private Class<T> targetClass = null;
    private final List<PdeObjectFieldWriter> fieldWriters = new ArrayList<>();

    public PdeObjectWriterImpl(Class<T> targetClass) {
        this.targetClass = targetClass;
        addFields();
    }

    private void addFields() {
        List<Field> allFields = Arrays.asList(targetClass.getDeclaredFields());

        allFields.forEach(field -> {
            try {
                addFieldWriter(field.getName());
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void addFieldWriter(String fieldName) throws NoSuchFieldException {
        Field field = this.targetClass.getField(fieldName);

        Class fieldType = field.getType();
        if (fieldType.equals(Boolean.class)) {
            this.fieldWriters.add(new PdeBooleanObjFieldWriter(field));
            return;
        }
        if (fieldType.equals(Integer.class)) {
            this.fieldWriters.add(new PdeIntObjFieldWriter(field));
            return;
        }
        if (fieldType.equals(Float.class)) {
            this.fieldWriters.add(new PdeFloatObjFieldWriter(field));
            return;
        }
        if (fieldType.equals(String.class)) {
            this.fieldWriters.add(new PdeUtf8ObjFieldWriter(field));
        }

    }

    public int writeKeysAndValues(byte[] dest, int offset, T object, int lengthByteCount) throws IllegalAccessException {
        dest[offset++] = (byte) (0xFF & PdeFieldTypes.OBJECT_NULL + lengthByteCount);

        int lengthByteOffset = offset;
        offset += lengthByteCount; //reserve the length bytes before starting to write object fields.

        for (int i = 0; i < this.fieldWriters.size(); i++) {
            offset += fieldWriters.get(i).writeKeyAndValue(dest, offset, object);
        }

        int bodyLength = offset - lengthByteOffset - lengthByteCount;
        for (int i = 0, n = lengthByteCount * 8; i < n; i += 8) {
            dest[offset++] = (byte) (0xFF & (bodyLength >> i));
        }

        return 1 + lengthByteCount + bodyLength;
    }
}
