package com.adobe.MiniProject.web;


import com.adobe.MiniProject.domain.Booking;
import com.adobe.MiniProject.domain.Client;
import com.adobe.MiniProject.domain.Room;
import com.adobe.MiniProject.domain.RoomLayout;
import com.adobe.MiniProject.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.security.PrivateKey;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest({BookingController.class})
public class BookingControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    BookingService bookingService;

    @MockBean
    RoomService roomService;

    @MockBean
    RoomLayoutService roomLayoutService;

    @MockBean
    AuthenticationService authService;

    @Test
    public void getBookingTest() throws Exception{
        String roomTitle = "Room", layoutTitle = "Layout";
        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(createBookingObject(1, roomTitle, layoutTitle,1));
        bookingList.add(createBookingObject(2, roomTitle, layoutTitle, 2));

        Mockito.when(bookingService.findAll()).thenReturn(bookingList);
        Mockito.when(authService.authenticate("sometoken")).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings").header("token","sometoken")
                .accept(MediaType.APPLICATION_JSON).content(mapToJson(bookingList)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getBookingByIDTest() throws Exception{
        String roomTitle = "Room", layoutTitle = "Layout";
        int bookingID = 1;
        Booking booking = createBookingObject(bookingID, roomTitle, layoutTitle, bookingID);

        Mockito.when(bookingService.findById(bookingID)).thenReturn(booking);
        Mockito.when(authService.authenticate("sometoken")).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/{id}", bookingID).header("token","sometoken")
                .accept(MediaType.APPLICATION_JSON).content(mapToJson(booking)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getBookingByID_No_Room_Exists_With_Given_ID_Test() throws Exception{
        String roomTitle = "Room", layoutTitle = "Layout";
        int bookingID = 1;
        Booking booking = createBookingObject(bookingID, roomTitle, layoutTitle, bookingID);

        Mockito.when(bookingService.findById(bookingID)).thenReturn(null);
        Mockito.when(authService.authenticate("sometoken")).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/{id}", bookingID).header("token","sometoken")
                .accept(MediaType.APPLICATION_JSON).content(mapToJson(booking)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void addBookingTest() throws Exception{
        String roomTitle = "Room", layoutTitle = "Layout";
        Booking booking = createBookingObject(0, roomTitle, layoutTitle, 0);
        Booking addedBooking = createBookingObject(1, roomTitle, layoutTitle, 1);

        Mockito.when(bookingService.addNewBooking(booking)).thenReturn(addedBooking);
        Mockito.when(authService.authenticate("sometoken")).thenReturn(true);
        Mockito.when(bookingService.checkErrorForNewBooking(any())).thenReturn(new JSONObject());

        mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapToJson(booking))
                .header("token","sometoken"))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    public void addBooking_With_Error_Test() throws Exception{
        String roomTitle = "Room", layoutTitle = "Layout";
        Booking booking = createBookingObject(0, roomTitle, layoutTitle, 0);
        Booking addedBooking = createBookingObject(1, roomTitle, layoutTitle, 1);
        JSONObject error = new JSONObject();
        error.put("ErrorCode", "Error Value");

        Mockito.when(bookingService.addNewBooking(booking)).thenReturn(addedBooking);
        Mockito.when(authService.authenticate("sometoken")).thenReturn(true);
        Mockito.when(bookingService.checkErrorForNewBooking(any())).thenReturn(error);

        mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapToJson(booking))
                .header("token","sometoken"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void updateBookingTest() throws Exception{
        String roomTitle = "Room", layoutTitle = "Layout", newRoomTitle = "NewRoom";
        int bookingID = 1;
        Booking booking = createBookingObject(bookingID, roomTitle, layoutTitle, 1);
        Booking updatedBooking = createBookingObject(bookingID, newRoomTitle, layoutTitle, 1);

        Mockito.when(bookingService.updateBookingById(booking)).thenReturn(updatedBooking);
        Mockito.when(bookingService.findById(bookingID)).thenReturn(booking);
        Mockito.when(authService.authenticate("sometoken")).thenReturn(true);
        Mockito.when(bookingService.checkErrorForUpdateBooking(any())).thenReturn(new JSONObject());

        mockMvc.perform(MockMvcRequestBuilders.put("/updateBooking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapToJson(booking))
                .header("token","sometoken"))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    public void updateBooking_No_Booking_Exist_With_Given_Id_Test() throws Exception{
        String roomTitle = "Room", layoutTitle = "Layout", newRoomTitle = "NewRoom";
        int bookingID = 1;
        Booking booking = createBookingObject(bookingID, roomTitle, layoutTitle, 1);
        Booking updatedBooking = createBookingObject(bookingID, newRoomTitle, layoutTitle, 1);

        Mockito.when(bookingService.updateBookingById(booking)).thenReturn(updatedBooking);
        Mockito.when(bookingService.findById(bookingID)).thenReturn(null);
        Mockito.when(authService.authenticate("sometoken")).thenReturn(true);
        Mockito.when(bookingService.checkErrorForUpdateBooking(any())).thenReturn(new JSONObject());

        mockMvc.perform(MockMvcRequestBuilders.put("/updateBooking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapToJson(booking))
                .header("token","sometoken"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void updateBooking_With_Error_Test() throws Exception{
        String roomTitle = "Room", layoutTitle = "Layout", newRoomTitle = "NewRoom";
        int bookingID = 1;
        Booking booking = createBookingObject(bookingID, roomTitle, layoutTitle, 1);
        Booking updatedBooking = createBookingObject(bookingID, newRoomTitle, layoutTitle, 1);
        JSONObject error = new JSONObject();
        error.put("ErrorCode", "Error Value");

        Mockito.when(bookingService.updateBookingById(booking)).thenReturn(updatedBooking);
        Mockito.when(bookingService.findById(bookingID)).thenReturn(booking);
        Mockito.when(authService.authenticate("sometoken")).thenReturn(true);
        Mockito.when(bookingService.checkErrorForUpdateBooking(any())).thenReturn(error);

        mockMvc.perform(MockMvcRequestBuilders.put("/updateBooking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapToJson(booking))
                .header("token","sometoken"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void deleteBookingTest() throws Exception{
        String roomTitle = "Room", layoutTitle = "Layout", newRoomTitle = "NewRoom";
        int bookingID = 1;
        Booking booking = createBookingObject(bookingID, roomTitle, layoutTitle, 1);

        Mockito.when(bookingService.findById(bookingID)).thenReturn(booking);
        Mockito.when(authService.authenticate("sometoken")).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.delete("/bookings/{id}", bookingID).header("token", "sometoken")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void deleteBooking_No_Booking_Exist_With_Given_Id_Test() throws Exception{
        int bookingID = 1;

        Mockito.when(bookingService.findById(bookingID)).thenReturn(null);
        Mockito.when(authService.authenticate("sometoken")).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.delete("/bookings/{id}", bookingID).header("token", "sometoken")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void changeBookingStatusTest() throws  Exception{
        int bookingID = 1;
        JSONObject statusChange = new JSONObject();
        statusChange.put("status", "Active");
        Booking booking = createBookingObject(bookingID, "room", "layout", bookingID);

        Mockito.when(authService.authenticate("sometoken")).thenReturn(true);
        Mockito.when(bookingService.findById(bookingID)).thenReturn(booking);

        mockMvc.perform(MockMvcRequestBuilders.post("/bookings/{id}/status", bookingID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapToJson(statusChange))
                .header("token","sometoken"))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    public void changeBookingStatus_No_Booking_Exist_With_Given_Id_Test() throws  Exception{
        int bookingID = 1;
        JSONObject statusChange = new JSONObject();
        statusChange.put("status", "Active");

        Mockito.when(authService.authenticate("sometoken")).thenReturn(true);
        Mockito.when(bookingService.findById(bookingID)).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/bookings/{id}/status", bookingID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapToJson(statusChange))
                .header("token","sometoken"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void authenticationFailureTest() throws JsonProcessingException, Exception {
        Booking booking = new Booking();
        Mockito.when(bookingService.findById(1)).thenReturn(booking);
        Mockito.when(authService.authenticate("sometoken")).thenReturn(false);
        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/{id}", 1).header("token","sometoken").
                accept(MediaType.APPLICATION_JSON).content(mapToJson(booking))).
                andExpect(status().isUnauthorized());
    }


    private String mapToJson(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }

    private RoomLayout createRoomLayoutObject(String title, String roomTitle){
        List<String> roomList = new ArrayList<>();
        roomList.add(roomTitle);
        RoomLayout roomLayout = new RoomLayout();
        roomLayout.setId(1);
        roomLayout.setTitle(title);
        roomLayout.setImageSrc(null);
        roomLayout.setRooms(roomList);
        return roomLayout;
    }

    private Room createRoomObject(String title, String layoutTitle){
        Room room = new Room();
        List<String> layout = new ArrayList<String>();
        layout.add(layoutTitle);
        room.setId(1);
        room.setTitle(title);
        room.setImageSrc("");
        room.setLayouts(layout);
        room.setBookingCount(0);
        room.setActiveStatus(true);
        room.setBookForHalfDay(false);
        room.setBookForHour(false);
        room.setBookForMultipleDays(true);
        room.setBookings(null);
        room.setCapacity(100);
        room.setDescription("Desc");
        room.setPricePerDay(10);
        room.setPricePerHalfDay(0);
        room.setPricePerHour(0);
        return room;
    }

    private Booking createBookingObject(int id, String roomTitle, String layoutTitle, int clientID) throws ParseException {
        Booking booking = new Booking();
        booking.setBookingID(id);
        booking.setEndDate(new SimpleDateFormat("yyyy-MM-dd").parse("2020-10-11"));
        booking.setCreatedOn(new Date());
        booking.setBookingDate(new SimpleDateFormat("yyyy-MM-dd").parse("2020-10-10"));
        booking.setStatus("pending");
        booking.setRoomType(roomTitle);
        booking.setTotal(100);
        booking.setTax(20);
        booking.setSubTotal(80);
        booking.setRoomPrice(80);
        booking.setPaymentMethod("Cash");
        booking.setLayout(layoutTitle);
        booking.setFoodPrice(0);
        booking.setEquipmentPrice(0);
        booking.setDuration("day");
        booking.setDeposit(100);
        booking.setAttendees(10);
        booking.setEquipments(null);
        booking.setBookingTime("");
        booking.setClient(createClientObject(clientID, id));
        booking.setIpAddress("");
        booking.setFood(null);
        return booking;
    }

    private Client createClientObject(int clientID, int bookingID){
        Client client = new Client();
        client.setBookingID(bookingID);
        client.setPhone(1234567890);
        client.setState("");
        client.setCountry("");
        client.setCity("");
        client.setAddress("");
        client.setCompany("");
        client.setNotes("");
        client.setEmail("agrawalharshita1995@gmail.com");
        client.setName("Client");
        client.setTitle("Ms");
        client.setClientID(clientID);
        client.setZip(385535);
        return client;
    }
}
