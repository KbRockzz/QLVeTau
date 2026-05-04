package com.trainstation.service.iface;

import com.trainstation.dto.ChuyenTauDTO;
import com.trainstation.dto.CreateChuyenTauRequest;
import com.trainstation.dto.UpdateChuyenTauRequest;
import com.trainstation.model.ChuyenTau;
import java.util.List;

public interface IChuyenTauService {
    List<ChuyenTau> layTatCaChuyenTau();
    ChuyenTau timChuyenTauTheoMa(String maChuyen);
    List<ChuyenTau> timKiemChuyenTau(String keyword);
    boolean themChuyenTau(ChuyenTau ct);
    boolean capNhatChuyenTau(ChuyenTau ct);
    boolean xoaChuyenTau(String maChuyen);
    ChuyenTauDTO taoChuyenTauTuRequest(CreateChuyenTauRequest request);
    ChuyenTauDTO capNhatChuyenTauTuRequest(UpdateChuyenTauRequest request);
}
