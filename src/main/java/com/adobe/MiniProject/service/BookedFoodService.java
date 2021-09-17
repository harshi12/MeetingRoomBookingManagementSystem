package com.adobe.MiniProject.service;

import com.adobe.MiniProject.domain.BookedFood;
import org.json.simple.JSONObject;
import java.util.List;

public interface BookedFoodService {
    void addNewBookedFood(List<BookedFood> bookedFoodList, int bookingID);
    void update(List<BookedFood> bookedFoodList);
    List<BookedFood> getBookedFoodByBookingID(int bookingID);
    void deleteByBookingID(int bookingID);
    JSONObject checkErrorForNewBooking(List<BookedFood> food);
    JSONObject checkErrorForUpdateBooking(List<BookedFood> food);
}
