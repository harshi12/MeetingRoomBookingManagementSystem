package com.adobe.MiniProject.web;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.adobe.MiniProject.domain.AdminLogin;
import com.adobe.MiniProject.service.AdminLoginService;
import com.adobe.MiniProject.service.AuthenticationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@WebMvcTest({AdminLoginController.class})
public class AdminLoginControllerTest {
	@Autowired
	MockMvc mockMvc;
	
	@MockBean
	AdminLoginService mockAdminService;
	
	@MockBean
	AuthenticationService mockAuthenticationService;
	
	@Test
	public void	addNewAdminTest() throws Exception {
		String user = "user@gmail.com";
		AdminLogin admin = new AdminLogin();
		admin.setId(1);
		admin.setUsername(user);
		
		Mockito.when(mockAdminService.findByUsername(user)).thenReturn(null);
		Mockito.when(mockAuthenticationService.authenticate("sometoken")).thenReturn(true);
		
		mockMvc.perform(MockMvcRequestBuilders.post("/addAdmin")
				.contentType(MediaType.APPLICATION_JSON)
	            .content(mapToJson(admin))
				.header("token","sometoken"))
		.andExpect(MockMvcResultMatchers.status().is(201));
	}
	
	@Test
	public void	addNewAdminTest_Already_Exists_Not_Created() throws Exception {
		String user = "user@gmail.com";
		AdminLogin admin = new AdminLogin();
		admin.setId(1);
		admin.setUsername(user);
		Mockito.when(mockAdminService.findByUsername(user)).thenReturn(admin);
		Mockito.when(mockAuthenticationService.authenticate("sometoken")).thenReturn(true);
		mockMvc.perform(MockMvcRequestBuilders.post("/addAdmin")
				.contentType(MediaType.APPLICATION_JSON)
	            .content(mapToJson(admin))
				.header("token","sometoken"))
		.andExpect(MockMvcResultMatchers.status().is(409));
	}
	
	@Test
	public void successfulLoginTest() throws JsonProcessingException, Exception {
		String user = "user@gmail.com";
		AdminLogin admin = new AdminLogin();
		admin.setId(1);
		admin.setUsername(user);
		admin.setPassword("12345678");
		Mockito.when(mockAdminService.findByUsername(user)).thenReturn(admin);
		JSONObject obj = new JSONObject();
		obj.put("username",user);
		obj.put("password","12345678");
		mockMvc.perform(MockMvcRequestBuilders.post("/login")
				.contentType(MediaType.APPLICATION_JSON)
	            .content(mapToJson(obj)))
		.andExpect(MockMvcResultMatchers.status().is(200));
	}
	
	@Test
	public void incorrectPasswordLoginTest() throws JsonProcessingException, Exception {
		String user = "user@gmail.com";
		AdminLogin admin = new AdminLogin();
		admin.setId(1);
		admin.setUsername(user);
		admin.setPassword("12345678");
		Mockito.when(mockAdminService.findByUsername(user)).thenReturn(admin);
		JSONObject obj = new JSONObject();
		obj.put("username",user);
		obj.put("password","wrongPass");
		mockMvc.perform(MockMvcRequestBuilders.post("/login")
				.contentType(MediaType.APPLICATION_JSON)
	            .content(mapToJson(obj)))
		.andExpect(MockMvcResultMatchers.status().is(403));
	}
	
	@Test
	public void incorrectUserIDForLoginTest() throws JsonProcessingException, Exception {
		String user = "user@gmail.com";
		AdminLogin admin = new AdminLogin();
		admin.setId(1);
		admin.setUsername(user);
		admin.setPassword("12345678");
		Mockito.when(mockAdminService.findByUsername(user)).thenReturn(admin);
		JSONObject obj = new JSONObject();
		obj.put("username","user@hotmail.com");
		obj.put("password","12345678");
		mockMvc.perform(MockMvcRequestBuilders.post("/login")
				.contentType(MediaType.APPLICATION_JSON)
	            .content(mapToJson(obj)))
		.andExpect(MockMvcResultMatchers.status().is(404));
	}
	
	@Test
	public void getAllAdminTest() throws JsonProcessingException, Exception {
		List<AdminLogin> list = new ArrayList<AdminLogin>();
		AdminLogin admin = new AdminLogin();
		list.add(admin);
		Mockito.when(mockAdminService.findAll()).thenReturn(list);
		Mockito.when(mockAuthenticationService.authenticate("sometoken")).thenReturn(true);
		mockMvc.perform(MockMvcRequestBuilders.get("/admin")
				.contentType(MediaType.APPLICATION_JSON)
	            .content(mapToJson(list))
				.header("token","sometoken"))
		.andExpect(MockMvcResultMatchers.status().is(200));
	}
	
	@Test
	public void	updateAdminTest() throws Exception {
		String user = "user@gmail.com";
		AdminLogin admin = new AdminLogin();
		admin.setId(1);
		admin.setUsername(user);
		admin.setPassword("12345678");
		Mockito.when(mockAdminService.findByUsername(user)).thenReturn(admin);
		admin.setPassword("1234567890");
		Mockito.when(mockAuthenticationService.authenticate("sometoken")).thenReturn(true);
		
		mockMvc.perform(MockMvcRequestBuilders.post("/updateAdminCreds")
				.contentType(MediaType.APPLICATION_JSON)
	            .content(mapToJson(admin))
				.header("token","sometoken"))
		.andExpect(MockMvcResultMatchers.status().is(200));
	}
	
	@Test
	public void	updateAdmin_UserID_Changed_Test() throws Exception {
		String user = "user@gmail.com";
		AdminLogin admin = new AdminLogin();
		admin.setId(1);
		admin.setUsername(user);
		admin.setPassword("12345678");
		Mockito.when(mockAdminService.findByUsername(user)).thenReturn(admin);
		admin.setUsername("user1@gmail.com");
		Mockito.when(mockAuthenticationService.authenticate("sometoken")).thenReturn(true);
		
		mockMvc.perform(MockMvcRequestBuilders.post("/updateAdminCreds")
				.contentType(MediaType.APPLICATION_JSON)
	            .content(mapToJson(admin))
				.header("token","sometoken"))
		.andExpect(MockMvcResultMatchers.status().is(404));
	}
	
	@Test
	public void forgetpassword() throws Exception {
		String user = "user@gmail.com";
		AdminLogin admin = new AdminLogin();
		admin.setId(1);
		admin.setUsername(user);
		admin.setPassword("12345678");
		JSONObject obj = new JSONObject();
		obj.put("username",user);
		Mockito.when(mockAdminService.findByUsername(user)).thenReturn(admin);
		
		mockMvc.perform(MockMvcRequestBuilders.post("/forgetpasswd")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapToJson(obj)))
		.andExpect(MockMvcResultMatchers.status().is(200));
	}
	
	@Test
	public void forgetpassword_User_Not_Found() throws Exception {
		String user = "user@gmail.com";
		AdminLogin admin = new AdminLogin();
		admin.setId(1);
		admin.setUsername(user);
		admin.setPassword("12345678");
		JSONObject obj = new JSONObject();
		obj.put("username",user);
		Mockito.when(mockAdminService.findByUsername("user1@gmail.com")).thenReturn(admin);
		
		mockMvc.perform(MockMvcRequestBuilders.post("/forgetpasswd")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapToJson(obj)))
		.andExpect(MockMvcResultMatchers.status().is(404));
	}
	
	private String mapToJson(Object object) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.writeValueAsString(object);
	}
}
