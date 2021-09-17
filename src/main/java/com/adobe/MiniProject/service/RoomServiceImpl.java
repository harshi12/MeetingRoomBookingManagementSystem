package com.adobe.MiniProject.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.*;
import com.adobe.MiniProject.domain.Booking;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.adobe.MiniProject.dal.RoomDao;
import com.adobe.MiniProject.dal.RoomDaoImpl;
import com.adobe.MiniProject.dal.RoomLayoutDao;
import com.adobe.MiniProject.dal.RoomLayoutDaoImpl;
import com.adobe.MiniProject.dal.RoomLayoutToRoomsDao;
import com.adobe.MiniProject.domain.Room;
import com.adobe.MiniProject.domain.RoomLayoutToRooms;

@Service
public class RoomServiceImpl implements RoomService{

	RoomDao roomDao;
	RoomLayoutDao roomLayoutDao;
	RoomLayoutToRoomsDao roomLayoutToRoomDao;
	BookingService bookingService;
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	@Autowired
	public void setService(BookingService bookingService){
		this.bookingService = bookingService;
	}
	@Autowired
	public void setRoomDao(RoomDaoImpl dao) {
		this.roomDao = dao;
	}
	
	@Autowired
	public void setRoomLayoutDao(RoomLayoutDaoImpl dao) {
		this.roomLayoutDao = dao;
	}
	
	@Autowired
	public void setRoomLayoutToRoomsDao(RoomLayoutToRoomsDao dao) {
		this.roomLayoutToRoomDao = dao;
	}
	
	@Override
	public Room addNewRoom(Room room) {
		Room newRoom = roomDao.save(room);
		List<String> layouts = newRoom.getLayouts();
		for(String titles : layouts) {
			int layoutId = roomLayoutDao.getIdFromTitle(titles);
			RoomLayoutToRooms roomLayoutToRoom = 
					new RoomLayoutToRooms(layoutId, newRoom.getId());
			roomLayoutToRoomDao.addEntry(roomLayoutToRoom);
		}
		return newRoom;
	}

	@Override
	public Room getByTitle(String title) {
		return roomDao.findByTitle(title);
	}

	@Override
	public List<Room> getAllRooms() {
		List<Room> rooms = roomDao.findAll();
		List<Room> newRoomList = new ArrayList<>();
		for(Room room : rooms) {
			List<Integer> idOfRoomLayouts =  roomLayoutToRoomDao.getRoomLayoutIdFromRoomId(room.getId());
			List<String> layouts = new ArrayList<>();
			for(int id : idOfRoomLayouts) {
				layouts.add(roomLayoutDao.getTitleFromId(id));
			}
			room.setLayouts(layouts);
			newRoomList.add(room);
		}
		return newRoomList;
	}

	@Override
	public Room getById(int roomId) {
		Room room = roomDao.findById(roomId);
		if(room != null){
			List<Integer> idOfRoomLayouts =  roomLayoutToRoomDao.getRoomLayoutIdFromRoomId(room.getId());
			List<String> layouts = new ArrayList<>();
			for(int id : idOfRoomLayouts) {
				layouts.add(roomLayoutDao.getTitleFromId(id));
			}
			room.setLayouts(layouts);
		}
		return room;
	}

	@Override
	public Room updateRoom(int roomId, Room room) {
		room.setId(roomId);
		Room newRoom = roomDao.updateById(roomId, room);
		roomLayoutToRoomDao.deleteByRoomId(roomId);
		List<String> layouts = room.getLayouts();
		for(String titles : layouts) {
			int roomLayoutId = roomLayoutDao.getIdFromTitle(titles);
			RoomLayoutToRooms roomLayoutToRoom =
					new RoomLayoutToRooms(roomLayoutId, roomId);
			roomLayoutToRoomDao.addEntry(roomLayoutToRoom);
		}
		return newRoom;
	}

	@Override
	public void removeRoom(int roomId) {
		Set<Integer> bookings = roomDao.findById(roomId).getBookings();
		for(int bookingID : bookings){
			deleteBookingID(bookingID, roomDao.getTitleFromId(roomId));
			bookingService.removeRoomType(bookingID);
		}
		roomDao.deleteById(roomId);
		roomLayoutToRoomDao.deleteByRoomId(roomId);
	}

	@Override
	public void addBookingID(String roomName, int bookingID) {
		Room room = roomDao.getRoomFromTitle(roomName);
		roomDao.addBookingID(room, bookingID);
	}

	@Override
	public void deleteBookingID(int bookingID, String roomName) {
		Room room = roomDao.getRoomFromTitle(roomName);
		roomDao.deleteBookingID(room, bookingID);
	}

	@Override
	public void changeRoomStatus(Room room, JSONObject statusChange) {
		if (statusChange.get("status").toString().toUpperCase().equals("ACTIVE")) {
			room.setActiveStatus(true);
		} else {
			room.setActiveStatus(false);
		}
		roomDao.updateById(room.getId(), room);
	}

	@Override
	public JSONObject isRoomAvailable(Room room, JSONObject roomBookingDetails) {
		Set<Integer> bookings = room.getBookings();
		JSONObject roomAvailability = new JSONObject();
		if(roomBookingDetails.get("duration").equals("day")){
			roomAvailability = compareDurationDay(bookings, roomBookingDetails);
		}
		else if(roomBookingDetails.get("duration").equals("halfDay")){
			roomAvailability = compareDurationHalfDay(bookings, roomBookingDetails);
		}
		else if(roomBookingDetails.get("duration").equals("hour")){
			roomAvailability = compareDurationHour(bookings, roomBookingDetails);
		}
		return roomAvailability;
	}

	private JSONObject compareDurationHour(Set<Integer> bookings, JSONObject roomBookingDetails) {
		HashMap<String, Boolean> slotsAvailability = populateSlots(roomBookingDetails.get("duration").toString());
		JSONObject roomAvailability = new JSONObject();
		Date proposedBookingDate = null;
		try {
			proposedBookingDate = dateFormat.parse((String) roomBookingDetails.get("bookingDate"));
			proposedBookingDate = addHoursToJavaUtilDate(proposedBookingDate, 5, 30);
		} catch (ParseException e) {
			roomAvailability.put("error", "Error Parsing date");
			return roomAvailability;
		}
		for(int bookingID: bookings){
			if(roomBookingDetails.containsKey("bookingID")){
				if(bookingID == (int)roomBookingDetails.get("bookingID"))
					continue;
			}
			Booking booking = bookingService.findById(bookingID);
			Date bookingDate = booking.getBookingDate();
			if(booking.getDuration().equals("day")){
				Date endDate = booking.getEndDate();
				if(proposedBookingDate.compareTo(bookingDate) == 0 || proposedBookingDate.compareTo(endDate) == 0){
					roomAvailability.put("availableSlots", new HashSet<>());
					return roomAvailability;
				}
			}
			else if(booking.getDuration().equals("halfDay")){
				if(bookingDate.compareTo(proposedBookingDate) == 0){
					if(booking.getBookingTime().equals("Half-Day Morning (08:00-12:00)")) {
						slotsAvailability = blockSlots("morning", slotsAvailability);
					} else{
						slotsAvailability = blockSlots("evening", slotsAvailability);
					}
				}
			}
			else if(booking.getDuration().equals("hour")){
				if(proposedBookingDate.compareTo(bookingDate) == 0){
					String[] bookingTime = booking.getBookingTime().replace(" ", "").split("-");
					LocalTime from = LocalTime.parse(bookingTime[0]);
					LocalTime to = LocalTime.parse(bookingTime[1]);
					slotsAvailability = getBookedHours(from, to, slotsAvailability);
				}
			}
		}
		List<String> availableSlots = getAvailableSlots(slotsAvailability);
		Collections.sort(availableSlots);
		roomAvailability.put("availableSlots", availableSlots);
		return roomAvailability;
	}

	private JSONObject compareDurationHalfDay(Set<Integer> bookings, JSONObject roomBookingDetails) {
		HashMap<String, Boolean> slotsAvailability = populateSlots(roomBookingDetails.get("duration").toString());
		JSONObject roomAvailability = new JSONObject();
		Date proposedBookingDate = null;
		try {
			proposedBookingDate = dateFormat.parse((String) roomBookingDetails.get("bookingDate"));
			proposedBookingDate = addHoursToJavaUtilDate(proposedBookingDate, 5, 30);
		} catch (ParseException e) {
			roomAvailability.put("error", "Error Parsing date");
			return roomAvailability;
		}
		for(int bookingID: bookings){
			if(roomBookingDetails.containsKey("bookingID")){
				if(bookingID == (int)roomBookingDetails.get("bookingID"))
					continue;
			}
			Booking booking = bookingService.findById(bookingID);
			Date bookingDate = booking.getBookingDate();
			if(booking.getDuration().equals("day")){
				Date endDate = booking.getEndDate();
				if(proposedBookingDate.compareTo(bookingDate) == 0 || proposedBookingDate.compareTo(endDate) == 0){
					roomAvailability.put("availableSlots", new HashSet<>());
					return roomAvailability;
				}
			}
			else if(booking.getDuration().equals("halfDay")){
				if (bookingDate.compareTo(proposedBookingDate) == 0) {
					slotsAvailability.replace(booking.getBookingTime(), false);
				}
			}
			else if(booking.getDuration().equals("hour")){
				String[] bookingTime = booking.getBookingTime().replace(" ", "").split("-");
				if(bookingDate.compareTo(proposedBookingDate) == 0){
					LocalTime from = LocalTime.parse(bookingTime[0]);
					LocalTime to = LocalTime.parse(bookingTime[1]);
					LocalTime morningSlotEnd = LocalTime.parse("12:00");
					LocalTime eveningSlotStart = LocalTime.parse("14:00");
					if(from.isBefore(morningSlotEnd)){
						slotsAvailability.replace("Half-Day Morning (08:00-12:00)", false);
					}
					if(to.isAfter(eveningSlotStart)){
						slotsAvailability.replace("Half-Day Evening (14:00-18:00)", false);
					}
				}
			}
		}
		List<String> availableSlots = getAvailableSlots(slotsAvailability);
		roomAvailability.put("availableSlots", availableSlots);
		return roomAvailability;
	}

	public JSONObject compareDurationDay(Set<Integer> bookings, JSONObject roomBookingDetails){
		Set<Date> occupiedDates = new HashSet<>();
		JSONObject roomAvailability = new JSONObject();
		Date proposedBookingDate = null;
		try {
			proposedBookingDate = dateFormat.parse((String) roomBookingDetails.get("bookingDate"));
			proposedBookingDate = addHoursToJavaUtilDate(proposedBookingDate, 5, 30);
		} catch (ParseException e) {
			roomAvailability.put("error", "Error Parsing date");
			return roomAvailability;
		}
		Date maxDate = null;
		for(int bookingID: bookings){
			if(roomBookingDetails.containsKey("bookingID")){
				if(bookingID == (int)roomBookingDetails.get("bookingID"))
					continue;
			}
			Booking booking = bookingService.findById(bookingID);
			Date bookingDate = booking.getBookingDate();
			occupiedDates.add(bookingDate);
			if(booking.getDuration().equals("day")){
				Date endDate = booking.getEndDate();
				if(bookingDate.compareTo(endDate) > 0){
					bookingDate = addDayToJavaUtilDate(bookingDate, 1);
					while(bookingDate.compareTo(endDate) <= 0){
						occupiedDates.add(bookingDate);
						bookingDate = addDayToJavaUtilDate(bookingDate, 1);
					}
				}
			}
		}
		roomAvailability.put("occupiedDays", occupiedDates);
		return roomAvailability;
	}

	private HashMap<String, Boolean> getBookedHours(LocalTime from, LocalTime to, HashMap<String, Boolean> slotsAvailability) {
		while(from.isBefore(to)){
			String slot = from.toString() + "-" + from.plusHours(1).toString();
			slotsAvailability.replace(slot, false);
			from = from.plusHours(1);
		}
		return slotsAvailability;
	}

	private HashMap<String, Boolean> blockSlots(String time, HashMap<String, Boolean> slots) {
		if(time.equals("morning")){
			slots.replace("08:00-09:00", false);
			slots.replace("09:00-10:00", false);
			slots.replace("10:00-11:00", false);
			slots.replace("11:00-12:00", false);
		}else{
			slots.replace("14:00-15:00", false);
			slots.replace("15:00-16:00", false);
			slots.replace("16:00-17:00", false);
			slots.replace("17:00-18:00", false);
		}
		return slots;
	}

	public Date addHoursToJavaUtilDate(Date date, int hours, int minutes) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.HOUR_OF_DAY, hours);
		calendar.add(Calendar.MINUTE, minutes);
		return calendar.getTime();
	}

	public Date addDayToJavaUtilDate(Date date, int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, days);
		return calendar.getTime();
	}

	public List<String> getAvailableSlots(HashMap<String, Boolean> slots){
		List<String> availableSlots = new ArrayList<>();
		for(String key: slots.keySet()){
			if(slots.get(key) == true)
				availableSlots.add(key);
		}
		return availableSlots;
	}

	public HashMap<String, Boolean> populateSlots(String slotType){
		HashMap<String, Boolean> slots = new HashMap<>();
		if(slotType.equals("halfDay")){
			slots.put("Half-Day Morning (08:00-12:00)", true);
			slots.put("Half-Day Evening (14:00-18:00)", true);
		}else{
			slots.put("08:00-09:00", true);
			slots.put("09:00-10:00", true);
			slots.put("10:00-11:00", true);
			slots.put("11:00-12:00", true);
			slots.put("12:00-13:00", true);
			slots.put("13:00-14:00", true);
			slots.put("14:00-15:00", true);
			slots.put("15:00-16:00", true);
			slots.put("16:00-17:00", true);
			slots.put("17:00-18:00", true);
		}
		return slots;
	}
}

