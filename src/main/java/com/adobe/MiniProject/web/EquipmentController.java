package com.adobe.MiniProject.web;

import java.net.URI;
import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.adobe.MiniProject.domain.Equipment;
import com.adobe.MiniProject.errorcodes.AdminErrorCode;
import com.adobe.MiniProject.errorcodes.Constants;
import com.adobe.MiniProject.errorcodes.EquipmentError;
import com.adobe.MiniProject.service.AuthenticationService;
import com.adobe.MiniProject.service.EquipmentService;

@RestController
@CrossOrigin
public class EquipmentController {
	
	EquipmentService equipmentService;
	
	@Autowired
	public void setEquipmentService(EquipmentService service) {
		this.equipmentService = service;
	}
	
	AuthenticationService authenticationService;
	
	@Autowired
	public void setService(AuthenticationService service) {
		this.authenticationService = service;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/equipments")
	public ResponseEntity getAllEquipment(@RequestHeader(required = false) String token)
			throws UnsatisfiedServletRequestParameterException {
		return new ResponseEntity(equipmentService.findAll(), HttpStatus.OK);
	}
	
	// GET /equipments/id - 200 + json body OR 404
	@SuppressWarnings("unchecked")
	@GetMapping("/equipments/{id}")
	public ResponseEntity getEquipmentById(@PathVariable("id")int equipmentId,
			@RequestHeader(required = false) String token) throws UnsatisfiedServletRequestParameterException {
		Equipment equipment = equipmentService.findById(equipmentId);
		if(equipment != null) {
			return new ResponseEntity<>(equipment, HttpStatus.OK);
		}else {
			 JSONObject jsonError = new JSONObject(); 
			 jsonError.put(Constants.ERROR_CODE_KEY,EquipmentError.EQUIPMENT_NOT_FOUND.name());
			 jsonError.put(Constants.DEBUG_MESSAGE_KEY,EquipmentError.EQUIPMENT_NOT_FOUND.value());
			 return new ResponseEntity(jsonError, HttpStatus.NOT_FOUND);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@PostMapping("/equipments")
	public ResponseEntity addNewEquipment(@RequestBody Equipment equipment,
			@RequestHeader(required = false) String token) throws UnsatisfiedServletRequestParameterException {
		ResponseEntity exception = verification(token);
		if (exception != null) {
			return exception;
		}
		try {
			if (equipment.getTitle() == "") {
				 JSONObject jsonError = new JSONObject(); 
				 jsonError.put(Constants.ERROR_CODE_KEY,EquipmentError.EMPTY_TITLE.name());
				 jsonError.put(Constants.DEBUG_MESSAGE_KEY,EquipmentError.EMPTY_TITLE.value());
				 return new ResponseEntity(jsonError, HttpStatus.BAD_REQUEST);
			}
			if (equipment.getPrice() <= 0) {
				 JSONObject jsonError = new JSONObject(); 
				 jsonError.put(Constants.ERROR_CODE_KEY,EquipmentError.INCORRECT_PRICE_VALUE.name());
				 jsonError.put(Constants.DEBUG_MESSAGE_KEY,EquipmentError.INCORRECT_PRICE_VALUE.value());
				 return new ResponseEntity(jsonError, HttpStatus.NOT_FOUND);
			}
			Equipment equipmentFromDatabase = equipmentService.getByTitle(equipment.getTitle());
			if (equipmentFromDatabase == null) {
				int id = equipmentService.addNewEquipment(equipment);
				HttpHeaders headers = new HttpHeaders();
				headers.setLocation(URI.create("/equipments/" + id));
				return new ResponseEntity<Equipment>(equipmentService.findById(id), headers,HttpStatus.CREATED);
			} else {
				 JSONObject jsonError = new JSONObject(); 
				 jsonError.put(Constants.ERROR_CODE_KEY,EquipmentError.TITLE_NOT_AVAILABLE.name());
				 jsonError.put(Constants.DEBUG_MESSAGE_KEY,EquipmentError.TITLE_NOT_AVAILABLE.value());
				 return new ResponseEntity(jsonError, HttpStatus.BAD_REQUEST);
			  }
		} catch(IllegalArgumentException iae) {
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
	}
	
	@DeleteMapping("/equipments/{id}")
	public ResponseEntity removeEquipment(@PathVariable("id")int id ,
			@RequestHeader(required = false) String token) throws UnsatisfiedServletRequestParameterException {
		ResponseEntity exception = verification(token);
		if (exception != null) {
			return exception;
		}
		Equipment equipment = equipmentService.findById(id);
		if (equipment != null) {
			try {
				equipmentService.removeEquipment(id);
				return new ResponseEntity<String> ("Equipment Deleted Successfully", HttpStatus.NO_CONTENT);
			} catch(IllegalArgumentException ex) {
				return new ResponseEntity(ex.getMessage(), HttpStatus.BAD_REQUEST);
			}
		}else {
			 JSONObject jsonError = new JSONObject(); 
			 jsonError.put(Constants.ERROR_CODE_KEY,EquipmentError.EQUIPMENT_NOT_FOUND.name());
			 jsonError.put(Constants.DEBUG_MESSAGE_KEY,EquipmentError.EQUIPMENT_NOT_FOUND.value());
			 return new ResponseEntity(jsonError, HttpStatus.NOT_FOUND);
		}
	}
	
	//- update existing equipment
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PostMapping("/equipments/{id}")
	public ResponseEntity updateExistingEquipment(@RequestBody Equipment equipment,
			@PathVariable("id")int equipmentId, @RequestHeader(required = false) String token)
					throws UnsatisfiedServletRequestParameterException {
		ResponseEntity exception = verification(token);
		if (exception != null) {
			return exception;
		}
		try{
			if (equipment.getTitle() == "") {
				 JSONObject jsonError = new JSONObject(); 
				 jsonError.put(Constants.ERROR_CODE_KEY,EquipmentError.EMPTY_TITLE.name());
				 jsonError.put(Constants.DEBUG_MESSAGE_KEY,EquipmentError.EMPTY_TITLE.value());
				 return new ResponseEntity(jsonError, HttpStatus.BAD_REQUEST);
			}
			if (equipmentService.findById(equipmentId) == null) {
				 JSONObject jsonError = new JSONObject(); 
				 jsonError.put(Constants.ERROR_CODE_KEY,EquipmentError.EQUIPMENT_NOT_FOUND.name());
				 jsonError.put(Constants.DEBUG_MESSAGE_KEY,EquipmentError.EQUIPMENT_NOT_FOUND.value());
				 return new ResponseEntity(jsonError, HttpStatus.NOT_FOUND);
			}
			if (equipment.getPrice() <= 0) {
				 JSONObject jsonError = new JSONObject(); 
				 jsonError.put(Constants.ERROR_CODE_KEY,EquipmentError.INCORRECT_PRICE_VALUE.name());
				 jsonError.put(Constants.DEBUG_MESSAGE_KEY,EquipmentError.INCORRECT_PRICE_VALUE.value());
				 return new ResponseEntity(jsonError, HttpStatus.BAD_REQUEST);
			}
			Equipment equipmentFromDatabase = equipmentService.getByTitle(equipment.getTitle());
			if (equipment.getTitle().equals(
					equipmentService.findById(equipmentId).getTitle()) || equipmentFromDatabase == null) {
				int returnedEquipmentId = equipmentService.updateEquipment(equipmentId, equipment);
				HttpHeaders headers = new HttpHeaders();
				headers.setLocation(URI.create("/equipments/" + returnedEquipmentId));
				return new ResponseEntity<Equipment>(equipmentService.findById(returnedEquipmentId) ,headers,HttpStatus.CREATED);
			} else {
				JSONObject jsonError = new JSONObject();
				jsonError.put(Constants.ERROR_CODE_KEY, EquipmentError.CAN_NOT_UPDATE.name());
				jsonError.put(Constants.DEBUG_MESSAGE_KEY,EquipmentError.CAN_NOT_UPDATE.value());
				return new ResponseEntity(jsonError, HttpStatus.BAD_REQUEST);
			}
		}catch(IllegalArgumentException illegalArgumentException) {
			return new ResponseEntity<Equipment>(HttpStatus.BAD_REQUEST);
		}
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
