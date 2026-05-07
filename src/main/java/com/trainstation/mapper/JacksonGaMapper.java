package com.trainstation.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trainstation.dto.CreateGaRequest;
import com.trainstation.dto.GaDTO;
import com.trainstation.model.Ga;

public class JacksonGaMapper implements GaMapper {
    private static JacksonGaMapper instance;
    private final ObjectMapper objectMapper;

    private JacksonGaMapper() {
        this.objectMapper = new ObjectMapper();
    }

    public static synchronized JacksonGaMapper getInstance() {
        if (instance == null) {
            instance = new JacksonGaMapper();
        }
        return instance;
    }

    @Override
    public GaDTO toDTO(Ga entity) {
        return objectMapper.convertValue(entity, GaDTO.class);
    }

    @Override
    public Ga toEntity(GaDTO dto) {
        return objectMapper.convertValue(dto, Ga.class);
    }

    @Override
    public Ga fromCreateRequest(CreateGaRequest request, String generatedId) {
        Ga ga = objectMapper.convertValue(request, Ga.class);
        ga.setMaGa(generatedId);
        return ga;
    }
}
