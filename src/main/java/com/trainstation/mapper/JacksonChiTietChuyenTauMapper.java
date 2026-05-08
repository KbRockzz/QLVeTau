package com.trainstation.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trainstation.dto.ChiTietChuyenTauDTO;
import com.trainstation.dto.CreateChiTietChuyenTauRequest;
import com.trainstation.model.ChiTietChuyenTau;

public class JacksonChiTietChuyenTauMapper implements ChiTietChuyenTauMapper {
    private static JacksonChiTietChuyenTauMapper instance;
    private final ObjectMapper objectMapper;

    private JacksonChiTietChuyenTauMapper() {
        this.objectMapper = new ObjectMapper();
    }

    public static synchronized JacksonChiTietChuyenTauMapper getInstance() {
        if (instance == null) {
            instance = new JacksonChiTietChuyenTauMapper();
        }
        return instance;
    }

    @Override
    public ChiTietChuyenTauDTO toDTO(ChiTietChuyenTau entity) {
        return objectMapper.convertValue(entity, ChiTietChuyenTauDTO.class);
    }

    @Override
    public ChiTietChuyenTau toEntity(ChiTietChuyenTauDTO dto) {
        return objectMapper.convertValue(dto, ChiTietChuyenTau.class);
    }

    @Override
    public ChiTietChuyenTau fromCreateRequest(CreateChiTietChuyenTauRequest request) {
        return objectMapper.convertValue(request, ChiTietChuyenTau.class);
    }
}
