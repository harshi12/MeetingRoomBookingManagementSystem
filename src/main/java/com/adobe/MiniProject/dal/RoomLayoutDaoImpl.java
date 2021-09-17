package com.adobe.MiniProject.dal;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import com.adobe.MiniProject.domain.RoomLayout;

@Repository
@Transactional
public class RoomLayoutDaoImpl implements RoomLayoutDao {

	@Autowired
	EntityManager entityManager;
	
	public RoomLayout save(RoomLayout roomLayout) {
		entityManager.persist(roomLayout);
		return roomLayout;
	}

	public List<RoomLayout> findAll() {
		Query query = entityManager.createQuery("SELECT r from RoomLayout r");
		return query.getResultList();
	}

	public void deleteById(int roomLayoutId) {
		Query query = entityManager.createQuery("DELETE from RoomLayout as r where "
				+ "r.id=:id");
		query.setParameter("id", roomLayoutId);
		query.executeUpdate();
	}

	public int updateById(int roomLayoutId, RoomLayout newRoomLayout) {
		
		entityManager.merge(newRoomLayout);
		return roomLayoutId;
		
	}

	public RoomLayout findById(int roomLayoutId) {
		return entityManager.find(RoomLayout.class, roomLayoutId);
	}

	@Nullable
	public RoomLayout findByTitle(String title) {
		Query query = entityManager.createQuery("SELECT r from RoomLayout r where r.title=:title");
		query.setParameter("title", title);
		@SuppressWarnings("unchecked")
		List<RoomLayout> listOfRoomLayouts = query.getResultList();
		if (listOfRoomLayouts.size() > 0) {
			return listOfRoomLayouts.get(0);
		} else {
			return null;
		}
	}

	@Override
	public int getIdFromTitle(String title) {
		Query query = entityManager.createQuery("SELECT r.id from RoomLayout r where r.title =:title");
		query.setParameter("title", title);
		return (Integer) query.getResultList().get(0);
	}

	@Override
	public String getTitleFromId(int roomLayoutId) {
		Query query = entityManager.createQuery("SELECT r.title from RoomLayout r where r.id =:rid");
		query.setParameter("rid", roomLayoutId);
		return (String) query.getResultList().get(0);
	}
}
