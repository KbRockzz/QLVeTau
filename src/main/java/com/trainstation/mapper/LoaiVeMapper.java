package com.trainstation.mapper;

import com.trainstation.dto.CreateLoaiVeRequest;
import com.trainstation.dto.LoaiVeDTO;
import com.trainstation.model.LoaiVe;

public interface LoaiVeMapper {
    LoaiVeDTO toDTO(LoaiVe entity);
    LoaiVe toEntity(LoaiVeDTO dto);
    LoaiVe fromCreateRequest(CreateLoaiVeRequest request);
}
