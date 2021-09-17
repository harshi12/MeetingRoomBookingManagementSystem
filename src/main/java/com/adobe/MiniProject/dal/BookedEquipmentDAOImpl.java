package com.adobe.MiniProject.dal;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.adobe.MiniProject.domain.BookedEquipment;

@Repository
@Transactional
public class BookedEquipmentDAOImpl implements BookedEquipmentDAO {
	@Autowired
	EntityManager entityManager;

	@Override
	public BookedEquipment save(BookedEquipment bookedEquipment) {
		entityManager.persist(bookedEquipment);
		return bookedEquipment;
	}

	@Override
	public List<BookedEquipment> findAllByBookingID(int bookingID) {
		Query q = entityManager.createQuery("select be from BookedEquipment as be where be.bookingID=:id");
		q.setParameter("id", bookingID);
		return q.getResultList();
	}

	@Override
	public void updateByBookingID(BookedEquipment equipment) {
		int bookingID = equipment.getBookingID();
		Query q = entityManager.createQuery("select be from BookedEquipment as be where be.bookingID=:id");
		q.setParameter("id", bookingID);
		if(q.getResultList().isEmpty()){
			entityManager.persist((equipment));
		}
		entityManager.merge(equipment);
	}

	@Override
	public void deleteByBookingID(int bookingID) {
		Query q = entityManager.createQuery("delete from BookedEquipment as be where be.bookingID=:id");
		q.setParameter("id", bookingID);
		q.executeUpdate();
	}
}
