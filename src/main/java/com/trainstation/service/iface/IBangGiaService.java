package com.trainstation.service.iface;

import com.trainstation.dto.BangGiaDTO;
import com.trainstation.dto.CreateBangGiaRequest;
import com.trainstation.dto.UpdateBangGiaRequest;
import com.trainstation.model.BangGia;

import java.util.List;

public interface IBangGiaService {
    List<BangGia> layTatCaBangGia();
    BangGia timBangGiaTheoMa(String maBG);
    String taoMaBangGia();
    boolean themBangGia(BangGia bg);
    boolean capNhatBangGia(BangGia bg);
    boolean xoaBangGia(String maBG);
    BangGiaDTO taoBangGiaTuRequest(CreateBangGiaRequest request);
    BangGiaDTO capNhatBangGiaTuRequest(UpdateBangGiaRequest request);
}
