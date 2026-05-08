package com.trainstation.mapper;

import com.trainstation.dto.CreateLoaiNVRequest;
import com.trainstation.dto.LoaiNVDTO;
import com.trainstation.model.LoaiNV;

public interface LoaiNVMapper {
    LoaiNVDTO toDTO(LoaiNV entity);
    LoaiNV toEntity(LoaiNVDTO dto);
    LoaiNV fromCreateRequest(CreateLoaiNVRequest request);
}
