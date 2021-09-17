package com.adobe.MiniProject.dal;

import java.util.List;
import com.adobe.MiniProject.domain.BookedEquipment;

public interface BookedEquipmentDAO {
	BookedEquipment save(BookedEquipment bookedEquipment);
	List<BookedEquipment> findAllByBookingID(int bookingID);
	void updateByBookingID(BookedEquipment equipmentDetails);
	void deleteByBookingID(int bookingID);
}
