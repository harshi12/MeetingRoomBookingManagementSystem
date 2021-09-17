package com.adobe.MiniProject.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import com.adobe.MiniProject.errorcodes.BookingError;
import com.adobe.MiniProject.errorcodes.Constants;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.adobe.MiniProject.dal.BookingDAOImpl;
import com.adobe.MiniProject.domain.Booking;

@Service
public class BookingServiceImpl implements BookingService {
	BookingDAOImpl bookingDAO;
	ClientService clientService;
	BookedEquipmentService bookedEquipmentService;
	RoomService roomService;
	RoomLayoutService roomLayoutService;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	@Autowired
	public void setService(BookedEquipmentService service, ClientService clientServ, RoomService rService, RoomLayoutService roomLayoutService) {
		this.clientService = clientServ;
		this.bookedEquipmentService = service;
		this.roomService = rService;
		this.roomLayoutService = roomLayoutService;
	}

	@Autowired
	public void setBookingDao(BookingDAOImpl dao) {
		this.bookingDAO = dao;
	}

	@Override
	public Booking addNewBooking(Booking newBooking) {
		Booking booking = bookingDAO.save(newBooking);
		int bookingID = booking.getBookingID();
		bookedEquipmentService.addNewBookedEquipment(booking.getEquipments(), bookingID);
		clientService.addNewClient(newBooking.getClient(), bookingID);
		roomService.addBookingID(booking.getRoomType(), bookingID);
		return booking;
	}

	@Override
	public List<Booking> findAll() {
		return bookingDAO.findAll();
	}

	@Override
	public void removeBooking(int id) {
		Booking booking = bookingDAO.findById(id);
		if (booking != null) {
			if(booking.getRoomType() != null)
				roomService.deleteBookingID(id, booking.getRoomType());
			bookedEquipmentService.deleteByBookingID(id);
			clientService.deleteByBookingID(id);
			bookingDAO.deleteById(id);
		}
	}

	@Override
	public Booking findById(int id) {
		return bookingDAO.findById(id);
	}

	@Override
	public Booking updateBookingById(Booking newBooking) {
		Booking oldBooking = bookingDAO.findById(newBooking.getBookingID());
		if (oldBooking.getRoomType() != null)
			roomService.deleteBookingID(oldBooking.getBookingID(), oldBooking.getRoomType());
		roomService.addBookingID(newBooking.getRoomType(), newBooking.getBookingID());
		bookedEquipmentService.updateBookedEquipment(newBooking.getEquipments());
		Booking booking = bookingDAO.updateById(newBooking);
		newBooking.getClient().setBookingID(newBooking.getBookingID());
		clientService.updateClient(newBooking.getClient());
		return booking;
	}

	@Override
	public JSONObject checkErrorForNewBooking(Booking booking) {
		JSONObject error = new JSONObject();
		if (booking.getBookingID() != 0) {
			error.put(Constants.ERROR_CODE_KEY, BookingError.BOOKING_JSON_HAS_BOOKING_ID.name());
			error.put(Constants.DEBUG_MESSAGE_KEY, BookingError.BOOKING_JSON_HAS_BOOKING_ID.value());
			return error;
		}
		error = bookedEquipmentService.checkErrorForNewBooking(booking.getEquipments());
		if (!error.isEmpty())
			return error;
		error = checkError(booking);
		if (!error.isEmpty())
			return error;
		error = clientService.checkErrorForNewBooking(booking.getClient());
		return error;
	}

	@Override
	public JSONObject checkErrorForUpdateBooking(Booking booking) {
		JSONObject error = new JSONObject();
		if (booking.getBookingID() == 0) {
			error.put(Constants.ERROR_CODE_KEY, BookingError.INVALID_UPDATE_BOOKING_JSON.name());
			error.put(Constants.DEBUG_MESSAGE_KEY, BookingError.INVALID_UPDATE_BOOKING_JSON.value());
			return error;
		}
		error = bookedEquipmentService.checkErrorForUpdateBooking(booking.getEquipments());
		if (!error.isEmpty())
			return error;
		error = checkError(booking);
		if (!error.isEmpty())
			return error;
		error = clientService.checkErrorForUpdateBooking(booking.getClient());
		return error;
	}

	public JSONObject checkError(Booking booking) {
		JSONObject error = new JSONObject();
		if (roomService.getByTitle(booking.getRoomType()) == null) {
			error.put(Constants.ERROR_CODE_KEY, BookingError.ROOM_NOT_FOUND.name());
			error.put(Constants.DEBUG_MESSAGE_KEY, BookingError.ROOM_NOT_FOUND.value());
			return error;
		}
		if (roomLayoutService.getByTitle(booking.getLayout()) == null) {
			error.put(Constants.ERROR_CODE_KEY, BookingError.LAYOUT_NOT_FOUND.name());
			error.put(Constants.DEBUG_MESSAGE_KEY, BookingError.LAYOUT_NOT_FOUND.value());
			return error;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = sdf.parse(sdf.format(new Date()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (booking.getBookingDate().compareTo(date) < 0) {
			error.put(Constants.ERROR_CODE_KEY, BookingError.INVALID_BOOKING_DATE.name());
			error.put(Constants.DEBUG_MESSAGE_KEY, BookingError.INVALID_BOOKING_DATE.value());
			return error;
		}
		if (booking.getEndDate() == null)
			return error;
		if (booking.getEndDate().compareTo(booking.getBookingDate()) < 0) {
			error.put(Constants.ERROR_CODE_KEY, BookingError.INVALID_END_DATE.name());
			error.put(Constants.DEBUG_MESSAGE_KEY, BookingError.INVALID_END_DATE.value());
			return error;
		}
		return error;
	}

	@Override
	public List<Booking> findByCreatedOn(Date date) {
		return bookingDAO.findByCreatedOn(date);
	}

	@Override
	public List<Booking> findByBookingDate(Date date) {
		return bookingDAO.findByBookingDate(date);
	}

	@Override
	public void removeRoomType(int bookingID) {
		bookingDAO.removeRoomType(bookingID);
	}

	@Override
	public void changeBookingStatus(Booking booking, JSONObject statusChange) {
		booking.setStatus(statusChange.get("status").toString());
		bookingDAO.updateById(booking);
	}
}
