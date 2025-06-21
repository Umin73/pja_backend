package com.project.PJA.common.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class ForceStringDeserializer extends JsonDeserializer<String> {
    private final ObjectMapper mapper = new ObjectMapper();
    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken token = p.currentToken();

        switch (token) {
            case VALUE_STRING:
                String text = p.getText();
                if ((text.startsWith("{") && text.endsWith("}")) || (text.startsWith("[") && text.endsWith("]"))) {
                    try {
                        Object parsed = mapper.readValue(text, Object.class);
                        return mapper.writeValueAsString(parsed); // 다시 정제된 JSON 문자열로 반환
                    } catch (Exception e) {
                        // 그냥 텍스트일 수도 있으니 예외 무시하고 원본 반환
                    }
                }
                return text;
                //return p.getText();
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
