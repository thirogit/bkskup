package com.bk.bkskup3.repo.serialization.serializers;

import com.bk.bkskup3.model.CowSex;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class CowSexDeserializer extends StdDeserializer<CowSex> {

    public CowSexDeserializer() {
        this(null);
    }

    public CowSexDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public CowSex deserialize(
            JsonParser jsonparser, DeserializationContext context)
            throws IOException {

        return CowSex.fromInt(jsonparser.getIntValue());

    }
}