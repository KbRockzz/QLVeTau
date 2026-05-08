package com.trainstation.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.trainstation.dto.CreateHoaDonRequest;
import com.trainstation.dto.HoaDonDTO;
import com.trainstation.model.HoaDon;

public class JacksonHoaDonMapper implements HoaDonMapper {
    private static JacksonHoaDonMapper instance;
    private final ObjectMapper objectMapper;

    private JacksonHoaDonMapper() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public static synchronized JacksonHoaDonMapper getInstance() {
        if (instance == null) {
            instance = new JacksonHoaDonMapper();
        }
        return instance;
    }

    @Override
    public HoaDonDTO toDTO(HoaDon entity) {
        return objectMapper.convertValue(entity, HoaDonDTO.class);
    }

    @Override
    public HoaDon toEntity(HoaDonDTO dto) {
        return objectMapper.convertValue(dto, HoaDon.class);
    }

    @Override
    public HoaDon fromCreateRequest(CreateHoaDonRequest request, String generatedId) {
        HoaDon hoaDon = objectMapper.convertValue(request, HoaDon.class);
        hoaDon.setMaHoaDon(generatedId);
        return hoaDon;
    }
}
