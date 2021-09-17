package com.adobe.MiniProject.web;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.adobe.MiniProject.domain.Equipment;
import com.adobe.MiniProject.service.AuthenticationService;
import com.adobe.MiniProject.service.EquipmentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@WebMvcTest({EquipmentController.class})
public class EquipmentControllerTest {

	@Autowired
	MockMvc mockMvc;
	
	@MockBean
	EquipmentService equipmentService;
	
	@MockBean
	AuthenticationService authService;
	
	@Test
	public void getEquipmentByIdTest() throws Exception{
		Equipment equipmentReturned = new Equipment(-1,"Marker",2,true,false);
		equipmentReturned.setId(1);
		Mockito.when(equipmentService.findById(1)).thenReturn(equipmentReturned);
		Mockito.when(authService.authenticate("sometoken")).thenReturn(true);
		mockMvc.perform(MockMvcRequestBuilders.get("/equipments/{id}", 1).header("token","sometoken").
				accept(MediaType.APPLICATION_JSON).content(mapToJson(equipmentReturned))).
				andExpect(MockMvcResultMatchers.status().isOk());
	} 
	
	@Test
	public void getEquipmentById_Return_NOT_FOUND_For_Wrong_Id_Test() throws Exception{
		Equipment equipmentReturned = new Equipment(-1,"Marker",2,true,false);
		equipmentReturned.setId(1);
		Mockito.when(equipmentService.findById(2)).thenReturn(null);
		Mockito.when(authService.authenticate("sometoken")).thenReturn(true);
		mockMvc.perform(MockMvcRequestBuilders.get("/equipments/{id}", 2).header("token","sometoken").
				accept(MediaType.APPLICATION_JSON).content(mapToJson(equipmentReturned))).
				andExpect(MockMvcResultMatchers.status().isNotFound());
	} 
	
	@Test
	public void getAllEquipmentTest() throws Exception{
		Equipment equipment1 = new Equipment(1,"Marker",2,true,false);
		List<Equipment> listOfEquipment = new ArrayList<Equipment>();
		listOfEquipment.add(equipment1);
		Mockito.when(equipmentService.findAll()).thenReturn(listOfEquipment);
		Mockito.when(authService.authenticate("sometoken")).thenReturn(true);
		mockMvc.perform(MockMvcRequestBuilders.get("/equipments").header("token","sometoken").
				accept(MediaType.APPLICATION_JSON).content(mapToJson(listOfEquipment))).
				andExpect(MockMvcResultMatchers.status().isOk());
	} 
	
	@Test
	public void removeEquipmentTest() throws Exception{
		Equipment equipment = new Equipment(1,"Marker",2,true,false);
		equipment.setId(1);
		Mockito.when(authService.authenticate("sometoken")).thenReturn(true);
		Mockito.when(equipmentService.findById(1)).thenReturn(equipment);
		mockMvc.perform(MockMvcRequestBuilders.delete("/equipments/{id}", 1).header("token","sometoken").
				accept(MediaType.APPLICATION_JSON)).
				andExpect(MockMvcResultMatchers.status().isNoContent());
	} 
	
	@Test
	public void removeEquipment_Return_Not_Found_If_Id_is_Wrong_Test() throws Exception{
		Equipment equipment = new Equipment(1,"Marker",2,true,false);
		equipment.setId(1);
		Mockito.when(authService.authenticate("sometoken")).thenReturn(true);
		Mockito.when(equipmentService.findById(2)).thenReturn(null);
		mockMvc.perform(MockMvcRequestBuilders.delete("/equipments/{id}", 2).header("token","sometoken").
				accept(MediaType.APPLICATION_JSON)).
				andExpect(MockMvcResultMatchers.status().isNotFound());
	}
	
	@Test
	public void addNewEquipmentTest() throws Exception{
		Equipment equipment = new Equipment();
		equipment.setTitle("Marker");
		equipment.setPerHour(true);
		equipment.setCanBookMultiple(false);
		equipment.setPrice(20);
		Mockito.when(authService.authenticate("sometoken")).thenReturn(true);
		Mockito.when(equipmentService.getByTitle("Marker")).thenReturn(null);
		Mockito.when(equipmentService.addNewEquipment(equipment)).thenReturn(1);
		mockMvc.perform(MockMvcRequestBuilders.post("/equipments").header("token","sometoken").
				contentType(MediaType.APPLICATION_JSON).
				content(mapToJson(equipment)).
				accept(MediaType.APPLICATION_JSON)).
				andExpect(MockMvcResultMatchers.status().isCreated());
	} 
	
	@Test
	public void addNewEquipment_If_Already_Exists_Return_Bad_Request_Test() throws Exception{
		Equipment equipment = new Equipment();
		equipment.setTitle("Marker");
		equipment.setPerHour(true);
		equipment.setCanBookMultiple(false);
		equipment.setPrice(20);
		Mockito.when(authService.authenticate("sometoken")).thenReturn(true);
		Mockito.when(equipmentService.getByTitle("Marker")).thenReturn(equipment);
		Mockito.when(equipmentService.addNewEquipment(equipment)).thenReturn(1);
		mockMvc.perform(MockMvcRequestBuilders.post("/equipments").header("token","sometoken").
				contentType(MediaType.APPLICATION_JSON).
				content(mapToJson(equipment)).
				accept(MediaType.APPLICATION_JSON)).
				andExpect(MockMvcResultMatchers.status().isBadRequest());
	} 
	
	@Test
	public void updateExistingEquipmentTest() throws Exception{
		Equipment equipment = new Equipment();
		equipment.setId(1);
		equipment.setTitle("Marker");
		equipment.setPerHour(true);
		equipment.setCanBookMultiple(false);
		equipment.setPrice(20);
		Mockito.when(authService.authenticate("sometoken")).thenReturn(true);
		Mockito.when(equipmentService.getByTitle("Marker")).thenReturn(null);
		Mockito.when(equipmentService.findById(1)).thenReturn(equipment);
		Mockito.when(equipmentService.updateEquipment(1, equipment)).thenReturn(1);
		mockMvc.perform(MockMvcRequestBuilders.post("/equipments/{id}", 1).header("token","sometoken").
				contentType(MediaType.APPLICATION_JSON).
				content(mapToJson(equipment)).
				accept(MediaType.APPLICATION_JSON)).
				andExpect(MockMvcResultMatchers.status().isCreated());
	} 
	
	@Test
	public void updateExistingEquipment_Must_Return_Bad_Request_StatusCode_If_Title_Already_Exist_Test() throws Exception{
		Equipment equipment = new Equipment();
		Equipment equipmentWithSameTitle = new Equipment();
		Equipment equipmentFromId = new Equipment();
		equipmentFromId.setTitle("NotMarker");
		equipment.setId(1);
		equipment.setTitle("Marker");
		equipment.setPerHour(true);
		equipment.setCanBookMultiple(false);
		equipment.setPrice(20);
		Mockito.when(authService.authenticate("sometoken")).thenReturn(true);
		Mockito.when(equipmentService.getByTitle("Marker")).thenReturn(equipmentWithSameTitle);
		Mockito.when(equipmentService.findById(1)).thenReturn(equipmentFromId);
		Mockito.when(equipmentService.updateEquipment(1, equipment)).thenReturn(1);
		mockMvc.perform(MockMvcRequestBuilders.post("/equipments/{id}", 1).header("token","sometoken").
				contentType(MediaType.APPLICATION_JSON).
				content(mapToJson(equipment)).
				accept(MediaType.APPLICATION_JSON)).
				andExpect(MockMvcResultMatchers.status().isBadRequest());
	} 
	
	private String mapToJson(Object object) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.writeValueAsString(object);
	}
}
