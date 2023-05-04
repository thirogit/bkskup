package com.bk.bkskup3.repo.serialization.serializers;

import com.bk.bkskup3.model.EAN;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/30/2014
 * Time: 9:05 PM
 */
public class EANSerializer extends JsonSerializer<EAN> {

    @Override
    public void serialize(EAN value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeString(value.toString());
    }
}
