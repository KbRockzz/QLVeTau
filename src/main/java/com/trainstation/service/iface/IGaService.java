package com.trainstation.service.iface;

import com.trainstation.dto.CreateGaRequest;
import com.trainstation.dto.GaDTO;
import com.trainstation.dto.UpdateGaRequest;
import com.trainstation.model.Ga;

import java.util.List;

public interface IGaService {
    List<Ga> layTatCaGa();
    Ga timGaTheoMa(String maGa);
    boolean themGa(Ga ga);
    boolean capNhatGa(Ga ga);
    boolean xoaGa(String maGa);
    String taoMaGa();
    List<Ga> layGaDaXoa();
    boolean khoiPhucGa(String maGa);
    GaDTO taoGaTuRequest(CreateGaRequest request);
    GaDTO capNhatGaTuRequest(UpdateGaRequest request);
}
