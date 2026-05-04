package com.trainstation.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.trainstation.dto.VeDTO;
import com.trainstation.dto.CreateVeRequest;
import com.trainstation.model.Ve;

public class JacksonVeMapper implements VeMapper {
    private static JacksonVeMapper instance;
    private final ObjectMapper objectMapper;

    private JacksonVeMapper() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public static synchronized JacksonVeMapper getInstance() {
        if (instance == null) {
            instance = new JacksonVeMapper();
        }
        return instance;
    }

    @Override
    public VeDTO toDTO(Ve entity) {
        return objectMapper.convertValue(entity, VeDTO.class);
    }

    @Override
    public Ve toEntity(VeDTO dto) {
        return objectMapper.convertValue(dto, Ve.class);
    }

    @Override
    public Ve fromCreateRequest(CreateVeRequest request, String generatedId) {
        Ve ve = objectMapper.convertValue(request, Ve.class);
        ve.setMaVe(generatedId);
        return ve;
    }
}
