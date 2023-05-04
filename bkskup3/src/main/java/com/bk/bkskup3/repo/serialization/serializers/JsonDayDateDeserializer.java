package com.bk.bkskup3.repo.serialization.serializers;

import com.bk.bkskup3.repo.serialization.wire.JsonDayDate;
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
public class JsonDayDateDeserializer extends JsonDeserializer<JsonDayDate> {



    @Override
    public JsonDayDate deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {

        try {
            return new JsonDayDate(JsonDateFormats.DAY_DATE_FORMAT.parse(jsonParser.getText()));
        } catch (ParseException e) {
            throw new IOException(e.getMessage());
        }
    }
}
