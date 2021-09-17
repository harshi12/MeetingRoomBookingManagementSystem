package com.adobe.MiniProject.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.lang.Nullable;

import com.adobe.MiniProject.dal.AdminLoginDAO;
import com.adobe.MiniProject.domain.AdminLogin;

@Service
public class AdminLoginServiceImpl implements AdminLoginService{

	AdminLoginDAO adminDao;

	@Autowired
	public void setDao(AdminLoginDAO dao) {
		this.adminDao = dao;
	}

	@Override
	public int addNewAdmin(AdminLogin toBeAdded) {
		if(toBeAdded.getPassword().length() >= 8) {
			AdminLogin newuser = adminDao.save(toBeAdded);
			return newuser.getId();
		}else {
			throw new IllegalArgumentException("Password must longer than 8 characters.");
		}
	}

	@Override
	public void removeAdmin(int id) {
		adminDao.deleteById(id);
	}

	@Override
	public List<AdminLogin> findAll() {
		return adminDao.findAll();
	}

	@Override
	@Nullable public AdminLogin findByUsername(String name) {
		try {
			return adminDao.findByUsername(name);
		}catch(Exception e) {
			System.out.println(e);
			return null;
		}
	}
	
	@Override
	public AdminLogin findById(int id) {
		return adminDao.findById(id);
	}

	@Override
	public int updateAdminUser(AdminLogin toBeUpdated) {
		return adminDao.updateById(toBeUpdated);
	}

	@Override
	public AdminLogin findByToken(String token) {
		try {
			return adminDao.findByToken(token);
		}catch(Exception e) {
			System.out.println(e);
			return null;
		}
	}
}
