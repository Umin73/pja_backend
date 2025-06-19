package com.project.PJA.common.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class ForceStringDeserializer extends JsonDeserializer<String> {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        try {
            JsonToken token = p.currentToken();

            // null 처리
            if (token == JsonToken.VALUE_NULL) {
                return null;
            }

            // 문자열인 경우 - 이미 JSON 문자열일 수 있음
            if (token == JsonToken.VALUE_STRING) {
                return p.getText();
            }

            // 나머지 모든 타입을 JSON 문자열로 변환
            try {
                Object value = p.readValueAsTree();
                return mapper.writeValueAsString(value);
            } catch (Exception e) {
                // 변환 실패시 문자열로 강제 변환 시도
                return p.getValueAsString();
            }

        } catch (Exception e) {
            // 모든 예외 상황에서 안전하게 문자열 반환
            try {
                return p.getValueAsString();
            } catch (Exception ex) {
                return ""; // 최후의 수단
            }
        }
        /*JsonToken token = p.currentToken();

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
        }*/
    }
}
