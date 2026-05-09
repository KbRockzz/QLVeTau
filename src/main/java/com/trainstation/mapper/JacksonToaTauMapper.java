package com.trainstation.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trainstation.dto.CreateToaTauRequest;
import com.trainstation.dto.ToaTauDTO;
import com.trainstation.model.ToaTau;

public class JacksonToaTauMapper implements ToaTauMapper {
    private static JacksonToaTauMapper instance;
    private final ObjectMapper objectMapper;

    private JacksonToaTauMapper() {
        this.objectMapper = new ObjectMapper();
    }

    public static synchronized JacksonToaTauMapper getInstance() {
        if (instance == null) {
            instance = new JacksonToaTauMapper();
        }
        return instance;
    }

    @Override
    public ToaTauDTO toDTO(ToaTau entity) {
        return objectMapper.convertValue(entity, ToaTauDTO.class);
    }

    @Override
    public ToaTau toEntity(ToaTauDTO dto) {
        return objectMapper.convertValue(dto, ToaTau.class);
    }

    @Override
    public ToaTau fromCreateRequest(CreateToaTauRequest request, String generatedId) {
        ToaTau toa = objectMapper.convertValue(request, ToaTau.class);
        toa.setMaToa(generatedId);
        return toa;
    }
}
