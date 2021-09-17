package com.adobe.MiniProject.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.adobe.MiniProject.dal.BookingDAOImpl;
import com.adobe.MiniProject.dal.ClientDAOImpl;
import com.adobe.MiniProject.domain.Booking;
import com.adobe.MiniProject.domain.Client;
import com.adobe.MiniProject.domain.Equipment;
import com.adobe.MiniProject.domain.Food;

import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public class BookingServiceImplTest {
	@Mock
	BookingDAOImpl mockBookingDAO;
	
	@Mock
	ClientServiceImpl mockClientService;
	
	@Mock
	BookedEquipmentServiceImpl mockBookedEquipmentService;
	
	@Mock
	RoomService mockRoomService;
	
	@Mock
	RoomLayoutService mockRoomLayoutService;
	
	@Test
	public void addNewBookingTest() {
		BookingServiceImpl bookingService = new BookingServiceImpl();
		Booking booking = new Booking();
		booking.setBookingID(1);
		
		Mockito.when(mockBookingDAO.save(booking)).thenReturn(booking);
		bookingService.setBookingDao(mockBookingDAO);
		bookingService.setService(mockBookedEquipmentService, mockClientService, mockRoomService, mockRoomLayoutService);
		Booking finalBooking = bookingService.addNewBooking(booking);
		Mockito.verify(mockBookingDAO);
		assertEquals(1, finalBooking.getBookingID());
	}
	
	@Test
	public void findAllTest() {
		BookingServiceImpl bookingService = new BookingServiceImpl();
		Booking booking1 = new Booking();
		Booking booking2 = new Booking();
		List<Booking> list = new ArrayList();
		list.add(booking1);
		list.add(booking2);
		Mockito.when(mockBookingDAO.findAll()).thenReturn(list);
		bookingService.setBookingDao(mockBookingDAO);
		assertEquals(2, bookingService.findAll().size());
	}
	
	@Test
	public void findByIdTest() {
		BookingServiceImpl bookingService = new BookingServiceImpl();
		int id = 1;
		Booking booking = new Booking();
		booking.setBookingID(id);
		
		Mockito.when(mockBookingDAO.findById(id)).thenReturn(booking);
		bookingService.setBookingDao(mockBookingDAO);
		assertEquals(id, bookingService.findById(id).getBookingID());
	}
	
	@Test
	public void removeBookingTest() {
		BookingServiceImpl bookingService = new BookingServiceImpl();
		int id = 1;
		Booking booking = new Booking();
		booking.setBookingID(id);
		
		bookingService.setBookingDao(mockBookingDAO);
		bookingService.setService(mockBookedEquipmentService, mockClientService, mockRoomService, mockRoomLayoutService);
		bookingService.removeBooking(id);
		Mockito.verify(mockBookingDAO, times(1)).findById(id);
	}
	
	@Test
	public void findByCreatedOnTest() {
		BookingServiceImpl bookingService = new BookingServiceImpl();
		Date date = new Date();
		Booking booking1 = new Booking();
		booking1.setCreatedOn(date);
		List<Booking> list = new ArrayList();
		list.add(booking1);
		Mockito.when(mockBookingDAO.findByCreatedOn(date)).thenReturn(list);
		bookingService.setBookingDao(mockBookingDAO);
		assertEquals(date, bookingService.findByCreatedOn(date).get(0).getCreatedOn());
	}
	
	@Test
	public void findByBookingDateTest() {
		BookingServiceImpl bookingService = new BookingServiceImpl();
		Date date = new Date();
		Booking booking1 = new Booking();
		booking1.setBookingDate(date);
		List<Booking> list = new ArrayList();
		list.add(booking1);
		Mockito.when(mockBookingDAO.findByBookingDate(date)).thenReturn(list);
		bookingService.setBookingDao(mockBookingDAO);
		assertEquals(date, bookingService.findByBookingDate(date).get(0).getBookingDate());
	}
}
