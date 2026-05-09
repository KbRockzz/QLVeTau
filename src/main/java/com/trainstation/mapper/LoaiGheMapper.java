package com.trainstation.mapper;

import com.trainstation.dto.CreateLoaiGheRequest;
import com.trainstation.dto.LoaiGheDTO;
import com.trainstation.model.LoaiGhe;

public interface LoaiGheMapper {
    LoaiGheDTO toDTO(LoaiGhe entity);
    LoaiGhe toEntity(LoaiGheDTO dto);
    LoaiGhe fromCreateRequest(CreateLoaiGheRequest request);
}
