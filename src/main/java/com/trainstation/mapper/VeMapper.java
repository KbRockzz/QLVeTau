package com.trainstation.mapper;

import com.trainstation.dto.VeDTO;
import com.trainstation.dto.CreateVeRequest;
import com.trainstation.model.Ve;

public interface VeMapper {
    VeDTO toDTO(Ve entity);
    Ve toEntity(VeDTO dto);
    Ve fromCreateRequest(CreateVeRequest request, String generatedId);
}
