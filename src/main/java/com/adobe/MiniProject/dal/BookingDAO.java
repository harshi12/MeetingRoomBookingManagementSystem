package com.adobe.MiniProject.dal;

import java.util.Date;
import java.util.List;
import com.adobe.MiniProject.domain.Booking;

public interface BookingDAO {
	Booking save(Booking newBooking);
	Booking findById(int id);
	Booking updateById(Booking updatedBooking);
	List<Booking> findAll();
	void deleteById(int id);
	List<Booking> findByCreatedOn(Date date);
	List<Booking> findByBookingDate(Date date);
    void removeRoomType(int bookingID);
}
