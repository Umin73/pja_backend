package com.project.PJA.common.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.io.IOException;

public class ForceStringDeserializer extends JsonDeserializer<String> {
    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken token = p.currentToken();

        switch (token) {
            case VALUE_STRING:
                return p.getText();
            case VALUE_NUMBER_INT:
            case VALUE_NUMBER_FLOAT:
                return p.getNumberValue().toString();
            case VALUE_TRUE:
            case VALUE_FALSE:
                return Boolean.toString(p.getBooleanValue());
            case START_ARRAY:
            case START_OBJECT:
                return p.readValueAsTree().toString();
            case VALUE_NULL:
                return null;
            default:
                // 최신 버전 호환: 명시적 예외 처리
                throw JsonMappingException.from(p, "Unexpected token type: " + token);
        }
    }
}
