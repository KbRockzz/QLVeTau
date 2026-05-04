package com.trainstation.mapper;

import com.trainstation.dto.KhachHangDTO;
import com.trainstation.dto.CreateKhachHangRequest;
import com.trainstation.model.KhachHang;

public interface KhachHangMapper {
    KhachHangDTO toDTO(KhachHang entity);
    KhachHang toEntity(KhachHangDTO dto);
    KhachHang fromCreateRequest(CreateKhachHangRequest request, String generatedId);
}
