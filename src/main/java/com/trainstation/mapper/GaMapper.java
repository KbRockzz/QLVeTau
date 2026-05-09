package com.trainstation.mapper;

import com.trainstation.dto.CreateGaRequest;
import com.trainstation.dto.GaDTO;
import com.trainstation.model.Ga;

public interface GaMapper {
    GaDTO toDTO(Ga entity);
    Ga toEntity(GaDTO dto);
    Ga fromCreateRequest(CreateGaRequest request, String generatedId);
}
