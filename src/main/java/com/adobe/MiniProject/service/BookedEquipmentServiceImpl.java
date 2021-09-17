package com.adobe.MiniProject.service;

import java.util.List;
import com.adobe.MiniProject.errorcodes.BookingError;
import com.adobe.MiniProject.errorcodes.Constants;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.adobe.MiniProject.dal.BookedEquipmentDAO;
import com.adobe.MiniProject.domain.BookedEquipment;

@Service
public class BookedEquipmentServiceImpl implements BookedEquipmentService {
	BookedEquipmentDAO bookedEquipmentDao;
	EquipmentService equipmentService;
	@Autowired
	public void setService(EquipmentService service){
		this.equipmentService = service;
	}
	@Autowired
	public void setDAO(BookedEquipmentDAO dao) {
		this.bookedEquipmentDao = dao;
	}


	@Override
	public void addNewBookedEquipment(List<BookedEquipment> bookedEquipments, int bookingID) {
		for(BookedEquipment equipment: bookedEquipments){
			equipment.setBookingID(bookingID);
			bookedEquipmentDao.save(equipment);
		}
	}

	@Override
	public void updateBookedEquipment(List<BookedEquipment> bookedEquipmentList) {
		for(BookedEquipment bookedEquipment: bookedEquipmentList){
			bookedEquipmentDao.updateByBookingID(bookedEquipment);
		}
	}

	@Override
	public List<BookedEquipment> getBookedEquipmentByBookingID(int bookingID) {
		return bookedEquipmentDao.findAllByBookingID(bookingID);
	}

	@Override
	public void deleteByBookingID(int bookingID) {
		bookedEquipmentDao.deleteByBookingID(bookingID);
	}

	public JSONObject checkErrorForNewBooking(List<BookedEquipment> bookedEquipmentList){
		JSONObject error = new JSONObject();
		for(BookedEquipment bookedEquipment: bookedEquipmentList){
			if(bookedEquipment.getBookingID() != 0){
				error.put(Constants.ERROR_CODE_KEY, BookingError.BOOKING_JSON_HAS_BOOKING_ID.name());
				error.put(Constants.DEBUG_MESSAGE_KEY, BookingError.BOOKING_JSON_HAS_BOOKING_ID.value());
				return error;
			}
			error = checkError(bookedEquipment);
			if(!error.isEmpty())
				return error;
		}
		return error;
	}

	@Override
	public JSONObject checkErrorForUpdateBooking(List<BookedEquipment> bookedEquipmentList) {
		JSONObject error = new JSONObject();
		for(BookedEquipment bookedEquipment: bookedEquipmentList){
			if(bookedEquipment.getBookingID() == 0){
				error.put(Constants.ERROR_CODE_KEY, BookingError.INVALID_UPDATE_BOOKING_JSON.name());
				error.put(Constants.DEBUG_MESSAGE_KEY, BookingError.INVALID_UPDATE_BOOKING_JSON.value());
				return error;
			}
			error = checkError(bookedEquipment);
			if(!error.isEmpty())
				return error;
		}
		return error;
	}

	public JSONObject checkError(BookedEquipment bookedEquipment){
		JSONObject error = new JSONObject();
		if(bookedEquipment.getEquipmentID() == 0) {
			error.put(Constants.ERROR_CODE_KEY, BookingError.EQUIPMENT_ID_NOT_FOUND.name());
			error.put(Constants.DEBUG_MESSAGE_KEY, BookingError.EQUIPMENT_ID_NOT_FOUND.value());
			return error;
		}
		if(equipmentService.getByTitle(bookedEquipment.getTitle()) == null){
			error.put(Constants.ERROR_CODE_KEY, BookingError.EQUIPMENT_NOT_FOUND.name());
			error.put(Constants.DEBUG_MESSAGE_KEY, BookingError.EQUIPMENT_NOT_FOUND.value());
			return error;
		}
		if(bookedEquipment.getEquipmentID() != equipmentService.getEquipmentIDByTitle(bookedEquipment.getTitle())){
			error.put(Constants.ERROR_CODE_KEY, BookingError.EQUIPMENT_NOT_FOUND.name());
			error.put(Constants.DEBUG_MESSAGE_KEY, BookingError.EQUIPMENT_NOT_FOUND.value());
			return error;
		}
		return error;
	}
}
