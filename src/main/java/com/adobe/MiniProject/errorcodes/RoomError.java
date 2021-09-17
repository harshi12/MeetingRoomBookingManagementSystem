package com.adobe.MiniProject.errorcodes;

public enum RoomError {
	
	TITLE_NOT_AVAILABLE("Can not create room as a room with "
			+ "the same title already in use"
			+ ", try another title"),
	CAN_NOT_UPDATE("Can not update room as a room with"
			+ " same title already exists, try another title"),
	ROOM_NOT_FOUND("There is no such Room with the given id"),
	IMAGE_NOT_FOUND("No image found with the given title");
	
	public String error;
	
	RoomError(String string) {
		this.error  = string;
	}
	
	public String value() {
		return this.error;
	}
	
}