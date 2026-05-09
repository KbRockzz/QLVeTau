package com.trainstation.mapper;

import com.trainstation.dto.CreateNhanVienRequest;
import com.trainstation.dto.NhanVienDTO;
import com.trainstation.model.NhanVien;

public interface NhanVienMapper {
    NhanVienDTO toDTO(NhanVien entity);
    NhanVien toEntity(NhanVienDTO dto);
    NhanVien fromCreateRequest(CreateNhanVienRequest request, String generatedId);
}
