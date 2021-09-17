package com.adobe.MiniProject.web;

import java.util.List;
import com.adobe.MiniProject.errorcodes.AdminErrorCode;
import com.adobe.MiniProject.service.AuthenticationService;
import com.adobe.MiniProject.service.EmailService;
import com.adobe.MiniProject.errorcodes.BookingError;
import com.adobe.MiniProject.errorcodes.Constants;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import com.adobe.MiniProject.domain.Booking;
import com.adobe.MiniProject.service.BookingService;

@RestController
@CrossOrigin
public class BookingController {
	BookingService service;
	AuthenticationService authenticationService;
	@Autowired
	public void setService(BookingService service, AuthenticationService authService) {
		this.authenticationService = authService;
		this.service = service;
	}

	@PostMapping("/bookings")
	public ResponseEntity<Booking> addNewBooking(@RequestBody Booking newBooking, @RequestHeader(required=false) String token) throws UnsatisfiedServletRequestParameterException {
		JSONObject error = service.checkErrorForNewBooking(newBooking);
		if(!error.isEmpty())
			return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
		Booking booking = service.addNewBooking(newBooking);
		String username = newBooking.getClient().getEmail();
		SimpleMailMessage message = EmailService.sendMail(username, "New Booking", emailBody(newBooking));
		EmailService.getMailSender().send(message);
		return new ResponseEntity<Booking>(booking, HttpStatus.CREATED);
	}

	@RequestMapping(method=RequestMethod.GET, value="/bookings")
	public ResponseEntity<List<Booking>> getAllBookings(@RequestHeader(required=false) String token) throws UnsatisfiedServletRequestParameterException {
		ResponseEntity exception = verification(token);
		if(exception!=null) {
			return exception;
		}
		return new ResponseEntity<List<Booking>>(service.findAll(), HttpStatus.OK);
	}
	
	@DeleteMapping("bookings/{id}")
	public ResponseEntity<Booking> removeBookingById(@PathVariable("id") int id, @RequestHeader(required=false) String token) throws UnsatisfiedServletRequestParameterException {
		ResponseEntity exception = verification(token);
		if(exception!=null) {
			return exception;
		}
		Booking booking = service.findById(id);
		if(booking == null){
			JSONObject error = new JSONObject();
			error.put(Constants.ERROR_CODE_KEY, BookingError.INVALID_BOOKING.name());
			error.put(Constants.DEBUG_MESSAGE_KEY, BookingError.INVALID_BOOKING.value());
			return new ResponseEntity(error, HttpStatus.NOT_FOUND);
		}
		service.removeBooking(id);
		return new ResponseEntity<Booking>(HttpStatus.OK);
	}

	@PutMapping("/updateBooking")
	public ResponseEntity<Booking> updateBooking(@RequestBody Booking booking, @RequestHeader(required=false) String token) throws UnsatisfiedServletRequestParameterException {
		ResponseEntity exception = verification(token);
		if(exception!=null) {
			return exception;
		}
		JSONObject error = service.checkErrorForUpdateBooking(booking);
		if(!error.isEmpty())
			return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
		if(service.findById(booking.getBookingID()) != null){
			Booking newBooking = service.updateBookingById(booking);
			String username = booking.getClient().getEmail();
			SimpleMailMessage message = EmailService.sendMail(username, "Booking Update", emailBody(booking));
			EmailService.getMailSender().send(message);
			return new ResponseEntity<Booking>(newBooking, HttpStatus.CREATED);
		}
		return new ResponseEntity<Booking>(HttpStatus.NOT_FOUND);
	}

	@GetMapping("/bookings/{id}")
	public ResponseEntity<Booking> getBookingById(@PathVariable("id") int id, @RequestHeader(required=false) String token) throws UnsatisfiedServletRequestParameterException {
		ResponseEntity exception = verification(token);
		if(exception!=null) {
			return exception;
		}
		Booking booking = service.findById(id);
		if(booking == null){
			return new ResponseEntity(invalidBookingError(), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Booking>(booking, HttpStatus.OK);
	}

	@PostMapping("/bookings/{id}/status")
	public ResponseEntity changeBookingStatus(@PathVariable("id") int id, @RequestBody JSONObject statusChange, @RequestHeader(required=false) String token) throws UnsatisfiedServletRequestParameterException {
		ResponseEntity exception = verification(token);
		if(exception!=null) {
			return exception;
		}
		Booking booking = service.findById(id);
		if(booking == null){
			return  new ResponseEntity(invalidBookingError(), HttpStatus.NOT_FOUND);
		}
		service.changeBookingStatus(booking, statusChange);
		return new ResponseEntity(statusChange, HttpStatus.CREATED);
	}

	@GetMapping("/sendEmail/{id}")
	public ResponseEntity sendEmail(@PathVariable("id") int id, @RequestHeader(required=false) String token) throws UnsatisfiedServletRequestParameterException {
		ResponseEntity exception = verification(token);
		if(exception!=null) {
			return exception;
		}
		Booking booking = service.findById(id);
		if(booking == null){
			return new ResponseEntity(invalidBookingError(), HttpStatus.NOT_FOUND);
		}
		String username = booking.getClient().getEmail();
		SimpleMailMessage message = EmailService.sendMail(username, "Booking Details", emailBody(booking));
		EmailService.getMailSender().send(message);
		return new ResponseEntity(HttpStatus.OK);
	}

	public JSONObject invalidBookingError(){
		JSONObject error = new JSONObject();
		error.put(Constants.ERROR_CODE_KEY, BookingError.INVALID_BOOKING.name());
		error.put(Constants.DEBUG_MESSAGE_KEY, BookingError.INVALID_BOOKING.value());
		return error;
	}

	public String emailBody(Booking booking){
		String body = "Your booking for " + booking.getRoomType() + " with " + booking.getAttendees() +" seating is confirmed";
		if(booking.getDuration() == ""){
			body += " from " + booking.getBookingTime();
		}else {
			body += " for " + booking.getDuration();
 		}
		body += " on " + booking.getBookingDate();
		body += "\nYour booking status is " + booking.getStatus();
		return body;
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
