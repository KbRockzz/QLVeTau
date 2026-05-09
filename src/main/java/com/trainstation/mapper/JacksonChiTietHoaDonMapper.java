package com.trainstation.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trainstation.dto.ChiTietHoaDonDTO;
import com.trainstation.dto.CreateChiTietHoaDonRequest;
import com.trainstation.model.ChiTietHoaDon;

public class JacksonChiTietHoaDonMapper implements ChiTietHoaDonMapper {
    private static JacksonChiTietHoaDonMapper instance;
    private final ObjectMapper objectMapper;

    private JacksonChiTietHoaDonMapper() {
        this.objectMapper = new ObjectMapper();
    }

    public static synchronized JacksonChiTietHoaDonMapper getInstance() {
        if (instance == null) {
            instance = new JacksonChiTietHoaDonMapper();
        }
        return instance;
    }

    @Override
    public ChiTietHoaDonDTO toDTO(ChiTietHoaDon entity) {
        return objectMapper.convertValue(entity, ChiTietHoaDonDTO.class);
    }

    @Override
    public ChiTietHoaDon toEntity(ChiTietHoaDonDTO dto) {
        return objectMapper.convertValue(dto, ChiTietHoaDon.class);
    }

    @Override
    public ChiTietHoaDon fromCreateRequest(CreateChiTietHoaDonRequest request) {
        return objectMapper.convertValue(request, ChiTietHoaDon.class);
    }
}
