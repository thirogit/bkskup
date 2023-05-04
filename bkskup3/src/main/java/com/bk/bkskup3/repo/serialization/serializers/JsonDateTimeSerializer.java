package com.bk.bkskup3.repo.serialization.serializers;

import com.bk.bkskup3.repo.serialization.wire.JsonDateTime;
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
public class JsonDateTimeSerializer extends JsonSerializer<JsonDateTime> {

    @Override
    public void serialize(JsonDateTime value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeString(JsonDateFormats.DATETIME_FORMAT.format(value.getDate()));
    }
}
