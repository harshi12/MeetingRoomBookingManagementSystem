package com.adobe.MiniProject.service;

import java.util.List;
import com.adobe.MiniProject.domain.BookedEquipment;
import org.json.simple.JSONObject;

public interface BookedEquipmentService {
	void addNewBookedEquipment(List<BookedEquipment> bookedEquipment, int bookingID);
	void updateBookedEquipment(List<BookedEquipment> bookedEquipmentJSON);
	List<BookedEquipment> getBookedEquipmentByBookingID(int bookingID);
	void deleteByBookingID(int bookingID);
    	JSONObject checkErrorForNewBooking(List<BookedEquipment> equipments);
	JSONObject checkErrorForUpdateBooking(List<BookedEquipment> equipments);
}
