package com.adobe.MiniProject.errorcodes;

public enum EquipmentError {
	
	TITLE_NOT_AVAILABLE("Can not create equipment as an equipment with the same title already in use, try another title"),
	CAN_NOT_UPDATE("Can not update equipment as an equipment with same title already exists, try another title"),
	EQUIPMENT_NOT_FOUND("There is no such Equipment with the given id"),
	EMPTY_TITLE("Title can't be empty"),
	INCORRECT_PRICE_VALUE("Price for equipment can't be negative or zero");
	
	public String error;
	EquipmentError(String string) {
		this.error  = string;
	}
	
	public String value() {
		return this.error;
	}

}
