package com.adobe.MiniProject.dal;

import com.adobe.MiniProject.domain.Food;
import java.util.List;

public interface FoodDAO {
    Food save(Food food);
    List<Food> findAll();
    Food update(Food food);
    void deleteByID(int foodID);
    int getFoodIDByTitle(String title);
    Food getByTitle(String title);
    Food findByID(int id);
}
