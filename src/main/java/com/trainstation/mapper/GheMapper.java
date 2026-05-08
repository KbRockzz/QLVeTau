package com.trainstation.mapper;

import com.trainstation.dto.CreateGheRequest;
import com.trainstation.dto.GheDTO;
import com.trainstation.model.Ghe;

public interface GheMapper {
    GheDTO toDTO(Ghe entity);
    Ghe toEntity(GheDTO dto);
    Ghe fromCreateRequest(CreateGheRequest request, String generatedId);
}
