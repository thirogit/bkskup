package com.bk.bkskup3.repo.serialization.serializers;

import com.bk.bkskup3.model.IBAN;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/30/2014
 * Time: 9:07 PM
 */
public class IBANDeserializer extends JsonDeserializer<IBAN> {
    @Override
    public IBAN deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        return IBAN.fromString(jsonParser.getText());
    }
}
