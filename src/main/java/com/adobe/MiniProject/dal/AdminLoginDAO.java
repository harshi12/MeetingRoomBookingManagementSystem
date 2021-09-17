package com.adobe.MiniProject.dal;

import java.util.List;

import com.adobe.MiniProject.domain.AdminLogin;

public interface AdminLoginDAO {
	AdminLogin save(AdminLogin toBeSaved);
	AdminLogin findByUsername(String username);
	AdminLogin findById(int id);
	List<AdminLogin> findAll();
	void deleteById(int id);
	int updateById(AdminLogin toBeUpdated);
	AdminLogin findByToken(String token);
}
