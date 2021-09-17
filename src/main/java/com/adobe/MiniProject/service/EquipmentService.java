package com.adobe.MiniProject.service;

import java.util.List;

import com.adobe.MiniProject.domain.Equipment;

public interface EquipmentService {
	
	int addNewEquipment(Equipment equipment);
	void removeEquipment(int euipId);
	int updateEquipment(int equipId, Equipment newEquipement);
	List<Equipment> findAll();
	Equipment findById(int id);
	Equipment getByTitle(String title);
	int getEquipmentIDByTitle(String title);
}
