package com.trainstation.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.trainstation.dto.CreateDauMayRequest;
import com.trainstation.dto.DauMayDTO;
import com.trainstation.model.DauMay;

public class JacksonDauMayMapper implements DauMayMapper {
    private static JacksonDauMayMapper instance;
    private final ObjectMapper objectMapper;

    private JacksonDauMayMapper() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public static synchronized JacksonDauMayMapper getInstance() {
        if (instance == null) {
            instance = new JacksonDauMayMapper();
        }
        return instance;
    }

    @Override
    public DauMayDTO toDTO(DauMay entity) {
        return objectMapper.convertValue(entity, DauMayDTO.class);
    }

    @Override
    public DauMay toEntity(DauMayDTO dto) {
        return objectMapper.convertValue(dto, DauMay.class);
    }

    @Override
    public DauMay fromCreateRequest(CreateDauMayRequest request, String generatedId) {
        DauMay dauMay = objectMapper.convertValue(request, DauMay.class);
        dauMay.setMaDauMay(generatedId);
        return dauMay;
    }
}
