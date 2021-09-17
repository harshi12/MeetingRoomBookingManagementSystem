package com.adobe.MiniProject.domain;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Embeddable;
import javax.persistence.IdClass;

@Embeddable
@IdClass(BookedEquipmentCompositeKey.class)
public class BookedEquipmentCompositeKey implements Serializable {
	private int bookingID;
	private int equipmentID;

	public BookedEquipmentCompositeKey() {
	}

	public BookedEquipmentCompositeKey(int bookingID, int equipmentID) {
		this.bookingID = bookingID;
		this.equipmentID = equipmentID;
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		BookedEquipmentCompositeKey compositeObj = (BookedEquipmentCompositeKey) o;
		if(this.bookingID != compositeObj.bookingID) return false;
		return this.equipmentID == compositeObj.equipmentID;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(bookingID, equipmentID);
	}
}
