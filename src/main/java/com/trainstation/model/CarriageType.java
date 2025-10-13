package com.trainstation.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class CarriageType implements Serializable {
    private String carriageTypeId;
    private String typeName;
    private int seatCount;
    private BigDecimal priceMultiplier;

    public CarriageType() {
    }

    public CarriageType(String carriageTypeId, String typeName, int seatCount, BigDecimal priceMultiplier) {
        this.carriageTypeId = carriageTypeId;
        this.typeName = typeName;
        this.seatCount = seatCount;
        this.priceMultiplier = priceMultiplier;
    }

    public String getCarriageTypeId() {
        return carriageTypeId;
    }

    public void setCarriageTypeId(String carriageTypeId) {
        this.carriageTypeId = carriageTypeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public int getSeatCount() {
        return seatCount;
    }

    public void setSeatCount(int seatCount) {
        this.seatCount = seatCount;
    }

    public BigDecimal getPriceMultiplier() {
        return priceMultiplier;
    }

    public void setPriceMultiplier(BigDecimal priceMultiplier) {
        this.priceMultiplier = priceMultiplier;
    }

    @Override
    public String toString() {
        return "CarriageType{" +
                "carriageTypeId='" + carriageTypeId + '\'' +
                ", typeName='" + typeName + '\'' +
                ", seatCount=" + seatCount +
                ", priceMultiplier=" + priceMultiplier +
                '}';
    }
}
