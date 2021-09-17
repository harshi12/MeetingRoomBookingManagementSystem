package com.adobe.MiniProject.service;

import java.util.List;

import com.adobe.MiniProject.domain.RoomLayout;

public interface RoomLayoutService {

	int addRoomLayout(RoomLayout roomLayout);
	void removeRoomLayout(int roomLayoutId);
	int updateRoomLayout(int roomLayoutId, RoomLayout newRoomLayout);
	List<RoomLayout> getRoomLayouts(); 
	RoomLayout getRoomLayoutById(int roomLayoutId);
	RoomLayout getByTitle(String title);
	
}
