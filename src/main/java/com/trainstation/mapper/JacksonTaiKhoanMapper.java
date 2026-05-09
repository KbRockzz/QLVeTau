package com.trainstation.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trainstation.dto.CreateTaiKhoanRequest;
import com.trainstation.dto.TaiKhoanDTO;
import com.trainstation.model.TaiKhoan;

public class JacksonTaiKhoanMapper implements TaiKhoanMapper {
    private static JacksonTaiKhoanMapper instance;
    private final ObjectMapper objectMapper;

    private JacksonTaiKhoanMapper() {
        this.objectMapper = new ObjectMapper();
    }

    public static synchronized JacksonTaiKhoanMapper getInstance() {
        if (instance == null) {
            instance = new JacksonTaiKhoanMapper();
        }
        return instance;
    }

    @Override
    public TaiKhoanDTO toDTO(TaiKhoan entity) {
        return objectMapper.convertValue(entity, TaiKhoanDTO.class);
    }

    @Override
    public TaiKhoan toEntity(TaiKhoanDTO dto) {
        return objectMapper.convertValue(dto, TaiKhoan.class);
    }

    @Override
    public TaiKhoan fromCreateRequest(CreateTaiKhoanRequest request, String generatedId) {
        TaiKhoan taiKhoan = objectMapper.convertValue(request, TaiKhoan.class);
        taiKhoan.setMaTK(generatedId);
        return taiKhoan;
    }
}
