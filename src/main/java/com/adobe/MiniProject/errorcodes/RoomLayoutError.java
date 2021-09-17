package com.adobe.MiniProject.errorcodes;

public enum RoomLayoutError {
	TITLE_NOT_AVAILABLE("Can not create layout as a layout with the same title already in use, try another title"),
	CAN_NOT_UPDATE("Can not update layout as a layout with same title already exists, try another title"),
	ROOMLAYOUT_NOT_FOUND("There is no such Room Layout with the given id"),
	EMPTY_TITLE("Title can't be empty"),
	IMAGE_NOT_FOUND("No image found with the given title");
	
	public String error;
	RoomLayoutError(String string) {
		this.error  = string;
	}
	
	public String value() {
		return this.error;
	}
}
