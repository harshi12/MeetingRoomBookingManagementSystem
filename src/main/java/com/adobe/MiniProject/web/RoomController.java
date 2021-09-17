package com.adobe.MiniProject.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import com.adobe.MiniProject.errorcodes.AdminErrorCode;
import com.adobe.MiniProject.errorcodes.UploadErrorCode;
import com.adobe.MiniProject.service.AuthenticationService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import com.adobe.MiniProject.domain.Room;
import com.adobe.MiniProject.errorcodes.Constants;
import com.adobe.MiniProject.errorcodes.RoomError;
import com.adobe.MiniProject.service.RoomService;
import com.adobe.MiniProject.service.RoomServiceImpl;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin
public class RoomController {

	RoomService roomService;
	AuthenticationService authenticationService;
	private String rootDirectory = System.getProperty("user.dir") + "/images";
//	private String DOWNLOAD_IMAGE_HERE = "http://localhost:8081/rooms/download";
	private  String DOWNLOAD_IMAGE_HERE = "https://heroku-roombooking.herokuapp.com/rooms/download";

	@Autowired
	public void setRoomService(RoomServiceImpl service, AuthenticationService authService) {
		this.authenticationService = authService;
		this.roomService = service;
	}

	@Autowired
	public void createDirectoryToStoreImages() {
		rootDirectory += "/rooms/";
		File file = new File(rootDirectory);
		file.mkdirs();
	}

	@RequestMapping(method = RequestMethod.GET, value = "/rooms")
	public ResponseEntity<List<Room>> getRooms(@RequestHeader(required=false) String token) throws UnsatisfiedServletRequestParameterException {
		return new ResponseEntity<List<Room>>(roomService.getAllRooms(), HttpStatus.OK);
	}

	@RequestMapping(value = "/rooms/download/{title}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
	public ResponseEntity<Object> downloadFile(@PathVariable("title") String title, @RequestHeader(required=false) String token) throws UnsatisfiedServletRequestParameterException {
		title = title.replaceAll("_", " ");
		Room room = roomService.getByTitle(title);
		if(room == null){
			JSONObject jsonError = titleNotAvailableError();
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.setContentType(MediaType.APPLICATION_JSON);
			return new ResponseEntity(jsonError, responseHeaders, HttpStatus.NOT_FOUND);
		}
		try {
			title = title.replaceAll(" ", "_");
			String pathToFile = Paths.get(rootDirectory + title).toString();
			File file = new File(pathToFile);
			InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
			ResponseEntity<Object> entity = ResponseEntity.ok(resource);
			return entity;
		}catch (FileNotFoundException e){
			JSONObject jsonError = new JSONObject();
			jsonError.put(Constants.ERROR_CODE_KEY, RoomError.IMAGE_NOT_FOUND.name());
			jsonError.put(Constants.DEBUG_MESSAGE_KEY, RoomError.IMAGE_NOT_FOUND.value());
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.setContentType(MediaType.APPLICATION_JSON);
			return new ResponseEntity(jsonError, responseHeaders, HttpStatus.NOT_FOUND);
		}
	}

	@RequestMapping(method = RequestMethod.GET, value = "/rooms/{id}")
	public ResponseEntity<Room> getRoomByID(@PathVariable("id") int id, @RequestHeader(required=false) String token) throws UnsatisfiedServletRequestParameterException {
		Room room = roomService.getById(id);
		if(room == null){
			JSONObject jsonError = new JSONObject();
			jsonError.put(Constants.ERROR_CODE_KEY, RoomError.ROOM_NOT_FOUND.name());
			jsonError.put(Constants.DEBUG_MESSAGE_KEY, RoomError.ROOM_NOT_FOUND.value());
			return new ResponseEntity(jsonError, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Room>(room, HttpStatus.OK);
	}

	//add new Room
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PostMapping("/rooms")
	public ResponseEntity addNewRoom(@RequestBody Room room, @RequestHeader(required=false) String token) throws UnsatisfiedServletRequestParameterException {
		ResponseEntity exception = verification(token);
		if(exception!=null) {
			return exception;
		}
		try {
			Room roomDb = roomService.getByTitle(room.getTitle());
			if (roomDb == null) {
				Room newRoom = roomService.addNewRoom(room);
				return new ResponseEntity<Room>(newRoom,HttpStatus.CREATED);
			} else {
				JSONObject jsonError = titleNotAvailableError();
				return new ResponseEntity(jsonError, HttpStatus.BAD_REQUEST);
			}
		}catch(IllegalArgumentException iae) {
			return new ResponseEntity<Room>(HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/rooms/image")
	public ResponseEntity uploadFile(@RequestParam("image") MultipartFile image, @RequestHeader(required=false) String token) throws UnsatisfiedServletRequestParameterException {
		ResponseEntity exception = verification(token);
		if(exception!=null) {
			return exception;
		}
		if(image.isEmpty()) {
			JSONObject error = new JSONObject();
			error.put(Constants.ERROR_CODE_KEY, UploadErrorCode.INVALID_UPLOAD);
			error.put(Constants.DEBUG_MESSAGE_KEY, UploadErrorCode.INVALID_UPLOAD.value());
			return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
		}
		try {
			byte[] bytes = image.getBytes();
			String filename = image.getOriginalFilename().replaceFirst("[.][^.]+$", "");
			filename = filename.replaceAll(" ", "_");
			String imageSrc = rootDirectory + filename;
			Path path = Paths.get(imageSrc);
			Files.write(path, bytes);
			return new ResponseEntity<>(DOWNLOAD_IMAGE_HERE + "/" + filename , HttpStatus.OK);
		} catch (IOException e) {
			return new ResponseEntity<>(e,HttpStatus.BAD_REQUEST);
		}
	}

	//update room
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PostMapping("/rooms/{id}")
	public ResponseEntity updateExistingRoom(@RequestBody Room room, @PathVariable("id")int roomId, @RequestHeader(required=false) String token) throws UnsatisfiedServletRequestParameterException, IOException {
		ResponseEntity exception = verification(token);
		if(exception!=null) {
			return exception;
		}
		try {
			Room roomFromDatabase = roomService.getByTitle(room.getTitle());
			if (room.getTitle().equals(
					roomService.getById(roomId).getTitle()) || roomFromDatabase == null) {
				String titleInImageSrc = room.getImageSrc().substring(room.getImageSrc().lastIndexOf("/") + 1).replaceAll("_", " ");
				if (roomFromDatabase == null) {
					if (titleInImageSrc.equals(room.getTitle())) {
						String roomTitle = roomService.getById(roomId).getTitle().replaceAll(" ", "_");
						Files.deleteIfExists(Paths.get(rootDirectory  + roomTitle));
					} else {
						File oldName = new File(rootDirectory  + roomService.getById(roomId).getTitle().replaceAll(" ", "_"));
						File newName = new File(rootDirectory  + room.getTitle().replaceAll(" ", "_"));
						oldName.renameTo(newName);
						room.setImageSrc(DOWNLOAD_IMAGE_HERE + "/" + room.getTitle().replaceAll(" ", "_"));
					}
				}
				Room newRoom = roomService.updateRoom(roomId, room);
				return new ResponseEntity<Room>(newRoom,HttpStatus.CREATED);
			} else {
				JSONObject jsonError = new JSONObject();
				jsonError.put(Constants.ERROR_CODE_KEY, RoomError.CAN_NOT_UPDATE.name());
				jsonError.put(Constants.DEBUG_MESSAGE_KEY, RoomError.CAN_NOT_UPDATE.value());
				return new ResponseEntity(jsonError, HttpStatus.BAD_REQUEST);
			}
		}catch(IllegalArgumentException illegalArgumentException) {
			return new ResponseEntity<Room>(HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/rooms/{id}/status")
	public ResponseEntity changeRoomStatus(@PathVariable("id") int roomId, @RequestBody JSONObject statusChange, @RequestHeader(required=false) String token) throws UnsatisfiedServletRequestParameterException {
		ResponseEntity exception = verification(token);
		if(exception!=null) {
			return exception;
		}
		Room room = roomService.getById(roomId);
		if(room == null){
			JSONObject jsonError = new JSONObject();
			jsonError.put(Constants.ERROR_CODE_KEY, RoomError.ROOM_NOT_FOUND.name());
			jsonError.put(Constants.DEBUG_MESSAGE_KEY, RoomError.ROOM_NOT_FOUND.value());
			return new ResponseEntity(jsonError, HttpStatus.NOT_FOUND);
		}
		roomService.changeRoomStatus(room, statusChange);
		return new ResponseEntity(statusChange, HttpStatus.OK);
	}
	
	//delete room
	@DeleteMapping("/rooms/{id}")
	public ResponseEntity removeRoom(@PathVariable("id")int roomId, @RequestHeader(required=false) String token) throws UnsatisfiedServletRequestParameterException, IOException {
		ResponseEntity exception = verification(token);
		if(exception!=null) {
			return exception;
		}
		Room room = roomService.getById(roomId);
		if(room == null){
			JSONObject jsonError = new JSONObject();
			jsonError.put(Constants.ERROR_CODE_KEY, RoomError.ROOM_NOT_FOUND.name());
			jsonError.put(Constants.DEBUG_MESSAGE_KEY, RoomError.ROOM_NOT_FOUND.value());
			return new ResponseEntity(jsonError, HttpStatus.NOT_FOUND);
		}
		String roomTitle = room.getTitle().replaceAll(" ", "_");
		Files.deleteIfExists(Paths.get(rootDirectory + roomTitle));
		roomService.removeRoom(roomId);
		return new ResponseEntity<String> ("Room Deleted", HttpStatus.NO_CONTENT);
	}

	@PostMapping("/rooms/isAvailable")
	public ResponseEntity isRoomAvailable(@RequestBody JSONObject roomBookingDetails, @RequestHeader(required=false) String token) throws UnsatisfiedServletRequestParameterException {
		if(roomBookingDetails.get("roomType") == null || roomService.getByTitle(roomBookingDetails.get("roomType").toString()) == null){
			JSONObject jsonError = new JSONObject();
			jsonError.put(Constants.ERROR_CODE_KEY, RoomError.ROOM_NOT_FOUND.name());
			jsonError.put(Constants.DEBUG_MESSAGE_KEY, RoomError.ROOM_NOT_FOUND.value());
			return new ResponseEntity(titleNotAvailableError(), HttpStatus.NOT_FOUND);
		}
		Room room = roomService.getByTitle(roomBookingDetails.get("roomType").toString());
		JSONObject roomAvailability = roomService.isRoomAvailable(room, roomBookingDetails);
		return new ResponseEntity(roomAvailability, HttpStatus.OK);
	}

	public JSONObject titleNotAvailableError(){
		JSONObject jsonError = new JSONObject();
		jsonError.put(Constants.ERROR_CODE_KEY, RoomError.TITLE_NOT_AVAILABLE.name());
		jsonError.put(Constants.DEBUG_MESSAGE_KEY, RoomError.TITLE_NOT_AVAILABLE.value());
		return jsonError;
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
