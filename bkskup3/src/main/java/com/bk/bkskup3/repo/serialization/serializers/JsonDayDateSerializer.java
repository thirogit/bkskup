package com.bk.bkskup3.repo.serialization.serializers;

import com.bk.bkskup3.repo.serialization.wire.JsonDayDate;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/30/2014
 * Time: 9:07 PM
 */
public class JsonDayDateSerializer extends JsonSerializer<JsonDayDate> {

    @Override
    public void serialize(JsonDayDate value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeString(JsonDateFormats.DAY_DATE_FORMAT.format(value.getDate()));
    }
}
