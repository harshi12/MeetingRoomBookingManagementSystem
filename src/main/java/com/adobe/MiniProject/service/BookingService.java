package com.adobe.MiniProject.service;

import com.adobe.MiniProject.domain.Booking;
import org.json.simple.JSONObject;
import java.util.Date;
import java.util.List;

public interface BookingService {
	Booking addNewBooking(Booking bookingJSON);
	Booking updateBookingById(Booking newBooking);
	void removeBooking(int id);
	List<Booking> findAll();
	Booking findById(int id);
   	JSONObject checkErrorForNewBooking(Booking newBooking);
	JSONObject checkErrorForUpdateBooking(Booking booking);
	List<Booking> findByCreatedOn(Date date);
	List<Booking> findByBookingDate(Date date);
	void removeRoomType(int bookingID);
    void changeBookingStatus(Booking booking, JSONObject statusChange);
}
