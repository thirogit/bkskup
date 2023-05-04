package com.bk.bkskup3.repo.serialization.serializers;

import com.bk.bkskup3.model.HentType;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.common.base.Strings;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/16/2015
 * Time: 8:59 PM
 */
public class HentTypeDeserializer extends JsonDeserializer<HentType> {
    @Override
    public HentType deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String hentTypeStr = jsonParser.getText();

        if(Strings.isNullOrEmpty(hentTypeStr))
            return null;

        HentType result = HentType.fromString(hentTypeStr);
        if(result == null)
            result = HentType.fromValue(hentTypeStr);

        if(result == null)
            throw new IOException(hentTypeStr + " is no a valid value for hent type");

        return result;
    }


}
