package com.bk.bkskup3.repo.serialization.serializers;

import com.bk.bkskup3.model.IBAN;
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
public class IBANSerializer extends JsonSerializer<IBAN> {

    @Override
    public void serialize(IBAN value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeString(value.toString());
    }
}
