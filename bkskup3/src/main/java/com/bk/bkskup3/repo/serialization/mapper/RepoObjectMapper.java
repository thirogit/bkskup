package com.bk.bkskup3.repo.serialization.mapper;

import com.bk.bkskup3.model.EAN;
import com.bk.bkskup3.model.HentType;
import com.bk.bkskup3.model.IBAN;
import com.bk.bkskup3.repo.serialization.serializers.*;
import com.bk.bkskup3.repo.serialization.wire.JsonDateTime;
import com.bk.bkskup3.repo.serialization.wire.JsonDayDate;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 1/18/2015
 * Time: 11:16 PM
 */
public class RepoObjectMapper extends ObjectMapper {
    public RepoObjectMapper() {
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(IBAN.class,new IBANSerializer());
        simpleModule.addSerializer(EAN.class,new EANSerializer());
        simpleModule.addDeserializer(HentType.class,new HentTypeDeserializer());

        simpleModule.addSerializer(JsonDateTime.class,new JsonDateTimeSerializer());
        simpleModule.addDeserializer(JsonDateTime.class,new JsonDateTimeDeserializer());

        simpleModule.addSerializer(JsonDayDate.class,new JsonDayDateSerializer());
        simpleModule.addDeserializer(JsonDayDate.class,new JsonDayDateDeserializer());

        this.registerModule(simpleModule);
        this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
}
