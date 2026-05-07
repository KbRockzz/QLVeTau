package com.trainstation.mapper;

import com.trainstation.dto.CreateDauMayRequest;
import com.trainstation.dto.DauMayDTO;
import com.trainstation.model.DauMay;

public interface DauMayMapper {
    DauMayDTO toDTO(DauMay entity);
    DauMay toEntity(DauMayDTO dto);
    DauMay fromCreateRequest(CreateDauMayRequest request, String generatedId);
}
