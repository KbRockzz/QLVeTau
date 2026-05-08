package com.trainstation.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trainstation.dto.CreateLoaiVeRequest;
import com.trainstation.dto.LoaiVeDTO;
import com.trainstation.model.LoaiVe;

public class JacksonLoaiVeMapper implements LoaiVeMapper {
    private static JacksonLoaiVeMapper instance;
    private final ObjectMapper objectMapper;

    private JacksonLoaiVeMapper() {
        this.objectMapper = new ObjectMapper();
    }

    public static synchronized JacksonLoaiVeMapper getInstance() {
        if (instance == null) {
            instance = new JacksonLoaiVeMapper();
        }
        return instance;
    }

    @Override
    public LoaiVeDTO toDTO(LoaiVe entity) {
        return objectMapper.convertValue(entity, LoaiVeDTO.class);
    }

    @Override
    public LoaiVe toEntity(LoaiVeDTO dto) {
        return objectMapper.convertValue(dto, LoaiVe.class);
    }

    @Override
    public LoaiVe fromCreateRequest(CreateLoaiVeRequest request) {
        return objectMapper.convertValue(request, LoaiVe.class);
    }
}
