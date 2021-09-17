package com.adobe.MiniProject.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.adobe.MiniProject.errorcodes.Constants;
import com.adobe.MiniProject.errorcodes.DashboardErrorCode;
import com.adobe.MiniProject.errorcodes.UploadErrorCode;

@RestController
@CrossOrigin
public class UploadController {
	
	@PostMapping("/upload")
	public ResponseEntity uploadFile(@RequestParam("file") MultipartFile file) {
		if(file.isEmpty()) {
			JSONObject error = new JSONObject();
			error.put(Constants.ERROR_CODE_KEY, UploadErrorCode.INVALID_UPLOAD);
    		error.put(Constants.DEBUG_MESSAGE_KEY, UploadErrorCode.INVALID_UPLOAD.value());
    		return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
		}
		
		try {
			byte[] bytes = file.getBytes();
            Path path = Paths.get(file.getOriginalFilename());
            Files.write(path, bytes);
            return new ResponseEntity<>("File Uploaded at " + file.getOriginalFilename(),HttpStatus.OK);
		} catch (IOException e) {
			return new ResponseEntity<>(e,HttpStatus.BAD_REQUEST);
        }
	}
}
