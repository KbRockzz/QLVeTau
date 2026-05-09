package com.trainstation.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trainstation.dto.CreateLoaiGheRequest;
import com.trainstation.dto.LoaiGheDTO;
import com.trainstation.model.LoaiGhe;

public class JacksonLoaiGheMapper implements LoaiGheMapper {
    private static JacksonLoaiGheMapper instance;
    private final ObjectMapper objectMapper;

    private JacksonLoaiGheMapper() {
        this.objectMapper = new ObjectMapper();
    }

    public static synchronized JacksonLoaiGheMapper getInstance() {
        if (instance == null) {
            instance = new JacksonLoaiGheMapper();
        }
        return instance;
    }

    @Override
    public LoaiGheDTO toDTO(LoaiGhe entity) {
        return objectMapper.convertValue(entity, LoaiGheDTO.class);
    }

    @Override
    public LoaiGhe toEntity(LoaiGheDTO dto) {
        return objectMapper.convertValue(dto, LoaiGhe.class);
    }

    @Override
    public LoaiGhe fromCreateRequest(CreateLoaiGheRequest request) {
        return objectMapper.convertValue(request, LoaiGhe.class);
    }
}
