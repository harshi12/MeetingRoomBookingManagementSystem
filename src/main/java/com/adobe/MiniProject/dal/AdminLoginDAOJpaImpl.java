package com.adobe.MiniProject.dal;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.adobe.MiniProject.domain.AdminLogin;

@Repository
@Transactional
public class AdminLoginDAOJpaImpl implements AdminLoginDAO {

	@Autowired
	EntityManager entityManager;
	
	@Override
	public AdminLogin save(AdminLogin toBeSaved) {
		entityManager.persist(toBeSaved);
		return toBeSaved;
	}

	@Override
	public AdminLogin findByUsername(String username) {
		return (AdminLogin) entityManager.createQuery("select t from AdminLogin t where t.username=:value1").setParameter("value1", username).getSingleResult();
	}

	@Override
	public List<AdminLogin> findAll() {
		Query q = entityManager.createQuery("select p from AdminLogin as p");
		return q.getResultList();
	}

	@Override
	public void deleteById(int id) {
		Query q = entityManager.createQuery("delete from AdminLogin as p where p.id=:pid");
		q.setParameter("pid", id);
		q.executeUpdate();
	}

	@Override
	public AdminLogin findById(int id) {
		return entityManager.find(AdminLogin.class, id);
	}

	@Override
	public int updateById(AdminLogin toBeUpdated) {
		entityManager.merge(toBeUpdated);
		return toBeUpdated.getId();
	}

	@Override
	public AdminLogin findByToken(String token) {
		return (AdminLogin) entityManager.createQuery("select t from AdminLogin t where t.token=:value1").setParameter("value1", token).getSingleResult();
	}

}
