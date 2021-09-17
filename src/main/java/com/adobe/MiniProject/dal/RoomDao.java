package com.adobe.MiniProject.dal;

import java.util.List;
import com.adobe.MiniProject.domain.Room;

public interface RoomDao {
	String getTitleFromId(int roomId);
	int getIdFromTitle(String title);
	Room save(Room newRoom);
	Room findByTitle(String title);
	Room findById(int roomId);
	List<Room> findAll();
	Room updateById(int roomId, Room room);
	void deleteById(int roomId);
	void addBookingID(Room room, int bookingID);
	Room getRoomFromTitle(String roomName);
	void deleteBookingID(Room room, int bookingID);
}
