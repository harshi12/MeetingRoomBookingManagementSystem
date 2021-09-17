package com.adobe.MiniProject.service;

import com.adobe.MiniProject.dal.FoodDAO;
import com.adobe.MiniProject.domain.Booking;
import com.adobe.MiniProject.domain.Food;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FoodServiceImpl implements FoodService{
    FoodDAO foodDAO;
    @Autowired
    public void setDAO(FoodDAO dao){
        this.foodDAO = dao;
    }

    @Override
    public Food addNewFood(Food food) {
        return foodDAO.save(food);
    }

    @Override
    public Food updateFood(Food food) {
        return foodDAO.update(food);
    }

    @Override
    public void removeFood(int foodID) {
        foodDAO.deleteByID(foodID);
    }

    @Override
    public List<Food> findAll() {
        return foodDAO.findAll();
    }

    @Override
    public int getFoodIDByTitle(String title) {
        return foodDAO.getFoodIDByTitle(title);
    }

    @Override
    public Food getByTitle(String title) {
        return foodDAO.getByTitle(title);
    }

    @Override
    public Food findById(int id) {
        return foodDAO.findByID(id);
    }
}
