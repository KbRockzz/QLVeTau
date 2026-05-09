package com.trainstation.mapper;

import com.trainstation.dto.ChiTietChuyenTauDTO;
import com.trainstation.dto.CreateChiTietChuyenTauRequest;
import com.trainstation.model.ChiTietChuyenTau;

public interface ChiTietChuyenTauMapper {
    ChiTietChuyenTauDTO toDTO(ChiTietChuyenTau entity);
    ChiTietChuyenTau toEntity(ChiTietChuyenTauDTO dto);
    ChiTietChuyenTau fromCreateRequest(CreateChiTietChuyenTauRequest request);
}
