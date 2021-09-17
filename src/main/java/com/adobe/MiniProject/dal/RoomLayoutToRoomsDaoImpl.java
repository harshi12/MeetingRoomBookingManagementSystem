package com.adobe.MiniProject.dal;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.adobe.MiniProject.domain.RoomLayoutToRooms;

@Repository
@Transactional
public class RoomLayoutToRoomsDaoImpl implements RoomLayoutToRoomsDao {

	@Autowired
	EntityManager entityManager;
	
	public List<Integer> getRoomIdFromRLId(int roomLayoutId) { 
		Query query = entityManager.createQuery("SELECT r.roomId from RoomLayoutToRooms r where"
				+ " r.roomLayoutId=:rlid");
		query.setParameter("rlid", roomLayoutId);
		return query.getResultList();
	}

	public int addEntry(RoomLayoutToRooms roomLayoutToRoom) {
		entityManager.persist(roomLayoutToRoom);
		return roomLayoutToRoom.getId();
	}

	public void deleteByRoomLayoutId(int roomLayoutId) {
		Query query = entityManager.createQuery("DELETE from RoomLayoutToRooms r "
				+ " where r.roomLayoutId=:id");
		query.setParameter("id", roomLayoutId);
		query.executeUpdate();
	}

	@Override
	public List<Integer> getRoomLayoutIdFromRoomId(int roomId) {
		Query query = entityManager.createQuery("SELECT r.roomLayoutId from RoomLayoutToRooms r where"
				+ " r.roomId=:rlid");
		query.setParameter("rlid", roomId);
		return query.getResultList();
	}

	@Override
	public void deleteByRoomId(int roomId) {
		Query query = entityManager.createQuery("DELETE from RoomLayoutToRooms r "
				+ " where r.roomId=:id");
		query.setParameter("id", roomId);
		query.executeUpdate();
	}
}
