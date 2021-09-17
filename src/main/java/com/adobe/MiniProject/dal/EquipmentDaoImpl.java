package com.adobe.MiniProject.dal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import com.adobe.MiniProject.domain.Equipment;

@Repository
@Transactional
public class EquipmentDaoImpl implements EquipmentDao{
	
	//This is used to remove the warning of needs for unchecked conversion
	public static <T> List<T> castList(Class<? extends T> clazz, Collection<?> c) {
	    List<T> r = new ArrayList<T>(c.size());
	    for(Object o: c)
	      r.add(clazz.cast(o));
	    return r;
	}
	
	@Autowired
	EntityManager entityManager;
	
	public Equipment save(Equipment equipment) {
		entityManager.persist(equipment);
		return equipment;
	}

	public List<Equipment> findAll() {
		Query query = entityManager.createQuery("select e from Equipment e");
		return castList(Equipment.class, query.getResultList());
	}

	public void deleteById(int id) {
		Query query = entityManager.createQuery("delete from Equipment as e where e.id =:eid");
		query.setParameter("eid", id);
		query.executeUpdate();
	}

	public int updateById(int equipmentId, Equipment newEquipment) {
		entityManager.merge(newEquipment);
		return equipmentId;
	}

	public Equipment findById(int id) {
		return entityManager.find(Equipment.class, id);
	}

	public int getEquipmentIDByTitle(String title) {
		Query query = entityManager.createQuery("select id from Equipment as e where e.title =:title");
		query.setParameter("title", title);
		return (int) query.getSingleResult();
	}

  @Override
	public Equipment findByTitle(String title) {
		Query query = entityManager.createQuery("select e from Equipment e where e.title"
				+ "=:title");
		query.setParameter("title", title);
		List<Equipment> equipments = query.getResultList();
		if (equipments.size() > 0) {
			return equipments.get(0);
		} else {
			return null;
		}
	}
}
