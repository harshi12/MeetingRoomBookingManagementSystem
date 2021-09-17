package com.adobe.MiniProject.service;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.adobe.MiniProject.dal.RoomDaoImpl;
import com.adobe.MiniProject.dal.RoomLayoutDaoImpl;
import com.adobe.MiniProject.dal.RoomLayoutToRoomsDao;
import com.adobe.MiniProject.domain.Room;

import junit.framework.TestCase;

@RunWith(MockitoJUnitRunner.class)
public class RoomServiceTest extends TestCase {

	@Mock
	RoomDaoImpl mockRoomDao;
	@Mock
	RoomLayoutDaoImpl mockRoomLayoutDao;
	@Mock
	RoomLayoutToRoomsDao mockRoomLayoutToRoomDao;
	@Mock
	BookingServiceImpl bookingServiceImpl;
	
	@Test
	public void getAllRooms_Should_Return_List_Of_Rooms() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		RoomServiceImpl service = new RoomServiceImpl();
		Room roomOne = (Room) Class.forName("com.adobe.MiniProject.domain.Room").newInstance();
		Room roomTwo = (Room) Class.forName("com.adobe.MiniProject.domain.Room").newInstance();
		ArrayList<Room> rooms = new ArrayList<Room>();
		rooms.add(roomOne);
		rooms.add(roomTwo);
		service.setRoomLayoutDao(mockRoomLayoutDao);
		service.setRoomLayoutToRoomsDao(mockRoomLayoutToRoomDao);
		service.setRoomDao(mockRoomDao);
		Mockito.when(service.getAllRooms()).thenReturn(rooms);
		assertEquals(service.getAllRooms().size(), 2);
	}
	
	@Test
	public void getByTitle_Must_Return_Room_With_Given_Title() {
		RoomServiceImpl service = new RoomServiceImpl();
		Room room = new Room();
		room.setTitle("title");
		Mockito.when(mockRoomDao.findByTitle("title")).thenReturn(room);
		service.setRoomDao(mockRoomDao);
		Room room1 = service.getByTitle("title");
		assertTrue(room1.getTitle() == room.getTitle());
	}
	
}
