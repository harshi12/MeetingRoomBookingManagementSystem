package com.adobe.MiniProject.web;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.adobe.MiniProject.domain.AdminLogin;
import com.adobe.MiniProject.domain.Booking;
import com.adobe.MiniProject.service.AdminLoginService;
import com.adobe.MiniProject.service.AuthenticationService;
import com.adobe.MiniProject.service.BookingService;

@RunWith(SpringRunner.class)
@WebMvcTest({DashboardController.class})
public class DashboardControllerTest {
	@Autowired
	MockMvc mockMvc;
	
	@MockBean
	AdminLoginService mockAdminService;
	
	@MockBean
	BookingService mockBookingService;
	
	@MockBean
	AuthenticationService mockAuthenticationService;
	
	@Mock
	HttpServletRequest mockRequest;
	
	@Test
	public void	getDashboardDetailsTest() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	Date date = null;
		try {
			date = sdf.parse(sdf.format(new Date()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		int value = 0;
		AdminLogin admin = new AdminLogin();
		admin.setId(1);
		admin.setLastLogin(date);
    	List<Booking> list = new ArrayList<Booking>();
		Mockito.when(mockBookingService.findByCreatedOn(date)).thenReturn(list);
		Mockito.when(mockAuthenticationService.authenticate("sometoken")).thenReturn(true);
		Mockito.when(mockAdminService.findByToken("sometoken")).thenReturn(admin);
		mockMvc.perform(MockMvcRequestBuilders.get("/dashboard")
				.header("token","sometoken")
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers.status().is(200));
	}
	
	@Test
	public void	getDashboardDetailsTest_Wrong_Token() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	Date date = null;
		try {
			date = sdf.parse(sdf.format(new Date()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		int value = 0;
		AdminLogin admin = new AdminLogin();
		admin.setLastLogin(date);
    	List<Booking> list = new ArrayList<Booking>();
		Mockito.when(mockBookingService.findByCreatedOn(date)).thenReturn(list);
		Mockito.when(mockAuthenticationService.authenticate("sometoken1")).thenReturn(true);
		Mockito.when(mockAdminService.findByToken("sometoken1")).thenReturn(admin);
		mockMvc.perform(MockMvcRequestBuilders.get("/dashboard")
				.header("token","sometoken")
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers.status().is(401));
	}
	
	@Test
	public void reservationForDate_When_No_booking_found() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	Date date = null;
		try {
			date = sdf.parse("2020-09-17");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		List<Booking> list = new ArrayList<Booking>();
		list.add(new Booking());
		list.add(new Booking());
		System.out.println("###########   "+list.size());
		Mockito.when(mockBookingService.findByBookingDate(date)).thenReturn(list);
		Mockito.when(mockAuthenticationService.authenticate("sometoken")).thenReturn(true);
		mockMvc.perform(MockMvcRequestBuilders.post("/dashboard")
				.requestAttr("givenDate", "2020-09-16")
				.header("token","sometoken")
				.requestAttr("request", mockRequest))
		.andExpect(MockMvcResultMatchers.status().is(400));
	}
}
