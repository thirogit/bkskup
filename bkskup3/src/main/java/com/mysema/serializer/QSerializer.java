package com.mysema.serializer;

import com.mysema.query.types.Templates;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;


public class QSerializer extends SerializerBase<QSerializer> {

    private static DateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public QSerializer() {
        super(new Templates());
    }

    public void visitObject(Date dt)
    {
        append('\'' + dateFmt.format(dt) + '\'');
    }

    public void visitObject(Collection collection)
    {
        append("(");

        for (Iterator iterator = collection.iterator(); iterator.hasNext(); ) {
            Object object = iterator.next();
            visitConstant(object);
            if(iterator.hasNext()) append(",");
        }
        append(")");
    }
}