package com.adobe.MiniProject.service;

import java.util.List;

import com.adobe.MiniProject.domain.AdminLogin;

public interface AdminLoginService {
	int addNewAdmin(AdminLogin toBeAdded);
	void removeAdmin(int id);
	List<AdminLogin> findAll();
	AdminLogin findByUsername(String name);
	AdminLogin findById(int id);
	int updateAdminUser(AdminLogin toBeUpdated);
	AdminLogin findByToken(String token);
}
