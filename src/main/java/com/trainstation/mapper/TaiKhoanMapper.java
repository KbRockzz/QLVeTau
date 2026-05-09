package com.trainstation.mapper;

import com.trainstation.dto.CreateTaiKhoanRequest;
import com.trainstation.dto.TaiKhoanDTO;
import com.trainstation.model.TaiKhoan;

public interface TaiKhoanMapper {
    TaiKhoanDTO toDTO(TaiKhoan entity);
    TaiKhoan toEntity(TaiKhoanDTO dto);
    TaiKhoan fromCreateRequest(CreateTaiKhoanRequest request, String generatedId);
}
