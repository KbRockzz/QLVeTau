package com.trainstation.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trainstation.dto.KhachHangDTO;
import com.trainstation.dto.CreateKhachHangRequest;
import com.trainstation.model.KhachHang;

public class JacksonKhachHangMapper implements KhachHangMapper {
    private static JacksonKhachHangMapper instance;
    private final ObjectMapper objectMapper;

    private JacksonKhachHangMapper() {
        this.objectMapper = new ObjectMapper();
    }

    public static synchronized JacksonKhachHangMapper getInstance() {
        if (instance == null) {
            instance = new JacksonKhachHangMapper();
        }
        return instance;
    }

    @Override
    public KhachHangDTO toDTO(KhachHang entity) {
        return objectMapper.convertValue(entity, KhachHangDTO.class);
    }

    @Override
    public KhachHang toEntity(KhachHangDTO dto) {
        return objectMapper.convertValue(dto, KhachHang.class);
    }

    @Override
    public KhachHang fromCreateRequest(CreateKhachHangRequest request, String generatedId) {
        KhachHang kh = objectMapper.convertValue(request, KhachHang.class);
        kh.setMaKhachHang(generatedId);
        return kh;
    }
}
