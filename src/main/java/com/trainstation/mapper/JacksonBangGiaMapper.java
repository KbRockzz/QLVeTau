package com.trainstation.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.trainstation.dto.BangGiaDTO;
import com.trainstation.dto.CreateBangGiaRequest;
import com.trainstation.model.BangGia;

public class JacksonBangGiaMapper implements BangGiaMapper {
    private static JacksonBangGiaMapper instance;
    private final ObjectMapper objectMapper;

    private JacksonBangGiaMapper() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public static synchronized JacksonBangGiaMapper getInstance() {
        if (instance == null) {
            instance = new JacksonBangGiaMapper();
        }
        return instance;
    }

    @Override
    public BangGiaDTO toDTO(BangGia entity) {
        return objectMapper.convertValue(entity, BangGiaDTO.class);
    }

    @Override
    public BangGia toEntity(BangGiaDTO dto) {
        return objectMapper.convertValue(dto, BangGia.class);
    }

    @Override
    public BangGia fromCreateRequest(CreateBangGiaRequest request, String generatedId) {
        BangGia bangGia = objectMapper.convertValue(request, BangGia.class);
        bangGia.setMaBangGia(generatedId);
        return bangGia;
    }
}
