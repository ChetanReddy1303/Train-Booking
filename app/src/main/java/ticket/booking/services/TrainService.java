package ticket.booking.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ticket.booking.entities.Train;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TrainService {
    List<Train> trainList;
    private ObjectMapper om = new ObjectMapper();
    private static final String path = "../localDb/trains.json";
    public TrainService() throws IOException{
        File trains = new File(path);
        trainList = om.readValue(trains, new TypeReference<List<Train>>() {});
    }
    public List<Train> searchTrains(String src, String des){
        return trainList.stream().filter(train -> validateTrain(train, src, des)).collect(Collectors.toList());
    }

    public void addTrain(Train newTrain){
        Optional<Train> exist = trainList.stream().filter(train -> train.getTrainId().equalsIgnoreCase(newTrain.getTrainId())).findFirst();
        if(exist.isPresent()){
            updateTrain(newTrain);
        }else{
            trainList.add(newTrain);
            saveTrainList();
        }
    }
    public void updateTrain(Train updatedTrain){
        OptionalInt idx = IntStream.range(0, trainList.size()).
                filter(i -> trainList.get(i).getTrainId().equalsIgnoreCase(updatedTrain.getTrainId())).
                findFirst();
        if(idx.isPresent()){
            trainList.set(idx.getAsInt(), updatedTrain);
            saveTrainList();
        }
    }
    private void saveTrainList(){
        try{
            om.writeValue(new File(path), trainList);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    private boolean validateTrain(Train train, String src, String des){
        List<String> stationOrder = train.getStations();
        int srcIndex = stationOrder.indexOf(src.toLowerCase());
        int desIndex = stationOrder.indexOf(des.toLowerCase());
        return srcIndex != -1 && desIndex != -1 && srcIndex < desIndex;
    }
}
