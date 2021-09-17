package com.adobe.MiniProject.dal;

import java.util.List;
import com.adobe.MiniProject.domain.RoomLayoutToRooms;

public interface RoomLayoutToRoomsDao {
	List<Integer> getRoomIdFromRLId(int roomLayoutId);
	int addEntry(RoomLayoutToRooms roomLayoutToRoom);
	void deleteByRoomLayoutId(int RoomLayoutId);
	List<Integer> getRoomLayoutIdFromRoomId(int roomId);
	void deleteByRoomId(int roomId);
}
