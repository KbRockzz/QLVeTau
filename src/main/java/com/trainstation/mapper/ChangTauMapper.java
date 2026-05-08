package com.trainstation.mapper;

import com.trainstation.dto.ChangTauDTO;
import com.trainstation.dto.CreateChangTauRequest;
import com.trainstation.model.ChangTau;

public interface ChangTauMapper {
    ChangTauDTO toDTO(ChangTau entity);
    ChangTau toEntity(ChangTauDTO dto);
    ChangTau fromCreateRequest(CreateChangTauRequest request);
}
