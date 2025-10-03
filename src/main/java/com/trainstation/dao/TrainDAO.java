package com.trainstation.dao;

import com.trainstation.model.Train;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrainDAO implements GenericDAO<Train> {
    private static TrainDAO instance;
    private Map<String, Train> trains;

    private TrainDAO() {
        trains = new HashMap<>();
    }

    public static synchronized TrainDAO getInstance() {
        if (instance == null) {
            instance = new TrainDAO();
        }
        return instance;
    }

    @Override
    public void add(Train train) {
        trains.put(train.getTrainId(), train);
    }

    @Override
    public void update(Train train) {
        trains.put(train.getTrainId(), train);
    }

    @Override
    public void delete(String id) {
        trains.remove(id);
    }

    @Override
    public Train findById(String id) {
        return trains.get(id);
    }

    @Override
    public List<Train> findAll() {
        return new ArrayList<>(trains.values());
    }

    public List<Train> findByRoute(String departureStation, String arrivalStation) {
        List<Train> result = new ArrayList<>();
        for (Train train : trains.values()) {
            if (train.getDepartureStation().equals(departureStation) && 
                train.getArrivalStation().equals(arrivalStation)) {
                result.add(train);
            }
        }
        return result;
    }
}
