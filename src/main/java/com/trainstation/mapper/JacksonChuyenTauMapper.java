package com.trainstation.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.trainstation.dto.ChuyenTauDTO;
import com.trainstation.dto.CreateChuyenTauRequest;
import com.trainstation.model.ChuyenTau;

public class JacksonChuyenTauMapper implements ChuyenTauMapper {
    private static JacksonChuyenTauMapper instance;
    private final ObjectMapper objectMapper;

    private JacksonChuyenTauMapper() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public static synchronized JacksonChuyenTauMapper getInstance() {
        if (instance == null) {
            instance = new JacksonChuyenTauMapper();
        }
        return instance;
    }

    @Override
    public ChuyenTauDTO toDTO(ChuyenTau entity) {
        return objectMapper.convertValue(entity, ChuyenTauDTO.class);
    }

    @Override
    public ChuyenTau toEntity(ChuyenTauDTO dto) {
        return objectMapper.convertValue(dto, ChuyenTau.class);
    }

    @Override
    public ChuyenTau fromCreateRequest(CreateChuyenTauRequest request, String generatedId) {
        ChuyenTau ct = objectMapper.convertValue(request, ChuyenTau.class);
        ct.setMaChuyen(generatedId);
        return ct;
    }
}
