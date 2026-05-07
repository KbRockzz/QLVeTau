package com.trainstation.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.trainstation.dto.CreateNhanVienRequest;
import com.trainstation.dto.NhanVienDTO;
import com.trainstation.model.NhanVien;

public class JacksonNhanVienMapper implements NhanVienMapper {
    private static JacksonNhanVienMapper instance;
    private final ObjectMapper objectMapper;

    private JacksonNhanVienMapper() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public static synchronized JacksonNhanVienMapper getInstance() {
        if (instance == null) {
            instance = new JacksonNhanVienMapper();
        }
        return instance;
    }

    @Override
    public NhanVienDTO toDTO(NhanVien entity) {
        return objectMapper.convertValue(entity, NhanVienDTO.class);
    }

    @Override
    public NhanVien toEntity(NhanVienDTO dto) {
        return objectMapper.convertValue(dto, NhanVien.class);
    }

    @Override
    public NhanVien fromCreateRequest(CreateNhanVienRequest request, String generatedId) {
        NhanVien nhanVien = objectMapper.convertValue(request, NhanVien.class);
        nhanVien.setMaNV(generatedId);
        return nhanVien;
    }
}
