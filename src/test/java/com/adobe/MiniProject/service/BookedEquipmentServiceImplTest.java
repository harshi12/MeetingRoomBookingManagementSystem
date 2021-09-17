package com.adobe.MiniProject.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.adobe.MiniProject.dal.BookedEquipmentDAOImpl;
import com.adobe.MiniProject.domain.BookedEquipment;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public class BookedEquipmentServiceImplTest {
	@Mock
	BookedEquipmentDAOImpl mockBookedEquipmentDao;
	
	@Mock
	EquipmentServiceImpl mockEquipmentService;
	
	@Test
	public void addNewBookedEquipmentTest() {
		BookedEquipmentServiceImpl bookedEquipmentService = new BookedEquipmentServiceImpl();
		BookedEquipment bookedEquipment = new BookedEquipment();
		bookedEquipment.setBookingID(1);
		int id = 1;
		List<BookedEquipment> bookedEquipments = new ArrayList<BookedEquipment>();
		bookedEquipments.add(bookedEquipment);
		bookedEquipmentService.setDAO(mockBookedEquipmentDao);
		bookedEquipmentService.setService(mockEquipmentService);
		bookedEquipmentService.addNewBookedEquipment(bookedEquipments, 1);
		Mockito.verify(mockBookedEquipmentDao, times(1)).save(bookedEquipments.get(0));
	}
	
	@Test
	public void updateBookedEquipmentTest() {
		BookedEquipmentServiceImpl bookedEquipmentService = new BookedEquipmentServiceImpl();
		BookedEquipment bookedEquipment = new BookedEquipment();
		bookedEquipment.setBookingID(1);
		int id = 1;
		List<BookedEquipment> bookedEquipments = new ArrayList<BookedEquipment>();
		bookedEquipments.add(bookedEquipment);
		bookedEquipmentService.setDAO(mockBookedEquipmentDao);
		bookedEquipmentService.setService(mockEquipmentService);
		bookedEquipmentService.updateBookedEquipment(bookedEquipments);
		Mockito.verify(mockBookedEquipmentDao, times(1)).updateByBookingID(bookedEquipments.get(0));
	}
	
	@Test
	public void getBookedEquipmentByBookingIDTest() {
		BookedEquipmentServiceImpl bookedEquipmentService = new BookedEquipmentServiceImpl();
		BookedEquipment bookedEquipment = new BookedEquipment();
		bookedEquipment.setBookingID(1);
		int id = 1;
		List<BookedEquipment> bookedEquipments = new ArrayList<BookedEquipment>();
		bookedEquipments.add(bookedEquipment);
		
		Mockito.when(mockBookedEquipmentDao.findAllByBookingID(id)).thenReturn(bookedEquipments);
		bookedEquipmentService.setDAO(mockBookedEquipmentDao);
		bookedEquipmentService.setService(mockEquipmentService);
		assertEquals(id, bookedEquipmentService.getBookedEquipmentByBookingID(id).get(0).getBookingID());
	}
	
	@Test
	public void deleteByBookingIDTest() {
		BookedEquipmentServiceImpl bookedEquipmentService = new BookedEquipmentServiceImpl();
		BookedEquipment bookedEquipment = new BookedEquipment();
		bookedEquipment.setBookingID(1);
		int id = 1;
		bookedEquipmentService.setDAO(mockBookedEquipmentDao);
		bookedEquipmentService.setService(mockEquipmentService);
		bookedEquipmentService.deleteByBookingID(id);
		Mockito.verify(mockBookedEquipmentDao, times(1)).deleteByBookingID(id);
	}
}
