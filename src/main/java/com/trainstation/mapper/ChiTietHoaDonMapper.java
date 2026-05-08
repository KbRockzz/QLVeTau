package com.trainstation.mapper;

import com.trainstation.dto.ChiTietHoaDonDTO;
import com.trainstation.dto.CreateChiTietHoaDonRequest;
import com.trainstation.model.ChiTietHoaDon;

public interface ChiTietHoaDonMapper {
    ChiTietHoaDonDTO toDTO(ChiTietHoaDon entity);
    ChiTietHoaDon toEntity(ChiTietHoaDonDTO dto);
    ChiTietHoaDon fromCreateRequest(CreateChiTietHoaDonRequest request);
}
