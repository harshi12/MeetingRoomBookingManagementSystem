package com.adobe.MiniProject.dal;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.lang.Nullable;
import com.adobe.MiniProject.domain.Room;

@Repository
@Transactional
public class RoomDaoImpl implements RoomDao {

	@Autowired
	EntityManager entityManager;

	public String getTitleFromId(int roomId) {
		Query query = entityManager.createQuery("SELECT r.title from Room r where r.roomId =:rid");
		query.setParameter("rid", roomId);
		return (String) query.getResultList().get(0);
	}

	public int getIdFromTitle(String title) {
		Query query = entityManager.createQuery("SELECT r.roomId from Room r where r.title =: title");
		query.setParameter("title", title);
		return (Integer) query.getResultList().get(0);
	}

	@Override
	public Room save(Room newRoom) {
		entityManager.persist(newRoom);
		return newRoom;
	}

	@Override
	@Nullable
	public Room findByTitle(String title) {
		Query query = entityManager.createQuery("SELECT r from Room r where r.title=:title");
		query.setParameter("title", title);
		@SuppressWarnings("unchecked")
		List<Room> listOfRooms = query.getResultList();
		if (listOfRooms.size() > 0) {
			Room room = listOfRooms.get(0);
			room.setBookingCount(room.getBookings().size());
			return room;
		} else {
			return null;
		}
	}

	@Override
	public Room findById(int roomId) {
		Room room = entityManager.find(Room.class, roomId);
		if(room != null)
			room.setBookingCount(room.getBookings().size());
		return room;
	}

	@Override
	public List<Room> findAll() {
		Query query = entityManager.createQuery("SELECT r from Room r");
		List<Room> roomList = query.getResultList();
		for(Room room: roomList){
			room.setBookingCount(room.getBookings().size());
		}
		return roomList;
	}

	@Override
	public Room updateById(int roomId, Room room) {
		room.setBookingCount(room.getBookings().size());
		entityManager.merge(room);
		return room;
	}

	@Override
	public void deleteById(int roomId) {
		Query query = entityManager.createQuery("DELETE from Room as r where "
				+ "r.roomId=:id");
		query.setParameter("id", roomId);
		query.executeUpdate();
	}

	@Override
	public void addBookingID(Room room, int bookingID) {
		room.getBookings().add(bookingID);
		room.setBookingCount(room.getBookings().size());
	}


	@Override
	@Nullable
	public Room getRoomFromTitle(String roomName) {
		Query query = entityManager.createQuery("select r from Room r where r.title"
				+ "=:title");
		query.setParameter("title", roomName);
		List<Room> roomList= query.getResultList();
		if (roomList.size() > 0) {
			return roomList.get(0);
		} else {
			return null;
		}
	}

	@Override
	public void deleteBookingID(Room room, int bookingID) {
		room.getBookings().remove(bookingID);
		room.setBookingCount(room.getBookings().size());
	}
}
