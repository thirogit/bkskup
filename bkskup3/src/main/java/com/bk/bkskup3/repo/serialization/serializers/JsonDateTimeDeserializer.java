package com.bk.bkskup3.repo.serialization.serializers;

import com.bk.bkskup3.repo.serialization.wire.JsonDateTime;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.text.ParseException;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/30/2014
 * Time: 9:07 PM
 */
public class JsonDateTimeDeserializer extends JsonDeserializer<JsonDateTime> {

    @Override
    public JsonDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {

        try {
            return new JsonDateTime(JsonDateFormats.DATETIME_FORMAT.parse(jsonParser.getText()));
        } catch (ParseException e) {
            throw new IOException(e.getMessage());
        }
    }
}
