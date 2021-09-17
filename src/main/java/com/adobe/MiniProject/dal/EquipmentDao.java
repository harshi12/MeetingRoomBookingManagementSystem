package com.adobe.MiniProject.dal;

import java.util.List;
import com.adobe.MiniProject.domain.Equipment;

public interface EquipmentDao {

	Equipment save(Equipment euipment);
	List<Equipment> findAll();
	int getEquipmentIDByTitle(String title);
	void deleteById(int equipmentId);
	int updateById(int equipmentId, Equipment newEquipment);
	Equipment findById(int equipmentId);
	Equipment findByTitle(String title);
}
