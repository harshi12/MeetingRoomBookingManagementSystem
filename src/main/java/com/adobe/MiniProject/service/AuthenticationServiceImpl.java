package com.adobe.MiniProject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adobe.MiniProject.dal.AdminLoginDAO;
import com.adobe.MiniProject.domain.AdminLogin;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
	AdminLoginDAO adminDao;

	@Autowired
	public void setDao(AdminLoginDAO dao) {
		this.adminDao = dao;
	}

	@Override
	public boolean authenticate(String token) {
		try {
			AdminLogin admin = adminDao.findByToken(token);
			if(admin!=null) {
				return true;
			}
			return false;
		}catch(Exception e) {
			System.out.println(e);
			return false;
		}
	}
}
