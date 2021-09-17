package com.adobe.MiniProject.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.adobe.MiniProject.domain.RoomLayout;
import com.adobe.MiniProject.errorcodes.AdminErrorCode;
import com.adobe.MiniProject.errorcodes.Constants;
import com.adobe.MiniProject.errorcodes.RoomLayoutError;
import com.adobe.MiniProject.errorcodes.UploadErrorCode;
import com.adobe.MiniProject.service.AuthenticationService;
import com.adobe.MiniProject.service.RoomLayoutService;

@RestController
@CrossOrigin
public class RoomLayoutController {
	
	RoomLayoutService roomLayoutService;
	private String rootDirectory = System.getProperty("user.dir").toString() + "/images/roomlayouts";
//	private String DOWNLOAD_IMAGE_HERE = "http://localhost:8081/roomlayouts/download";
	private  String DOWNLOAD_IMAGE_HERE = "https://heroku-roombooking.herokuapp.com/roomlayouts/download";
	@Autowired
	public void createDirectoryToStoreImages() {
		File file = new File(rootDirectory);
		boolean done = file.mkdirs();
		if (done) {
			System.out.println(Paths.get(rootDirectory) + " Created");
		} else {
			System.out.println("directory already exists or could not create directory");
		}
	}
	
	@Autowired
	public void setRoomLayoutService(RoomLayoutService service) {
		this.roomLayoutService = service;
	}
	
	AuthenticationService authenticationService;
	
	@Autowired
	public void setService(AuthenticationService service) {
		this.authenticationService = service;
	}
	
	//To get all the available RoomLayouts 
	@RequestMapping(method = RequestMethod.GET, value = "/roomlayouts")
	public ResponseEntity getRoomLayouts(@RequestHeader(required = false) String token) throws UnsatisfiedServletRequestParameterException{
		return new ResponseEntity(roomLayoutService.getRoomLayouts() ,HttpStatus.OK);
	}

	// GET /roomlayouts/id - 200 + json body OR 404
	@SuppressWarnings("unchecked")
	@GetMapping("/roomlayouts/{id}")
	public ResponseEntity getRoomLayoutById(@PathVariable("id")int roomLayoutId, 
			@RequestHeader(required = false) String token) throws UnsatisfiedServletRequestParameterException {
		RoomLayout roomLayout = roomLayoutService.getRoomLayoutById(roomLayoutId);
		if (roomLayout != null) {
			return new ResponseEntity<>(roomLayout,HttpStatus.OK);
		} else {
			return roomLayoutNotFoundError();
		}
	}
	
	@RequestMapping(value = "/roomlayouts/download/{title}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
	public ResponseEntity downloadFile(@PathVariable("title") String title
			, @RequestHeader(required = false) String token) throws UnsatisfiedServletRequestParameterException, FileNotFoundException{
		RoomLayout layout = roomLayoutService.getByTitle(title.replaceAll("_", " ")); 
		System.out.println(title);
		if (layout == null) {
			JSONObject jsonError = new JSONObject();
			jsonError.put(Constants.ERROR_CODE_KEY, RoomLayoutError.IMAGE_NOT_FOUND.name());
			jsonError.put(Constants.DEBUG_MESSAGE_KEY, RoomLayoutError.IMAGE_NOT_FOUND.value());
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.setContentType(MediaType.APPLICATION_JSON);
			System.out.println(jsonError.toString());
			return new ResponseEntity(jsonError, responseHeaders, HttpStatus.NOT_FOUND);
		}
		String pathToFile = Paths.get(rootDirectory + "/" + title).toString();
		System.out.println(pathToFile + "<--------->");
		File file = new File(pathToFile);
		InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
		ResponseEntity<Object> entity = ResponseEntity.ok(resource);
		return entity;
	}

	@PostMapping("/roomlayouts/image")
	public ResponseEntity uploadFile(@RequestParam("image") MultipartFile image,
			@RequestHeader(required = false) String token) throws UnsatisfiedServletRequestParameterException {
		ResponseEntity exception = verification(token);
		if (exception != null) {
			return exception;
		}
		if(image.isEmpty()) {
			JSONObject error = new JSONObject();
			error.put(Constants.ERROR_CODE_KEY, UploadErrorCode.INVALID_UPLOAD.name());
    		error.put(Constants.DEBUG_MESSAGE_KEY, UploadErrorCode.INVALID_UPLOAD.value());
    		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
		}
		try {
			byte[] bytes = image.getBytes();
			System.out.println(image.getOriginalFilename());
			String pathToFile = rootDirectory + "/" + image.getOriginalFilename().replaceFirst("[.][^.]+$", "").replaceAll(" ", "_");
			System.out.println(pathToFile);
            Path path = Paths.get(pathToFile);
            System.out.println(path);
            Files.write(path, bytes);
            return new ResponseEntity<>(DOWNLOAD_IMAGE_HERE + "/" + 
            			image.getOriginalFilename().replaceFirst("[.][^.]+$", "").replaceAll(" ", "_") , HttpStatus.OK);
		} catch (IOException e) {
			return new ResponseEntity<>(e,HttpStatus.BAD_REQUEST);
        }
	}
	
	//-add new Room Layout
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PostMapping("/roomlayouts")
	public ResponseEntity addNewRoomLayout(@RequestBody RoomLayout roomLayout, 
			@RequestHeader(required = false) String token) throws UnsatisfiedServletRequestParameterException {
		ResponseEntity exception = verification(token);
		if (exception != null) {
			return exception;
		}
		try {
			RoomLayout roomLayoutD = roomLayoutService.getByTitle(roomLayout.getTitle());
			if (roomLayout.getTitle() == "") {
				JSONObject errorJson = new JSONObject();
				errorJson.put(Constants.ERROR_CODE_KEY, RoomLayoutError.EMPTY_TITLE.name());
				errorJson.put(Constants.DEBUG_MESSAGE_KEY, RoomLayoutError.EMPTY_TITLE.value());
				return new ResponseEntity(errorJson, HttpStatus.BAD_REQUEST);
			}
			if (roomLayoutD == null) {
				int id = roomLayoutService.addRoomLayout(roomLayout);
				HttpHeaders headers = new HttpHeaders();
				headers.setLocation(URI.create("/roomlayouts/" + id));
				return new ResponseEntity<RoomLayout>(roomLayoutService.getRoomLayoutById(id), headers, HttpStatus.CREATED);
			} else {
				JSONObject errorJson = new JSONObject();
				errorJson.put(Constants.ERROR_CODE_KEY, RoomLayoutError.TITLE_NOT_AVAILABLE.name());
				errorJson.put(Constants.DEBUG_MESSAGE_KEY, RoomLayoutError.TITLE_NOT_AVAILABLE.value());
				return new ResponseEntity(errorJson, HttpStatus.BAD_REQUEST);
			}
		}catch(IllegalArgumentException iae) {
			return new ResponseEntity<RoomLayout>(HttpStatus.BAD_REQUEST);
		}
	}
	
	//- update existing RoomLayout
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PostMapping("/roomlayouts/{id}")
	public ResponseEntity updateExistingRoomLayout(@RequestBody RoomLayout layout,
			@PathVariable("id")int layoutId ,@RequestHeader(required = false) String token)
					throws UnsatisfiedServletRequestParameterException, IOException{
		ResponseEntity exception = verification(token);
		if (exception != null) {
			return exception;
		}
		try {
			if (layout.getTitle() == "") {
				JSONObject errorJson = new JSONObject();
				errorJson.put(Constants.ERROR_CODE_KEY, RoomLayoutError.EMPTY_TITLE.name());
				errorJson.put(Constants.DEBUG_MESSAGE_KEY, RoomLayoutError.EMPTY_TITLE.value());
				return new ResponseEntity(errorJson, HttpStatus.BAD_REQUEST);
			}
			if (roomLayoutService.getRoomLayoutById(layoutId) == null) {
				return roomLayoutNotFoundError();
			}
			RoomLayout layoutFromDatabase = roomLayoutService.getByTitle(layout.getTitle());
			if (layout.getTitle().equals(
					roomLayoutService.getRoomLayoutById(layoutId).getTitle()) || layoutFromDatabase == null) {
				if (layoutFromDatabase == null) {
					String titleInImageSrc = layout.getImageSrc().substring(layout.getImageSrc().lastIndexOf("/") + 1).replaceAll("_", " ");
					if (titleInImageSrc.equals(layout.getTitle())) {
						String layoutTitle = roomLayoutService.getRoomLayoutById(layoutId).getTitle().replaceAll(" ", "_");
						Files.deleteIfExists(Paths.get(rootDirectory + "/" + layoutTitle));
					} else {
						File oldName = new File(rootDirectory + "/" + roomLayoutService.getRoomLayoutById(layoutId).getTitle().replaceAll(" ", "_"));
						File newName =  new File(rootDirectory + "/" + layout.getTitle().replaceAll(" ", "_"));
						oldName.renameTo(newName);
						layout.setImageSrc(DOWNLOAD_IMAGE_HERE + "/" + layout.getTitle().replaceAll(" ", "_"));
					}
				}
				int returnedLayoutId = roomLayoutService.updateRoomLayout(layoutId, layout);
				HttpHeaders headers = new HttpHeaders();
				headers.setLocation(URI.create("/roomlayouts/" + returnedLayoutId));
				return new ResponseEntity<RoomLayout>(roomLayoutService.getRoomLayoutById(returnedLayoutId) ,headers, HttpStatus.CREATED);
			} else {
				JSONObject jsonError = new JSONObject();
				jsonError.put(Constants.ERROR_CODE_KEY, RoomLayoutError.CAN_NOT_UPDATE.name());
				jsonError.put(Constants.DEBUG_MESSAGE_KEY, RoomLayoutError.CAN_NOT_UPDATE.value());
				return new ResponseEntity(jsonError, HttpStatus.BAD_REQUEST);
			}
		}catch(IllegalArgumentException illegalArgumentException) {
			return new ResponseEntity<RoomLayout>(HttpStatus.BAD_REQUEST);
		}
	}
	
	//-delete an Existing Room Layout
	@DeleteMapping("/roomlayouts/{id}")
	public ResponseEntity removeRoomLayout(@PathVariable("id")int roomLayoutId,
			@RequestHeader(required = false) String token) throws IOException, UnsatisfiedServletRequestParameterException {
		ResponseEntity exception = verification(token);
		if (exception != null) {
			return exception;
		}
		RoomLayout roomLayout = roomLayoutService.getRoomLayoutById(roomLayoutId);
		if(roomLayout != null) {
			try {
				String layoutTitle = roomLayout.getTitle().replaceAll(" ", "_");
				Files.deleteIfExists(Paths.get(rootDirectory + "/" + layoutTitle));
				roomLayoutService.removeRoomLayout(roomLayoutId);
				return new ResponseEntity<String> ("Room Layout Deleted", HttpStatus.NO_CONTENT);
			}catch(IllegalArgumentException ex){
				return new ResponseEntity(ex.getMessage(), HttpStatus.BAD_REQUEST);
			}
		}else {
			return roomLayoutNotFoundError();
		}
	}
	
	public ResponseEntity roomLayoutNotFoundError() {
	     JSONObject jsonError = new JSONObject(); 
		 jsonError.put(Constants.ERROR_CODE_KEY,RoomLayoutError.ROOMLAYOUT_NOT_FOUND.name());
		 jsonError.put(Constants.DEBUG_MESSAGE_KEY,RoomLayoutError.ROOMLAYOUT_NOT_FOUND.value());
		 return new ResponseEntity(jsonError, HttpStatus.NOT_FOUND);
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
