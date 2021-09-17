package com.adobe.MiniProject.service;

import com.adobe.MiniProject.dal.BookedFoodDAO;
import com.adobe.MiniProject.domain.BookedFood;
import com.adobe.MiniProject.errorcodes.BookingError;
import com.adobe.MiniProject.errorcodes.Constants;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BookedFoodServiceImpl implements BookedFoodService {
    BookedFoodDAO bookedFoodDAO;
    FoodService foodService;
    @Autowired
    public void setService(FoodService service){
        this.foodService = service;
    }
    @Autowired
    public void setDAO(BookedFoodDAO dao){
        this.bookedFoodDAO = dao;
    }

    @Override
    public void addNewBookedFood(List<BookedFood> bookedFoodList, int bookingID) {
        for(BookedFood food: bookedFoodList){
            food.setBookingID(bookingID);
            bookedFoodDAO.save(food);
        }
    }

    @Override
    public void update(List<BookedFood> bookedFoodList) {
        for(BookedFood food: bookedFoodList){
            bookedFoodDAO.update(food);
        }
    }

    @Override
    public List<BookedFood> getBookedFoodByBookingID(int bookingID) {
        return bookedFoodDAO.findAllByBookingID(bookingID);
    }

    @Override
    public void deleteByBookingID(int bookingID) {
        bookedFoodDAO.deleteByBookingID(bookingID);
    }

    @Override
    public JSONObject checkErrorForNewBooking(List<BookedFood> bookedFoodList) {
        JSONObject error = new JSONObject();
        for(BookedFood bookedFood: bookedFoodList){
            if(bookedFood.getBookingID() != 0){
                error.put(Constants.ERROR_CODE_KEY, BookingError.BOOKING_JSON_HAS_BOOKING_ID.name());
                error.put(Constants.DEBUG_MESSAGE_KEY, BookingError.BOOKING_JSON_HAS_BOOKING_ID.value());
                return error;
            }
            error = checkError(bookedFood);
            if(!error.isEmpty())
                return error;
        }
        return error;
    }

    @Override
    public JSONObject checkErrorForUpdateBooking(List<BookedFood> bookedFoodList) {
        JSONObject error = new JSONObject();
        for(BookedFood bookedFood: bookedFoodList){
            if(bookedFood.getBookingID() == 0){
                error.put(Constants.ERROR_CODE_KEY, BookingError.INVALID_UPDATE_BOOKING_JSON.name());
                error.put(Constants.DEBUG_MESSAGE_KEY, BookingError.INVALID_UPDATE_BOOKING_JSON.value());
                return error;
            }
            error = checkError(bookedFood);
            if(!error.isEmpty())
                return error;
        }
        return error;
    }

    public JSONObject checkError(BookedFood bookedFood){
        JSONObject error = new JSONObject();
        if(bookedFood.getFoodID() == 0){
            error.put(Constants.ERROR_CODE_KEY, BookingError.FOOD_ID_NOT_FOUND.name());
            error.put(Constants.DEBUG_MESSAGE_KEY, BookingError.FOOD_ID_NOT_FOUND.value());
            return error;
        }
        if(foodService.getByTitle(bookedFood.getTitle()) == null){
            error.put(Constants.ERROR_CODE_KEY, BookingError.FOOD_ITEM_NOT_FOUND.name());
            error.put(Constants.DEBUG_MESSAGE_KEY, BookingError.FOOD_ITEM_NOT_FOUND.value());
            return error;
        }

        if(bookedFood.getFoodID() != foodService.getFoodIDByTitle(bookedFood.getTitle())){
            error.put(Constants.ERROR_CODE_KEY, BookingError.FOOD_ITEM_NOT_FOUND.name());
            error.put(Constants.DEBUG_MESSAGE_KEY, BookingError.FOOD_ITEM_NOT_FOUND.value());
            return error;
        }
        return error;
    }
}
