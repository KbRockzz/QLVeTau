package com.trainstation.model;

import java.io.Serializable;

public class Carriage implements Serializable {
    private String carriageId;
    private String trainId;
    private String carriageTypeId;
    private String carriageName;
    private int carriageNumber;
    
    private Train train;
    private CarriageType carriageType;

    public Carriage() {
    }

    public Carriage(String carriageId, String trainId, String carriageTypeId, String carriageName, int carriageNumber) {
        this.carriageId = carriageId;
        this.trainId = trainId;
        this.carriageTypeId = carriageTypeId;
        this.carriageName = carriageName;
        this.carriageNumber = carriageNumber;
    }

    public Carriage(String carriageId, Train train, CarriageType carriageType, String carriageName, int carriageNumber) {
        this.carriageId = carriageId;
        this.train = train;
        this.carriageType = carriageType;
        if (train != null) {
            this.trainId = train.getTrainId();
        }
        if (carriageType != null) {
            this.carriageTypeId = carriageType.getCarriageTypeId();
        }
        this.carriageName = carriageName;
        this.carriageNumber = carriageNumber;
    }

    public String getCarriageId() {
        return carriageId;
    }

    public void setCarriageId(String carriageId) {
        this.carriageId = carriageId;
    }

    public String getTrainId() {
        return trainId;
    }

    public void setTrainId(String trainId) {
        this.trainId = trainId;
    }

    public String getCarriageTypeId() {
        return carriageTypeId;
    }

    public void setCarriageTypeId(String carriageTypeId) {
        this.carriageTypeId = carriageTypeId;
    }

    public String getCarriageName() {
        return carriageName;
    }

    public void setCarriageName(String carriageName) {
        this.carriageName = carriageName;
    }

    public int getCarriageNumber() {
        return carriageNumber;
    }

    public void setCarriageNumber(int carriageNumber) {
        this.carriageNumber = carriageNumber;
    }

    public Train getTrain() {
        return train;
    }

    public void setTrain(Train train) {
        this.train = train;
        if (train != null) {
            this.trainId = train.getTrainId();
        }
    }

    public CarriageType getCarriageType() {
        return carriageType;
    }

    public void setCarriageType(CarriageType carriageType) {
        this.carriageType = carriageType;
        if (carriageType != null) {
            this.carriageTypeId = carriageType.getCarriageTypeId();
        }
    }

    @Override
    public String toString() {
        return "Carriage{" +
                "carriageId='" + carriageId + '\'' +
                ", trainId='" + trainId + '\'' +
                ", carriageTypeId='" + carriageTypeId + '\'' +
                ", carriageName='" + carriageName + '\'' +
                ", carriageNumber=" + carriageNumber +
                '}';
    }
}
