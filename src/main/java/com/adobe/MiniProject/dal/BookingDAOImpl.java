package com.adobe.MiniProject.dal;

import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.adobe.MiniProject.domain.Booking;

@Repository
@Transactional
public class BookingDAOImpl implements BookingDAO {
	@Autowired
	EntityManager entityManager;
	@Override
	public Booking save(Booking newBooking) {
		entityManager.persist(newBooking);
		return newBooking;
	}

	@Override
	public Booking findById(int id) {
		return entityManager.find(Booking.class, id);
	}

	@Override
	public List<Booking> findAll() {
		Query q = entityManager.createQuery("select b from Booking as b");
		return q.getResultList();
	}

	@Override
	public void deleteById(int id) {
		Query q = entityManager.createQuery("delete from Booking as b where b.bookingID=:id");
		q.setParameter("id", id);
		q.executeUpdate();
	}

	@Override
	public Booking updateById(Booking updatedBooking) {
		entityManager.merge(updatedBooking);
		return updatedBooking;
	}

	@Override
	public List<Booking> findByCreatedOn(Date date) {
		return entityManager.createQuery("select b from Booking b where b.createdOn=:value").setParameter("value", date).getResultList();
	}

	@Override
	public List<Booking> findByBookingDate(Date date) {
		return entityManager.createQuery("select b from Booking b where b.bookingDate=:value").setParameter("value", date).getResultList();
	}

	@Override
	public void removeRoomType(int bookingID) {
		Booking booking = findById(bookingID);
		booking.setRoomType(null);
		entityManager.merge(booking);
	}
}
