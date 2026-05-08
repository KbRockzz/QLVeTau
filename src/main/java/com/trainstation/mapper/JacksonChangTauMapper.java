package com.trainstation.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trainstation.dto.ChangTauDTO;
import com.trainstation.dto.CreateChangTauRequest;
import com.trainstation.model.ChangTau;

public class JacksonChangTauMapper implements ChangTauMapper {
    private static JacksonChangTauMapper instance;
    private final ObjectMapper objectMapper;

    private JacksonChangTauMapper() {
        this.objectMapper = new ObjectMapper();
    }

    public static synchronized JacksonChangTauMapper getInstance() {
        if (instance == null) {
            instance = new JacksonChangTauMapper();
        }
        return instance;
    }

    @Override
    public ChangTauDTO toDTO(ChangTau entity) {
        return objectMapper.convertValue(entity, ChangTauDTO.class);
    }

    @Override
    public ChangTau toEntity(ChangTauDTO dto) {
        return objectMapper.convertValue(dto, ChangTau.class);
    }

    @Override
    public ChangTau fromCreateRequest(CreateChangTauRequest request) {
        return objectMapper.convertValue(request, ChangTau.class);
    }
}
