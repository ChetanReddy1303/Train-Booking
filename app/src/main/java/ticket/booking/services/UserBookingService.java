package ticket.booking.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ticket.booking.entities.Train;
import ticket.booking.entities.User;
import ticket.booking.util.UserServiceUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class UserBookingService {
    private User user;
    private List<User> userList;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String USERS_PATH = "ticket/booking/localDb/users.json";
    public UserBookingService(User user1) throws IOException {
        this.user = user1;
        loadUsers();

    }
    public UserBookingService() throws IOException{
        loadUsers();
    }
    public void loadUsers() throws IOException{
        File users = new File(USERS_PATH);
        objectMapper.readValue(users, new TypeReference<List<User>>() {
        });
    }
    public Boolean loginUser(){
        Optional<User> foundUser = userList.stream().filter(user1
                -> { return user1.getName().equalsIgnoreCase(user.getName()) &&
                UserServiceUtil.checkPassword(user.getPassword(), user1.getHashedPassword());
        }).findFirst();
        return foundUser.isPresent();
    }
    public Boolean signUp(User user1){
        try {
            userList.add(user1);
            saveUserListToFile();
            return Boolean.TRUE;
        }catch (IOException ex){
            return Boolean.FALSE;
        }
    }
    private void saveUserListToFile() throws IOException{
        File userFile = new File(USERS_PATH);
        objectMapper.writeValue(userFile, userList);
    }

    public void fetchBooking(){
        Optional<User> userFetched = userList.stream().filter(u1 -> {
            return u1.getName().equals(user.getName()) && UserServiceUtil.checkPassword(user.getPassword(), u1.getHashedPassword());
        }).findFirst();
        if(userFetched.isPresent()){
            userFetched.get().printTickets();
        }
    }
    public Boolean cancelBooking(String ticketId){
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the ticket ID you want to delete-");
        ticketId = sc.next();
        if(ticketId == null || ticketId.isEmpty()){
            System.out.println("Ticket ID can't be Empty or Null");
            return false;
        }
        String finalTicketId1 = ticketId;
        boolean removed = user.getTicketsBooked().removeIf(ticket -> ticket.getTicketId().equals(finalTicketId1));
        String finalTicketId = ticketId;
        user.getTicketsBooked().removeIf(Ticket -> Ticket.getTicketId().equals(finalTicketId));
        if(removed){
            System.out.println("Ticke with ID : " + ticketId + " has been cancelled.");
            return Boolean.TRUE;
        }else{
            System.out.println("No ticket found with ID : " + ticketId);
            return Boolean.FALSE;
        }
    }

    public List<Train> getTrains(String source, String dest) {
        try{
            TrainService trainService = new TrainService();
            return trainService.searchTrains(source, dest);
        }catch(IOException e){
            return new ArrayList<>();
        }
    }
    public List<List<Integer>> fetchSeats(Train train){
        return train.getSeats();
    }
    public boolean bookTrainSeat(Train train, int r, int seat){
        try{
            TrainService trainService = new TrainService();
            List<List<Integer>> seats = train.getSeats();
            if(r >= 0 && r < seats.size() && seat >= 0 && seat < seats.get(r).size()){
                if(seats.get(r).get(seat) == 0){
                    seats.get(r).set(seat, 1);
                    train.setSeats(seats);
                    trainService.addTrain(train);
                    return true;
                }else{
                    return false;
                }
            }else{
                return false;
            }
        }catch(IOException e){
            return Boolean.FALSE;
        }
    }
}
