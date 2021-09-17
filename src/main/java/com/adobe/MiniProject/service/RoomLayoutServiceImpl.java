package com.adobe.MiniProject.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adobe.MiniProject.dal.RoomDao;
import com.adobe.MiniProject.dal.RoomLayoutDao;
import com.adobe.MiniProject.dal.RoomLayoutToRoomsDao;
import com.adobe.MiniProject.domain.RoomLayout;
import com.adobe.MiniProject.domain.RoomLayoutToRooms;

@Service
public class RoomLayoutServiceImpl implements RoomLayoutService {

	RoomLayoutToRoomsDao roomLayoutToRoomDao;
	RoomLayoutDao roomLayoutDao;
	RoomDao roomDao;
	
	@Autowired
	public void setRoomLayoutToRoomsDao(RoomLayoutToRoomsDao dao) {
		this.roomLayoutToRoomDao = dao;
	}
	
	@Autowired
	public void setRoomLayoutDao(RoomLayoutDao dao) {
		this.roomLayoutDao = dao;
	}
	
	@Autowired
	public void setRoomDao(RoomDao dao) {
		this.roomDao = dao;
	}
	
	public int addRoomLayout(RoomLayout roomLayout) {
		RoomLayout newRoomLayout = roomLayoutDao.save(roomLayout);
		List<String> roomTitles = newRoomLayout.getRooms();
		for(String titles : roomTitles) {
			int roomId = roomDao.getIdFromTitle(titles);
			RoomLayoutToRooms roomLayoutToRoom = 
					new RoomLayoutToRooms(newRoomLayout.getId(),roomId);
			roomLayoutToRoomDao.addEntry(roomLayoutToRoom);
		}
		return newRoomLayout.getId();
	}

	public void removeRoomLayout(int roomLayoutID) {
		roomLayoutDao.deleteById(roomLayoutID);
		roomLayoutToRoomDao.deleteByRoomLayoutId(roomLayoutID);
	}

	public int updateRoomLayout(int layoutId, RoomLayout newRoomLayout) {
		newRoomLayout.setId(layoutId);
		int returnId = roomLayoutDao.updateById(layoutId, newRoomLayout);
		roomLayoutToRoomDao.deleteByRoomLayoutId(layoutId);
		List<String> roomTitles = newRoomLayout.getRooms();
		for(String titles : roomTitles) {
			int roomId = roomDao.getIdFromTitle(titles);
			RoomLayoutToRooms roomLayoutToRoom = 
					new RoomLayoutToRooms(layoutId,roomId);
			roomLayoutToRoomDao.addEntry(roomLayoutToRoom);
		}
		return layoutId;
	}

	public List<RoomLayout> getRoomLayouts() {
		List<RoomLayout> roomLayouts = roomLayoutDao.findAll();
		List<RoomLayout> newRoomLayoutList = new ArrayList<RoomLayout>();
		for(RoomLayout roomLayout : roomLayouts) {
			List<Integer> idOfRooms =  roomLayoutToRoomDao.getRoomIdFromRLId(roomLayout.getId());
			List<String> roomNames = new ArrayList<String>();
			for(int id : idOfRooms) {
				roomNames.add(roomDao.getTitleFromId(id));
			}
			roomLayout.setRooms(roomNames);
			newRoomLayoutList.add(roomLayout);
		}
		return newRoomLayoutList;
	}

	public RoomLayout getRoomLayoutById(int roomLayoutId) {
		RoomLayout roomLayout = roomLayoutDao.findById(roomLayoutId);
		if (roomLayout == null) return roomLayout;
		List<Integer> idOfRooms =  roomLayoutToRoomDao.getRoomIdFromRLId(roomLayout.getId());
		List<String> roomNames = new ArrayList<String>();
		for(int id : idOfRooms) {
			roomNames.add(roomDao.getTitleFromId(id));
		}
		roomLayout.setRooms(roomNames);
		return roomLayout;
	}

	public RoomLayout getByTitle(String title) {
		return roomLayoutDao.findByTitle(title);
	}

}
