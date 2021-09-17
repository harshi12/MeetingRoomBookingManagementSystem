package com.adobe.MiniProject.dal;


import com.adobe.MiniProject.domain.BookedFood;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class BookedFoodDAOImpl implements  BookedFoodDAO{
    @Autowired
    EntityManager entityManager;


    @Override
    public BookedFood save(BookedFood bookedFood) {
        entityManager.persist(bookedFood);
        return bookedFood;
    }

    @Override
    public List<BookedFood> findAllByBookingID(int bookingID) {
        Query q = entityManager.createQuery("select bf from BookedFood as bf where bf.bookingID=:id");
        q.setParameter("id", bookingID);
        return q.getResultList();
    }

    @Override
    public void update(BookedFood bookedFood) {
        entityManager.merge(bookedFood);
    }

    @Override
    public void deleteByBookingID(int bookingID) {
        Query q = entityManager.createQuery("delete from BookedFood as bf where bf.bookingID=:id");
        q.setParameter("id", bookingID);
        q.executeUpdate();
    }
}
