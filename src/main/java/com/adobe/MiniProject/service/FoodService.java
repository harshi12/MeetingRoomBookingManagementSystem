package com.adobe.MiniProject.service;

import com.adobe.MiniProject.domain.Food;
import java.util.List;

public interface FoodService {
    Food addNewFood(Food food);
    Food updateFood(Food food);
    void removeFood(int foodID);
    List<Food> findAll();
    int getFoodIDByTitle(String title);
    Food getByTitle(String title);
    Food findById(int id);
}
