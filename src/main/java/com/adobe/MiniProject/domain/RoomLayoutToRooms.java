package com.adobe.MiniProject.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class RoomLayoutToRooms {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private int roomLayoutId;
	private int roomId;

	public RoomLayoutToRooms(int id, int roomLayoutId, int roomId) {
		this.id = id;
		this.roomLayoutId = roomLayoutId;
		this.roomId = roomId;
	}
	
	public RoomLayoutToRooms(int roomLayoutId, int roomId) {
		this.roomLayoutId = roomLayoutId;
		this.roomId = roomId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getRoomLayoutId() {
		return roomLayoutId;
	}

	public void setRoomLayoutId(int roomLayoutId) {
		this.roomLayoutId = roomLayoutId;
	}

	public int getRoomId() {
		return roomId;
	}

	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}
	
}
