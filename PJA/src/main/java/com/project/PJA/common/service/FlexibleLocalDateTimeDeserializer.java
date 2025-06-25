package com.project.PJA.common.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FlexibleLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, IOException {
        String value = p.getText();

        if(value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            // ISO 포맷 "yyyy-MM-dd'T'HH:mm:ss"
            return LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e1) {
            try {
                // 공백 포맷 "yyyy-MM-dd HH:mm:ss"
                DateTimeFormatter spaceFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                return LocalDateTime.parse(value, spaceFormatter);
            } catch (Exception e2) {
                throw new RuntimeException("날짜 파싱 실패: " + value);
            }
        }
    }
}