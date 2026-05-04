package com.trainstation.mapper;

import com.trainstation.dto.ChuyenTauDTO;
import com.trainstation.dto.CreateChuyenTauRequest;
import com.trainstation.model.ChuyenTau;

public interface ChuyenTauMapper {
    ChuyenTauDTO toDTO(ChuyenTau entity);
    ChuyenTau toEntity(ChuyenTauDTO dto);
    ChuyenTau fromCreateRequest(CreateChuyenTauRequest request, String generatedId);
}
