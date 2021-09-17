package com.adobe.MiniProject.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.adobe.MiniProject.domain.RoomLayout;
import com.adobe.MiniProject.service.AuthenticationService;
import com.adobe.MiniProject.service.RoomLayoutService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest({RoomLayoutController.class})
public class RoomLayoutControllerTest{

	@Autowired
	MockMvc mockMvc;
	
	@MockBean
	RoomLayoutService roomLayoutService;
	
	@MockBean
	AuthenticationService authService;

	
	@Test
	public void getRoomLayoutsTest() throws Exception{
		RoomLayout roomLayout1 = new RoomLayout();
		List<RoomLayout> listOfRoomLayout = new ArrayList<RoomLayout>();
		listOfRoomLayout.add(roomLayout1);
		Mockito.when(roomLayoutService.getRoomLayouts()).thenReturn(listOfRoomLayout);
		Mockito.when(authService.authenticate("sometoken")).thenReturn(true);
		mockMvc.perform(MockMvcRequestBuilders.get("/roomlayouts").header("token","sometoken").
				accept(MediaType.APPLICATION_JSON).content(mapToJson(listOfRoomLayout))).
				andExpect(status().isOk());
	} 
	
	@Test
	public void getRoomLayoutByIdTest() throws Exception{
		RoomLayout roomLayoutReturned = new RoomLayout();
		roomLayoutReturned.setId(1);
		Mockito.when(roomLayoutService.getRoomLayoutById(1)).thenReturn(roomLayoutReturned);
		Mockito.when(authService.authenticate("sometoken")).thenReturn(true);
		mockMvc.perform(MockMvcRequestBuilders.get("/roomlayouts/{id}", 1).header("token","sometoken").
				accept(MediaType.APPLICATION_JSON).content(mapToJson(roomLayoutReturned))).
				andExpect(status().isOk());
	} 
	
	@Test
	public void getRoomLayoutById_No_RoomLayout_Exist_With_Given_Id_Test() throws Exception{
		RoomLayout roomLayoutReturned = new RoomLayout();
		roomLayoutReturned.setId(1);
		Mockito.when(roomLayoutService.getRoomLayoutById(2)).thenReturn(null);
		Mockito.when(authService.authenticate("sometoken")).thenReturn(true);
		mockMvc.perform(MockMvcRequestBuilders.get("/roomlayouts/{id}", 2).header("token","sometoken").
				accept(MediaType.APPLICATION_JSON).content(mapToJson(roomLayoutReturned))).
				andExpect(status().isNotFound());
	} 
	
	@Test
	public void removeRoomLayoutTest() throws Exception{
		RoomLayout roomLayout = new RoomLayout();
		roomLayout.setId(1);
		roomLayout.setTitle("layout");
		Mockito.when(authService.authenticate("sometoken")).thenReturn(true);
		Mockito.when(roomLayoutService.getRoomLayoutById(1)).thenReturn(roomLayout);
		mockMvc.perform(MockMvcRequestBuilders.delete("/roomlayouts/{id}", 1).header("token","sometoken").
				accept(MediaType.APPLICATION_JSON)).
				andExpect(status().isNoContent());
	}
	
	@Test
	public void removeRoomLayout_No_RoomLayout_Exist_With_Given_Id_Test() throws Exception{
		RoomLayout roomLayout = new RoomLayout();
		roomLayout.setId(1);
		Mockito.when(authService.authenticate("sometoken")).thenReturn(true);
		Mockito.when(roomLayoutService.getRoomLayoutById(2)).thenReturn(null);
		mockMvc.perform(MockMvcRequestBuilders.delete("/roomlayouts/{id}", 2).header("token","sometoken").
				accept(MediaType.APPLICATION_JSON)).
				andExpect(status().isNotFound());
	} 
	
	@Test
	public void addNewRoomLayoutTest() throws Exception{
		RoomLayout roomLayout = new RoomLayout();
		roomLayout.setId(1);
		roomLayout.setTitle("Layout");
		Mockito.when(authService.authenticate("sometoken")).thenReturn(true);
		Mockito.when(roomLayoutService.getByTitle("Layout")).thenReturn(null);
		Mockito.when(roomLayoutService.addRoomLayout(roomLayout)).thenReturn(1);
		mockMvc.perform(MockMvcRequestBuilders.post("/roomlayouts").header("token","sometoken").
				contentType(MediaType.APPLICATION_JSON).
				content(mapToJson(roomLayout)).
				accept(MediaType.APPLICATION_JSON)).
				andExpect(status().isCreated());
	} 
	
	@Test
	public void addNewRoomLayout_Layout_Already_Exist_With_Same_Title_Test() throws Exception{
		RoomLayout roomLayout = new RoomLayout();
		roomLayout.setId(1);
		roomLayout.setTitle("Layout");
		Mockito.when(authService.authenticate("sometoken")).thenReturn(true);
		Mockito.when(roomLayoutService.getByTitle("Layout")).thenReturn(roomLayout);
		Mockito.when(roomLayoutService.addRoomLayout(roomLayout)).thenReturn(1);
		mockMvc.perform(MockMvcRequestBuilders.post("/roomlayouts").header("token","sometoken").
				contentType(MediaType.APPLICATION_JSON).
				content(mapToJson(roomLayout)).
				accept(MediaType.APPLICATION_JSON)).
				andExpect(status().isBadRequest());
	} 
	
	@Test
	public void updateExistingRoomLayoutTest() throws Exception{
		RoomLayout roomLayout = new RoomLayout();
		roomLayout.setTitle("Layout");
		roomLayout.setImageSrc("/Layout");
		Mockito.when(authService.authenticate("sometoken")).thenReturn(true);
		Mockito.when(roomLayoutService.getByTitle("Layout")).thenReturn(null);
		Mockito.when(roomLayoutService.getRoomLayoutById(1)).thenReturn(roomLayout);
		Mockito.when(roomLayoutService.updateRoomLayout(1, roomLayout)).thenReturn(1);
		mockMvc.perform(MockMvcRequestBuilders.post("/roomlayouts/{id}", 1).header("token","sometoken").
				contentType(MediaType.APPLICATION_JSON).
				content(mapToJson(roomLayout)).
				accept(MediaType.APPLICATION_JSON)).
				andExpect(status().isCreated());
	}
	
	@Test
	public void updateExistingRoomLayout_Must_Return_Bad_Request_StatusCode_If_Title_Already_Exist_Test() throws Exception{
		RoomLayout roomLayout = new RoomLayout();
		roomLayout.setTitle("Layout");
		RoomLayout roomLayoutWithSameTitle = new RoomLayout();
		RoomLayout roomLayoutFromId = new RoomLayout();
		roomLayoutFromId.setTitle("Not Layout");
		Mockito.when(authService.authenticate("sometoken")).thenReturn(true);
		Mockito.when(roomLayoutService.getByTitle("Layout")).thenReturn(roomLayoutWithSameTitle);
		Mockito.when(roomLayoutService.getRoomLayoutById(1)).thenReturn(roomLayoutFromId);
		Mockito.when(roomLayoutService.updateRoomLayout(1, roomLayout)).thenReturn(1);
		mockMvc.perform(MockMvcRequestBuilders.post("/roomlayouts/{id}", 1).header("token","sometoken").
				contentType(MediaType.APPLICATION_JSON).
				content(mapToJson(roomLayout)).
				accept(MediaType.APPLICATION_JSON)).
				andExpect(status().isBadRequest());
	}

	@Test
	public void uploadFileTest() throws Exception{
		MockMultipartFile image = new MockMultipartFile("image", "image.png", MediaType.MULTIPART_FORM_DATA.toString() , "image".getBytes(StandardCharsets.UTF_8));
		Mockito.when(authService.authenticate("sometoken")).thenReturn(true);
		mockMvc.perform(MockMvcRequestBuilders.multipart("/roomlayouts/image")
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
		mockMvc.perform(MockMvcRequestBuilders.multipart("/roomlayouts/image")
				.file(image)
				.header("token", "sometoken")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void authenticationFailure() throws Exception{
		RoomLayout roomLayout = new RoomLayout();
		roomLayout.setId(1);
		roomLayout.setTitle("Layout");
		Mockito.when(authService.authenticate("sometoken")).thenReturn(false);
		Mockito.when(roomLayoutService.getByTitle("Layout")).thenReturn(null);
		Mockito.when(roomLayoutService.addRoomLayout(roomLayout)).thenReturn(1);
		mockMvc.perform(MockMvcRequestBuilders.post("/roomlayouts").header("token","sometoken").
				contentType(MediaType.APPLICATION_JSON).
				content(mapToJson(roomLayout)).
				accept(MediaType.APPLICATION_JSON)).
				andExpect(status().isUnauthorized());
	}

	private String mapToJson(Object object) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.writeValueAsString(object);
	}
}
