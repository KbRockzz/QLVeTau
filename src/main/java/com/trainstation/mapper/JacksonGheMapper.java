package com.trainstation.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trainstation.dto.CreateGheRequest;
import com.trainstation.dto.GheDTO;
import com.trainstation.model.Ghe;

public class JacksonGheMapper implements GheMapper {
    private static JacksonGheMapper instance;
    private final ObjectMapper objectMapper;

    private JacksonGheMapper() {
        this.objectMapper = new ObjectMapper();
    }

    public static synchronized JacksonGheMapper getInstance() {
        if (instance == null) {
            instance = new JacksonGheMapper();
        }
        return instance;
    }

    @Override
    public GheDTO toDTO(Ghe entity) {
        return objectMapper.convertValue(entity, GheDTO.class);
    }

    @Override
    public Ghe toEntity(GheDTO dto) {
        return objectMapper.convertValue(dto, Ghe.class);
    }

    @Override
    public Ghe fromCreateRequest(CreateGheRequest request, String generatedId) {
        Ghe ghe = objectMapper.convertValue(request, Ghe.class);
        ghe.setMaGhe(generatedId);
        return ghe;
    }
}
