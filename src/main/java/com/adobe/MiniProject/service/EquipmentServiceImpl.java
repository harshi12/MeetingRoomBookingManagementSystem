package com.adobe.MiniProject.service;

import java.util.List;

import com.adobe.MiniProject.domain.BookedEquipment;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adobe.MiniProject.dal.EquipmentDao;
import com.adobe.MiniProject.domain.Equipment;

@Service
public class EquipmentServiceImpl implements EquipmentService{

	EquipmentDao equipmentDao;
	
	@Autowired
	public void setEquipmentDao(EquipmentDao equipment) {
		this.equipmentDao = equipment;
	}
	
	public int addNewEquipment(Equipment equipment) {
		Equipment newEquipment = equipmentDao.save(equipment);
		return newEquipment.getId();
	}

	public void removeEquipment(int equipId) {
		equipmentDao.deleteById(equipId);
	}

	public int updateEquipment(int equipmentId, Equipment newEquipment) {
		newEquipment.setId(equipmentId);
		return equipmentDao.updateById(equipmentId, newEquipment);
	}
	
	public Equipment findById(int id) {
		return equipmentDao.findById(id);
	}
	
	public List<Equipment> findAll() {
		return equipmentDao.findAll();
	}

	@Override
	public Equipment getByTitle(String title) {
		return equipmentDao.findByTitle(title);
	}

	@Override
	public int getEquipmentIDByTitle(String title) {
		return equipmentDao.getEquipmentIDByTitle(title);
	}
}
