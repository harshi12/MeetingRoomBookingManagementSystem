package com.adobe.MiniProject.service;

import java.util.List;

import org.json.simple.JSONObject;

import com.adobe.MiniProject.domain.Room;
import org.json.simple.JSONObject;

public interface RoomService {

	Room addNewRoom(Room room);
	Room getByTitle(String title);
	List<Room> getAllRooms();
	Room getById(int roomId);
	Room updateRoom(int roomId, Room room);
	void removeRoom(int roomId);
	void addBookingID(String roomName, int bookingID);
	void deleteBookingID(int bookingID, String roomType);
    JSONObject isRoomAvailable(Room room, JSONObject roomBookingDetails);
	void changeRoomStatus(Room room, JSONObject statusChange);
}
