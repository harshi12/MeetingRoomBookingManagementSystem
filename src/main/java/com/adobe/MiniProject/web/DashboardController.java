package com.adobe.MiniProject.web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.adobe.MiniProject.domain.AdminLogin;
import com.adobe.MiniProject.domain.Booking;
import com.adobe.MiniProject.errorcodes.AdminErrorCode;
import com.adobe.MiniProject.errorcodes.Constants;
import com.adobe.MiniProject.errorcodes.DashboardErrorCode;
import com.adobe.MiniProject.service.AdminLoginService;
import com.adobe.MiniProject.service.AuthenticationService;
import com.adobe.MiniProject.service.BookingService;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@RestController
@CrossOrigin
public class DashboardController {
	
	BookingService bookService;
	
	@Autowired
    public void setService(BookingService service) {
        this.bookService = service;
    }
	
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
    
    @GetMapping("/dashboard")
	public ResponseEntity getDashboardDetails(@RequestHeader(required=false) String token) throws UnsatisfiedServletRequestParameterException  {
    	ResponseEntity exception = verification(token);
		if(exception!=null) {
			return exception;
		}
    	
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	Date date = null;
		try {
			date = sdf.parse(sdf.format(new Date()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		JSONObject jsonObject = new JSONObject();
		List<Booking> madeToday = bookService.findByCreatedOn(date);
		jsonObject.put("madeToday", madeToday.size());
		
		List<Booking> madeForToday = bookService.findByBookingDate(date);
		jsonObject.put("madeForToday", madeForToday.size());
		List<Booking> total = bookService.findAll();
		jsonObject.put("total", total.size());
		
		jsonObject.put("allBookings", total);
		jsonObject.put("dateToday", date.toString());
		AdminLogin admin = adminService.findByToken(token);
		jsonObject.put("lastLogin", admin.getLastLogin().toString());
		admin.setLastLogin(new Date());
		adminService.updateAdminUser(admin);
		return new ResponseEntity<JSONObject>(jsonObject,HttpStatus.OK);
	}
    
    @PostMapping("/dashboard")
	public ResponseEntity<JSONObject> reservationForDate(@RequestBody Date givenDate, HttpServletRequest request, @RequestHeader(required=false) String token) throws UnsatisfiedServletRequestParameterException  {
    	ResponseEntity exception = verification(token);
		if(exception!=null) {
			return exception;
		}
    	String address = request.getRequestURL().toString();
    	String filePath = address.substring(0, address.length()-10);
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    	Date date = null;
		try {
			date = dateFormat.parse(dateFormat.format(givenDate));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		List<Booking> bookingsForDate = bookService.findByBookingDate(date);
		if(bookingsForDate.size() >= 1) {
			getDetails(bookingsForDate, dateFormat.format(date));
			JSONObject result = new JSONObject();
			result.put("path", filePath + "download/" + dateFormat.format(date) + ".pdf");
			return new ResponseEntity<>(result,HttpStatus.OK);
		}else {
			JSONObject error = new JSONObject();
    		error.put(Constants.ERROR_CODE_KEY, DashboardErrorCode.PDF_NOT_CREATED);
    		error.put(Constants.DEBUG_MESSAGE_KEY, DashboardErrorCode.PDF_NOT_CREATED.value());
			return new ResponseEntity<>(error,HttpStatus.NOT_FOUND);
		}
	}
    
    @GetMapping("/download/{filename}")
	public ResponseEntity getProductById(@PathVariable("filename")String filename) {
		String path = System.getProperty("user.dir");
		File file = new File(path + "/" + filename);
		if(file.exists()) {
			HttpHeaders header = new HttpHeaders();
	        header.add(HttpHeaders.CONTENT_DISPOSITION, String.format("inline; filename=\"" + file.getName() + "\""));
	        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
	        header.add("Pragma", "no-cache");
	        header.add("Expires", "0");
	        Path newPath = Paths.get(file.getAbsolutePath());
	        ByteArrayResource resource = null;
			try {
				resource = new ByteArrayResource(Files.readAllBytes(newPath));
			} catch (IOException e) {
				e.printStackTrace();
			}
	        return ResponseEntity.ok()
	                .headers(header)
	                .contentLength(file.length())
	                .contentType(MediaType.parseMediaType("application/octet-stream"))
	                .body(resource);
		}
		JSONObject error = new JSONObject();
		error.put(Constants.ERROR_CODE_KEY, DashboardErrorCode.PDF_DOES_NOT_EXIST);
		error.put(Constants.DEBUG_MESSAGE_KEY, DashboardErrorCode.PDF_DOES_NOT_EXIST.value());
		return new ResponseEntity<>(error,HttpStatus.NOT_FOUND);
    }
    
    public void getDetails(List<Booking> list, String date){
		Document document = new Document();
		try {
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(date+".pdf"));
			document.open();
			PdfPTable table = new PdfPTable(9);
	        table.setWidthPercentage(100);
	        String[] head = {"Date", "Duration", "Attendees", "Room", "Layout", "Equipments", "Name", "Email", "Phone"};
	        Font font = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
	        for(int i = 0;i < head.length; i++) {
				PdfPCell c1 = new PdfPCell(new Phrase(head[i], font));
		        table.addCell(c1);
	        }
	        table.setHeaderRows(1);

	        for(int i = 0;i < list.size(); i++) {
	        	table.addCell(list.get(i).getBookingDate().toString());
	        	table.addCell(list.get(i).getDuration().toString());
	        	table.addCell(String.valueOf(list.get(i).getAttendees()));
	        	table.addCell(list.get(i).getRoomType().toString());
	        	table.addCell(list.get(i).getLayout().toString());
	        	String equipment = "";
	        	for(int j = 0;j < list.get(i).getEquipments().size();j++) {
	        		equipment += list.get(i).getEquipments().get(j).getTitle() + " x " + list.get(i).getEquipments().get(j).getUnits() + " , ";
	        	}
	        	table.addCell(equipment);
	        	table.addCell(list.get(i).getClient().getName().toString());
	        	table.addCell(list.get(i).getClient().getEmail().toString());
	        	table.addCell(String.valueOf(list.get(i).getClient().getPhone()));
	        }
	        document.add(table);
	        document.close();
	        writer.close();
		}catch(Exception e) {
			e.printStackTrace();
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
