package com.trainstation.mapper;

import com.trainstation.dto.BangGiaDTO;
import com.trainstation.dto.CreateBangGiaRequest;
import com.trainstation.model.BangGia;

public interface BangGiaMapper {
    BangGiaDTO toDTO(BangGia entity);
    BangGia toEntity(BangGiaDTO dto);
    BangGia fromCreateRequest(CreateBangGiaRequest request, String generatedId);
}
