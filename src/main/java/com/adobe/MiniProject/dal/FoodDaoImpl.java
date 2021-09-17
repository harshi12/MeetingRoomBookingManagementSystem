package com.adobe.MiniProject.dal;

import com.adobe.MiniProject.domain.Booking;
import com.adobe.MiniProject.domain.Food;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class FoodDaoImpl implements  FoodDAO{
    @Autowired
    EntityManager entityManager;

    @Override
    public Food save(Food food) {
        entityManager.persist((food));
        return food;
    }

    @Override
    public List<Food> findAll() {
        Query q = entityManager.createQuery("select f from Food as f");
        return q.getResultList();
    }

    @Override
    public Food update(Food food) {
        entityManager.merge(food);
        return food;
    }

    @Override
    public void deleteByID(int foodID) {
        Query query = entityManager.createQuery("delete from Food as f where f.foodID=:id");
        query.setParameter("id", foodID);
        query.executeUpdate();
    }

    @Override
    public int getFoodIDByTitle(String title) {
        Query query = entityManager.createQuery("select foodID from Food as f where f.title =:title");
        query.setParameter("title", title);
        return (int) query.getSingleResult();
    }

    @Override
    public Food getByTitle(String title) {
        Query query = entityManager.createQuery("select f from Food as f where f.title =:title");
        query.setParameter("title", title);
        List<Food> foodList = query.getResultList();
        if (foodList.size() > 0) {
            return foodList.get(0);
        }
        return null;
    }

    @Override
    public Food findByID(int id) {
        return entityManager.find(Food.class, id);
    }
}
