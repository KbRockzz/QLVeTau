package com.trainstation.service.iface;

import com.trainstation.dto.CreateVeRequest;
import com.trainstation.dto.UpdateVeRequest;
import com.trainstation.dto.VeDTO;
import com.trainstation.model.Ve;
import java.util.List;

public interface IVeService {
    List<Ve> layTatCaVe();
    Ve timVeTheoMa(String maVe);
    List<Ve> layVeTheoChuyen(String maChuyen);
    Ve taoVe(Ve ve);
    boolean capNhatVe(Ve ve);
    boolean xoaVe(String maVe);
    VeDTO taoVeTuRequest(CreateVeRequest request);
    VeDTO capNhatVeTuRequest(UpdateVeRequest request);
}
