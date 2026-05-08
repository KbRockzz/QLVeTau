package com.trainstation.mapper;

import com.trainstation.dto.CreateToaTauRequest;
import com.trainstation.dto.ToaTauDTO;
import com.trainstation.model.ToaTau;

public interface ToaTauMapper {
    ToaTauDTO toDTO(ToaTau entity);
    ToaTau toEntity(ToaTauDTO dto);
    ToaTau fromCreateRequest(CreateToaTauRequest request, String generatedId);
}
