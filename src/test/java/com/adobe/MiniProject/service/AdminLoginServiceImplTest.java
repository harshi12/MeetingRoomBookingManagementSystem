package com.adobe.MiniProject.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.times;
import org.mockito.junit.MockitoJUnitRunner;

import com.adobe.MiniProject.dal.AdminLoginDAO;
import com.adobe.MiniProject.domain.AdminLogin;

@RunWith(MockitoJUnitRunner.class)
public class AdminLoginServiceImplTest {
	@Mock
	AdminLoginDAO mockAdminDao;
	
	@Test
	public void addNewAdminTest() {
		AdminLoginServiceImpl service = new AdminLoginServiceImpl();
		AdminLogin toBeAdded = new AdminLogin(0, "test@gmail.com", "12345678", "PP", "active", "admin", null);
//		AdminLogin saved = new AdminLogin("test@gmail.com", "12345678", "PP", "active", "admin", null);
		toBeAdded.setId(1);
		
		Mockito.when(mockAdminDao.save(toBeAdded)).thenReturn(toBeAdded);
		service.setDao(mockAdminDao);
		int id = service.addNewAdmin(toBeAdded);
//		Mockito.verify(mockAdminDao);
		assertEquals(id, 1);
	}
	
	@Test
	public void getAllAdminTest() {
		AdminLoginServiceImpl service = new AdminLoginServiceImpl();
		AdminLogin obj1 = new AdminLogin();
		AdminLogin obj2 = new AdminLogin();
		List<AdminLogin> list = new ArrayList();
		list.add(obj1);
		list.add(obj2);
		Mockito.when(mockAdminDao.findAll()).thenReturn(list);
		service.setDao(mockAdminDao);
		assertEquals(2, service.findAll().size());
	}
	
	@Test
	public void getUserByUsernameTest() {
		AdminLoginServiceImpl service = new AdminLoginServiceImpl();
		String user = "test@gmail.com";
		AdminLogin obj1 = new AdminLogin(0, "test@gmail.com", "12345678", "PP", "active", "admin", null);
		
		Mockito.when(mockAdminDao.findByUsername(user)).thenReturn(obj1);
		service.setDao(mockAdminDao);
		assertEquals(user, service.findByUsername(user).getUsername());
	}
	
	@Test
	public void getUserByIdTest() {
		AdminLoginServiceImpl service = new AdminLoginServiceImpl();
		int id = 1;
		AdminLogin obj1 = new AdminLogin(id, "test@gmail.com", "12345678", "PP", "active", "admin", null);
		obj1.setId(id);
		
		Mockito.when(mockAdminDao.findById(id)).thenReturn(obj1);
		service.setDao(mockAdminDao);
		assertEquals(1, service.findById(id).getId());
	}
	
	@Test
	public void deleteAdminByIdTest() {
		AdminLoginServiceImpl service = new AdminLoginServiceImpl();
		AdminLogin obj1 = new AdminLogin(0, "test@gmail.com", "12345678", "PP", "active", "admin", null);
		int id = 1;
		obj1.setId(id);
		service.setDao(mockAdminDao);
		service.removeAdmin(id);
		Mockito.verify(mockAdminDao, times(1)).deleteById(id);
	}
}
