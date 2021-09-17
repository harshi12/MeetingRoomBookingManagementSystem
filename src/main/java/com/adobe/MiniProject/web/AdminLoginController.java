package com.adobe.MiniProject.web;
import java.net.URI;
import java.util.Date;
import java.util.UUID;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.adobe.MiniProject.domain.AdminLogin;
import com.adobe.MiniProject.errorcodes.AdminErrorCode;
import com.adobe.MiniProject.errorcodes.Constants;
import com.adobe.MiniProject.service.AdminLoginService;
import com.adobe.MiniProject.service.AuthenticationService;
import com.adobe.MiniProject.service.EmailService;

@RestController
@CrossOrigin
public class AdminLoginController {
	
	AdminLoginService adminService;
	
	@Autowired
	public void setService(AdminLoginService service) {
		this.adminService = service;
	}
	
	AuthenticationService authenticationService;
	
	@Autowired
	public void setService(AuthenticationService service) {
		this.authenticationService = service;
	}
	
	@PostMapping("/addAdmin")
	public ResponseEntity addNewAdmin(@RequestBody AdminLogin toBeAdded, @RequestHeader(required=false) String token) throws UnsatisfiedServletRequestParameterException  {
//		ResponseEntity exception = verification(token);
//		if(exception!=null) {
//			return exception;
//		}
		AdminLogin user = adminService.findByUsername(toBeAdded.getUsername());
		if(user==null) {
			toBeAdded.setRegistrationDate(new Date());
			toBeAdded.setLastLogin(new Date());
			toBeAdded.setStatus("Inactive");
			int id = adminService.addNewAdmin(toBeAdded);
			HttpHeaders headers = new HttpHeaders();
			headers.setLocation(URI.create("/addAdmin/"));
			return new ResponseEntity<String>("Admin added successfully",headers,HttpStatus.CREATED);
		} else {
			JSONObject error = new JSONObject();
			error.put(Constants.ERROR_CODE_KEY, AdminErrorCode.USER_EXISTS_CANNOT_BE_ADDED);
			error.put(Constants.DEBUG_MESSAGE_KEY,AdminErrorCode.USER_EXISTS_CANNOT_BE_ADDED.value());
			return new ResponseEntity(error,HttpStatus.CONFLICT);
		}
	}
	
	@PostMapping("/login")
	public ResponseEntity login(@RequestBody JSONObject loginRequested){
		AdminLogin user = adminService.findByUsername(loginRequested.get("username").toString());
		if(user!=null) {   
			if(user.getPassword().equals(loginRequested.get("password").toString())) {
				user.setStatus("Active");
				String token = UUID.randomUUID().toString();
				user.setToken(token);
				adminService.updateAdminUser(user);
				HttpHeaders headers = new HttpHeaders();
				headers.setLocation(URI.create("/login/"));
				JSONObject response = new JSONObject();
				response.put("status","Login successful");
				response.put("token",token);
				return new ResponseEntity(response,headers,HttpStatus.OK);
			} else {
				JSONObject error = new JSONObject();
				error.put(Constants.ERROR_CODE_KEY,AdminErrorCode.INCORRECT_PASSWORD);
				error.put(Constants.DEBUG_MESSAGE_KEY,AdminErrorCode.INCORRECT_PASSWORD.value());
				return new ResponseEntity(error,HttpStatus.FORBIDDEN);
			}
		}else {
			JSONObject error = new JSONObject();
			error.put(Constants.ERROR_CODE_KEY, AdminErrorCode.USER_INVALID);
			error.put(Constants.DEBUG_MESSAGE_KEY,AdminErrorCode.USER_INVALID.value());
			return new ResponseEntity(error,HttpStatus.NOT_FOUND);
		}
	}
	
	@GetMapping("/admin")
	public ResponseEntity ListAdmin(@RequestHeader(required=false) String token) throws UnsatisfiedServletRequestParameterException  {
		ResponseEntity exception = verification(token);
		if(exception!=null) {
			return exception;
		}
		return new ResponseEntity(adminService.findAll(),HttpStatus.OK);
	}

	@PostMapping("/forgetpasswd")
	public ResponseEntity forget(@RequestBody JSONObject username) {
		AdminLogin adminLogin = adminService.findByUsername(username.get("username").toString());
		if(adminLogin!=null) {
			try {
				String password = adminLogin.getPassword();
				SimpleMailMessage message = EmailService.sendMail(username.get("username").toString(), "Password Request", "Your password is "+password);
				EmailService.getMailSender().send(message);
				HttpHeaders headers = new HttpHeaders();
				headers.setLocation(URI.create("/forgetpasswd/"));
				return new ResponseEntity<String>("Email sent.",headers,HttpStatus.OK);
			}catch (IllegalArgumentException er) {
				return new ResponseEntity(er,HttpStatus.BAD_GATEWAY);
			}
		}else {
			JSONObject error = new JSONObject();
			error.put(Constants.ERROR_CODE_KEY, AdminErrorCode.USER_INVALID);
			error.put(Constants.DEBUG_MESSAGE_KEY,AdminErrorCode.USER_INVALID.value());
			return new ResponseEntity(error,HttpStatus.NOT_FOUND);
		}
	}
	
	@PostMapping("/updateAdminCreds")
	public ResponseEntity updateAdminCreds(@RequestBody AdminLogin adminCreds, @RequestHeader(required=false) String token) throws UnsatisfiedServletRequestParameterException  {
		ResponseEntity exception = verification(token);
		if(exception!=null) {
			return exception;
		}
		AdminLogin admin = adminService.findByUsername(adminCreds.getUsername());
    	if(admin != null) {
			adminCreds.setId(admin.getId());
			adminCreds.setRegistrationDate(admin.getRegistrationDate());
			adminCreds.setStatus(admin.getStatus());
			adminCreds.setLastLogin(admin.getLastLogin());
			adminCreds.setToken(admin.getToken());
			adminService.updateAdminUser(adminCreds);
			return new ResponseEntity("Admin Details Updated.", HttpStatus.OK);
		}else {
			JSONObject error = new JSONObject();
			error.put(Constants.ERROR_CODE_KEY, AdminErrorCode.INVALID_USER_CHANGE);
			error.put(Constants.DEBUG_MESSAGE_KEY,AdminErrorCode.INVALID_USER_CHANGE.value());
			return new ResponseEntity(error, HttpStatus.NOT_FOUND);
		}
	}
	
	@PostMapping("/logout")
	public ResponseEntity logoutAdmin(@RequestHeader(required=false) String token) throws UnsatisfiedServletRequestParameterException  {
//		ResponseEntity exception = verification(token);
//		if(exception!=null) {
//			return exception;
//		}
		AdminLogin admin = adminService.findByToken(token);
		admin.setStatus("Inactive");
		admin.setToken("");
		adminService.updateAdminUser(admin);
		return new ResponseEntity("Logged out.", HttpStatus.NO_CONTENT);
	}
	
	private ResponseEntity verification(String token) {
		JSONObject error = new JSONObject();
		if(token == null) {
			error.put(Constants.ERROR_CODE_KEY, AdminErrorCode.TOKEN_NOT_FOUND);
			error.put(Constants.DEBUG_MESSAGE_KEY,AdminErrorCode.TOKEN_NOT_FOUND.value());
			return new ResponseEntity(error,HttpStatus.FORBIDDEN);
		}
		if(authenticationService.authenticate(token) == false){
			error.put(Constants.ERROR_CODE_KEY, AdminErrorCode.INVALID_TOKEN);
			error.put(Constants.DEBUG_MESSAGE_KEY,AdminErrorCode.INVALID_TOKEN.value());
			return new ResponseEntity(error,HttpStatus.UNAUTHORIZED);
		}
		return null;
	}
}
