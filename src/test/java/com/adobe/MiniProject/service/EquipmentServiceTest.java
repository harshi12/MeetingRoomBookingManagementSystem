package com.adobe.MiniProject.service;
import static org.mockito.Mockito.times;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.adobe.MiniProject.dal.EquipmentDao;
import com.adobe.MiniProject.domain.Equipment;
import com.adobe.MiniProject.domain.RoomLayout;

import junit.framework.TestCase;

@RunWith(MockitoJUnitRunner.class)
public class EquipmentServiceTest extends TestCase {
	
	@Mock
	EquipmentDao mockDao;
	
	@Test
	public void addNewEquipment_Must_Return_Valid_Id() {
		EquipmentServiceImpl service = new EquipmentServiceImpl();
		Equipment toBeAdded = new Equipment(-1,"Marker",2,true,false);
		Equipment saved = new Equipment(-1,"Marker",2,true,false);
		saved.setId(1);
		Mockito.when(mockDao.save(toBeAdded)).thenReturn(saved);
		service.setEquipmentDao(mockDao);
		int id = service.addNewEquipment(toBeAdded);
		Mockito.verify(mockDao);
		assertTrue(id == 1);
	}
	
	@Test
	public void findAll_Should_Return_List_Of_Equipments() {
		EquipmentServiceImpl service = new EquipmentServiceImpl();
		Equipment equipment = new Equipment(-1,"Marker",2,true,false);
		List<Equipment> lstOfEquips = new ArrayList<Equipment>();
		lstOfEquips.add(equipment);
		equipment.setId(1);
		Mockito.when(mockDao.findAll()).thenReturn(lstOfEquips);
		service.setEquipmentDao(mockDao);
		List<Equipment> returnedEquipList = service.findAll();
		Mockito.verify(mockDao);
		assertTrue(returnedEquipList.get(0).toString().equals(lstOfEquips.get(0).toString()));
	}
	
	@Test
	public void removeEquipment_Must_Remove_Equipment_With_Given_Id() {
		EquipmentServiceImpl service = new EquipmentServiceImpl();
		Equipment equipment = new Equipment(-1,"Marker",2,true,false);
		equipment.setId(1);
		service.setEquipmentDao(mockDao);
		service.removeEquipment(equipment.getId());
		Mockito.verify(mockDao, times(1)).deleteById(1);
	}
	
	@Test
	public void findById_Must_Return_Equipment_With_Given_Id() {
		EquipmentServiceImpl service = new EquipmentServiceImpl();
		Equipment equipment = new Equipment(-1,"Marker",2,true,false);
		equipment.setId(1);
		Mockito.when(mockDao.findById(1)).thenReturn(equipment);
		service.setEquipmentDao(mockDao);
		Equipment equip = service.findById(1);
		assertTrue(equip.toString().equals(equipment.toString()));
	}
	
	@Test
	public void getByTitle_Must_Return_Equipment_With_Given_Title() {
		EquipmentServiceImpl service = new EquipmentServiceImpl();
		Equipment equipment = new Equipment();
		equipment.setTitle("title");
		Mockito.when(mockDao.findByTitle("title")).thenReturn(equipment);
		service.setEquipmentDao(mockDao);
		Equipment returnEquipment = service.getByTitle("title");
		assertTrue(returnEquipment.toString().equals(equipment.toString()));
	}
	
	@Test
	public void updateEquipment_Must_Return_Equipment_Id() {
		EquipmentServiceImpl service = new EquipmentServiceImpl();
		Equipment equipment = new Equipment();
		Equipment updatedEquip = new Equipment(1,"Marker",2,true,false);
		equipment.setId(1);
		Mockito.when(mockDao.updateById(1, updatedEquip))
				.thenReturn(equipment.getId());
		service.setEquipmentDao(mockDao);
		int id = service.updateEquipment(1, updatedEquip);
		Mockito.verify(mockDao);
		assertTrue(id == 1);
	}
}