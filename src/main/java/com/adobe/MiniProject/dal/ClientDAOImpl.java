package com.adobe.MiniProject.dal;

import com.adobe.MiniProject.domain.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import javax.persistence.Query;

@Repository
@Transactional
public class ClientDAOImpl implements ClientDAO {
    @Autowired
    EntityManager entityManager;

    @Override
    public void save(Client client) {
        entityManager.persist(client);
    }

    @Override
    public Client findByBookingId(int bookingID) {
        Query query = entityManager.createQuery("select c from Client as c where c.bookingID=:id");
        query.setParameter("id", bookingID);
        return (Client) query.getSingleResult();
    }

    @Override
    public void updateByID(Client client) {
        entityManager.merge(client);
    }

    @Override
    public void deleteByBookingID(int bookingID) {
        Query q = entityManager.createQuery("delete from Client as c where c.bookingID=:id");
        q.setParameter("id", bookingID);
        q.executeUpdate();
    }
}
