package com.adobe.MiniProject.service;

import static org.mockito.Mockito.times;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.adobe.MiniProject.dal.RoomDao;
import com.adobe.MiniProject.dal.RoomLayoutDao;
import com.adobe.MiniProject.dal.RoomLayoutToRoomsDao;
import com.adobe.MiniProject.domain.RoomLayout;

import junit.framework.TestCase;

@RunWith(MockitoJUnitRunner.class)
public class RoomLayoutServiceTest extends TestCase {
	
	@Mock
	RoomDao mockRoomDao;
	@Mock
	RoomLayoutDao mockRoomLayoutDao;
	@Mock
	RoomLayoutToRoomsDao mockRoomLayoutToRoomDao;
	
	@Test
	public void getRoomLayouts_Should_Return_List_Of_RoomLayouts() {
		RoomLayoutServiceImpl service = new RoomLayoutServiceImpl();
		RoomLayout layout1 = new RoomLayout();
		RoomLayout layout2 = new RoomLayout();
		ArrayList<RoomLayout> roomlayouts = new ArrayList<RoomLayout>();
		roomlayouts.add(layout1);
		roomlayouts.add(layout2);
		service.setRoomLayoutDao(mockRoomLayoutDao);
		service.setRoomLayoutToRoomsDao(mockRoomLayoutToRoomDao);
		service.setRoomDao(mockRoomDao);
		Mockito.when(service.getRoomLayouts()).thenReturn(roomlayouts);
		assertEquals(service.getRoomLayouts().size(), 2);
	}
	
	@Test
	public void removeRoomLayout_Should_Delete_RoomLayout() {
		RoomLayoutServiceImpl service = new RoomLayoutServiceImpl();
		RoomLayout layout = new RoomLayout();
		layout.setId(1);
		service.setRoomLayoutDao(mockRoomLayoutDao);
		service.setRoomLayoutToRoomsDao(mockRoomLayoutToRoomDao);
		service.removeRoomLayout(layout.getId());
		Mockito.verify(mockRoomLayoutDao, times(1)).deleteById(1);
	}
	
	@Test
	public void getRoomLayoutById_Must_Return_RoomLayout_With_Given_Id() {
		RoomLayoutServiceImpl service = new RoomLayoutServiceImpl();
		RoomLayout layout = new RoomLayout();
		layout.setId(1);
		Mockito.when(mockRoomLayoutDao.findById(1)).thenReturn(layout);
		service.setRoomLayoutDao(mockRoomLayoutDao);
		service.setRoomLayoutToRoomsDao(mockRoomLayoutToRoomDao);
		service.setRoomDao(mockRoomDao);
		RoomLayout layout1 = service.getRoomLayoutById(1);
		assertTrue(layout1.toString().equals(layout.toString()));
	}
	
	@Test
	public void getByTitle_Must_Return_RoomLayout_With_Given_Title() {
		RoomLayoutServiceImpl service = new RoomLayoutServiceImpl();
		RoomLayout layout = new RoomLayout();
		layout.setTitle("title");
		Mockito.when(mockRoomLayoutDao.findByTitle("title")).thenReturn(layout);
		service.setRoomLayoutDao(mockRoomLayoutDao);
		RoomLayout layout1 = service.getByTitle("title");
		assertTrue(layout1.toString().equals(layout.toString()));
	}
}
