package com.adobe.MiniProject.web;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.adobe.MiniProject.domain.RoomLayout;
import org.json.simple.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.adobe.MiniProject.domain.Room;
import com.adobe.MiniProject.service.AuthenticationService;
import com.adobe.MiniProject.service.RoomServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest({RoomController.class})
public class RoomControllerTest{

	@Autowired
	MockMvc mockMvc;
	
	@MockBean
	RoomServiceImpl roomService;
	
	@MockBean
	AuthenticationService authService;

	
	@Test
	public void getRoomsTest() throws Exception{
		Room Room1 = new Room();
		List<Room> listOfRoom = new ArrayList<Room>();
		listOfRoom.add(Room1);
		Mockito.when(roomService.getAllRooms()).thenReturn(listOfRoom);
		Mockito.when(authService.authenticate("sometoken")).thenReturn(true);
		mockMvc.perform(MockMvcRequestBuilders.get("/rooms").header("token","sometoken").
				accept(MediaType.APPLICATION_JSON).content(mapToJson(listOfRoom))).
				andExpect(status().isOk());
	} 
	
	@Test
	public void getRoomByIdTest() throws Exception{
		Room roomReturned = new Room();
		roomReturned.setId(1);
		Mockito.when(roomService.getById(1)).thenReturn(roomReturned);
		Mockito.when(authService.authenticate("sometoken")).thenReturn(true);
		mockMvc.perform(MockMvcRequestBuilders.get("/rooms/{id}", 1).header("token","sometoken").
				accept(MediaType.APPLICATION_JSON).content(mapToJson(roomReturned))).
				andExpect(status().isOk());
	} 
	
	@Test
	public void getRoomById_No_Room_Exist_With_Given_Id_Test() throws Exception{
		Room roomReturned = new Room();
		roomReturned.setId(1);
		Mockito.when(roomService.getById(2)).thenReturn(null);
		Mockito.when(authService.authenticate("sometoken")).thenReturn(true);
		mockMvc.perform(MockMvcRequestBuilders.get("/rooms/{id}", 2).header("token","sometoken").
				accept(MediaType.APPLICATION_JSON).content(mapToJson(roomReturned))).
				andExpect(status().isNotFound());
	}

	@Test
	public void removeRoomTest() throws Exception{
		Room room = new Room();
		room.setId(1);
		room.setTitle("room");
		Mockito.when(authService.authenticate("sometoken")).thenReturn(true);
		Mockito.when(roomService.getById(1)).thenReturn(room);
		mockMvc.perform(MockMvcRequestBuilders.delete("/rooms/{id}", 1).header("token","sometoken").
				accept(MediaType.APPLICATION_JSON)).
				andExpect(status().isNoContent());
	}

	@Test
	public void removeRoom_No_Room_Exist_With_Given_Id_Test() throws Exception{
		Room Room = new Room();
		Room.setId(1);
		Mockito.when(authService.authenticate("sometoken")).thenReturn(true);
		Mockito.when(roomService.getById(2)).thenReturn(null);
		mockMvc.perform(MockMvcRequestBuilders.delete("/rooms/{id}", 2).header("token","sometoken").
				accept(MediaType.APPLICATION_JSON)).
				andExpect(status().isNotFound());
	} 
	
	@Test
	public void addNewRoomTest() throws Exception{
		Room room = new Room();
		room.setId(1);
		room.setTitle("Room");
		Mockito.when(authService.authenticate("sometoken")).thenReturn(true);
		Mockito.when(roomService.getByTitle("Room")).thenReturn(null);
		Mockito.when(roomService.addNewRoom(room)).thenReturn(room);
		mockMvc.perform(MockMvcRequestBuilders.post("/rooms").header("token","sometoken").
				contentType(MediaType.APPLICATION_JSON).
				content(mapToJson(room)).
				accept(MediaType.APPLICATION_JSON)).
				andExpect(status().isCreated());
	} 
	
	@Test
	public void addNewRoom_Room_Already_Exist_With_Same_Title_Test() throws Exception{
		Room room = new Room();
		room.setId(1);
		room.setTitle("Room");
		Mockito.when(authService.authenticate("sometoken")).thenReturn(true);
		Mockito.when(roomService.getByTitle("Room")).thenReturn(room);
		Mockito.when(roomService.addNewRoom(room)).thenReturn(room);
		mockMvc.perform(MockMvcRequestBuilders.post("/rooms").header("token","sometoken").
				contentType(MediaType.APPLICATION_JSON).
				content(mapToJson(room)).
				accept(MediaType.APPLICATION_JSON)).
				andExpect(status().isBadRequest());
	}

	@Test
	public void updateExistingRoomTest() throws Exception{
		Room room = new Room();
		room.setTitle("Room");
		room.setImageSrc("/Room");
		Mockito.when(authService.authenticate("sometoken")).thenReturn(true);
		Mockito.when(roomService.getByTitle("Room")).thenReturn(null);
		Mockito.when(roomService.getById(1)).thenReturn(room);
		Mockito.when(roomService.updateRoom(1, room)).thenReturn(room);
		mockMvc.perform(MockMvcRequestBuilders.post("/rooms/{id}", 1).header("token","sometoken").
				contentType(MediaType.APPLICATION_JSON).
				content(mapToJson(room)).
				accept(MediaType.APPLICATION_JSON)).
				andExpect(status().isCreated());
	}

	@Test
	public void updateExistingRoom_Must_Return_Bad_Request_StatusCode_If_Title_Already_Exist_Test() throws Exception{
		Room room = new Room();
		room.setTitle("Room");
		Room roomWithSameTitle = new Room();
		Room roomFromId = new Room();
		roomFromId.setTitle("NotRoom");
		Mockito.when(authService.authenticate("sometoken")).thenReturn(true);
		Mockito.when(roomService.getByTitle("Room")).thenReturn(roomWithSameTitle);
		Mockito.when(roomService.getById(1)).thenReturn(roomFromId);
		Mockito.when(roomService.updateRoom(1, room)).thenReturn(room);
		mockMvc.perform(MockMvcRequestBuilders.post("/rooms/{id}", 1).header("token","sometoken").
				contentType(MediaType.APPLICATION_JSON).
				content(mapToJson(room)).
				accept(MediaType.APPLICATION_JSON)).
				andExpect(status().isBadRequest());
	} 

	@Test
	public void changeRoomStatusTest() throws Exception{
		Room room = new Room();
		JSONObject activeStatus = new JSONObject(); 
		activeStatus.put("status", "active");
		room.setId(1);
		room.setTitle("Room");
		Mockito.when(authService.authenticate("sometoken")).thenReturn(true);
		Mockito.when(roomService.getById(1)).thenReturn(room);
		mockMvc.perform(MockMvcRequestBuilders.post("/rooms/{id}/status", 1).header("token","sometoken").
				contentType(MediaType.APPLICATION_JSON).
				content(mapToJson(activeStatus))).
				andExpect(status().isOk());
	} 
	
	@Test
	public void changeRoomStatusTest_NOT_FOUND_IF_ROOM_DOES_NOT_EXIST_TEST() throws Exception{
		Room room = new Room();
		JSONObject activeStatus = new JSONObject(); 
		activeStatus.put("status", "active");
		room.setId(1);
		room.setTitle("Room");
		Mockito.when(authService.authenticate("sometoken")).thenReturn(true);
		Mockito.when(roomService.getById(1)).thenReturn(null);
		mockMvc.perform(MockMvcRequestBuilders.post("/rooms/{id}/status", 1).header("token","sometoken").
				contentType(MediaType.APPLICATION_JSON).
				content(mapToJson(activeStatus))).
				andExpect(status().isNotFound());
	} 

	@Test
	public void authenticationFailure() throws Exception{
		Room room = new Room();
		room.setId(1);
		room.setTitle("Room");
		Mockito.when(authService.authenticate("sometoken")).thenReturn(false);
		Mockito.when(roomService.getByTitle("Room")).thenReturn(null);
		Mockito.when(roomService.addNewRoom(room)).thenReturn(room);
		mockMvc.perform(MockMvcRequestBuilders.post("/rooms").header("token","sometoken").
				contentType(MediaType.APPLICATION_JSON).
				content(mapToJson(room)).
				accept(MediaType.APPLICATION_JSON)).
				andExpect(status().isUnauthorized());
	}

	@Test
	public void uploadFileTest() throws Exception{
		MockMultipartFile image = new MockMultipartFile("image", "image.png", MediaType.MULTIPART_FORM_DATA.toString() , "image".getBytes(StandardCharsets.UTF_8));
		Mockito.when(authService.authenticate("sometoken")).thenReturn(true);
		mockMvc.perform(MockMvcRequestBuilders.multipart("/rooms/image")
				.file(image)
				.header("token", "sometoken")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void uploadFileTest_Should_Give_BAD_REQUEST_If_Image_Is_Empty() throws Exception{
		MockMultipartFile image = new MockMultipartFile("image", "image.png",
				MediaType.MULTIPART_FORM_DATA.toString() , "".getBytes(StandardCharsets.UTF_8));
		Mockito.when(authService.authenticate("sometoken")).thenReturn(true);
		mockMvc.perform(MockMvcRequestBuilders.multipart("/rooms/image")
				.file(image)
				.header("token", "sometoken")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}
	
	private String mapToJson(Object object) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.writeValueAsString(object);
	}
}
