package com.trainstation.mapper;

import com.trainstation.dto.CreateHoaDonRequest;
import com.trainstation.dto.HoaDonDTO;
import com.trainstation.model.HoaDon;

public interface HoaDonMapper {
    HoaDonDTO toDTO(HoaDon entity);
    HoaDon toEntity(HoaDonDTO dto);
    HoaDon fromCreateRequest(CreateHoaDonRequest request, String generatedId);
}
