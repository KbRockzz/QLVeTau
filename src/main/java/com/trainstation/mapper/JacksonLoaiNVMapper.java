package com.trainstation.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trainstation.dto.CreateLoaiNVRequest;
import com.trainstation.dto.LoaiNVDTO;
import com.trainstation.model.LoaiNV;

public class JacksonLoaiNVMapper implements LoaiNVMapper {
    private static JacksonLoaiNVMapper instance;
    private final ObjectMapper objectMapper;

    private JacksonLoaiNVMapper() {
        this.objectMapper = new ObjectMapper();
    }

    public static synchronized JacksonLoaiNVMapper getInstance() {
        if (instance == null) {
            instance = new JacksonLoaiNVMapper();
        }
        return instance;
    }

    @Override
    public LoaiNVDTO toDTO(LoaiNV entity) {
        return objectMapper.convertValue(entity, LoaiNVDTO.class);
    }

    @Override
    public LoaiNV toEntity(LoaiNVDTO dto) {
        return objectMapper.convertValue(dto, LoaiNV.class);
    }

    @Override
    public LoaiNV fromCreateRequest(CreateLoaiNVRequest request) {
        return objectMapper.convertValue(request, LoaiNV.class);
    }
}
