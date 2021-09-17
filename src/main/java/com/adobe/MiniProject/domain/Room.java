package com.adobe.MiniProject.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.*;
import org.springframework.lang.NonNull;

@Entity
public class Room {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int roomId;
	@NonNull private String title;
	private int capacity;
	private int bookingCount;
	private String imageSrc;
	private boolean activeStatus;
	private String description;
	private boolean bookForMultipleDays = true;
	private boolean bookForHalfDay;
	private boolean bookForHour;
	private double pricePerHour;
	private double pricePerHalfDay;
	private double pricePerDay;
	@Transient private List<String> layouts = null;

	@ElementCollection
	private Set<Integer> bookings;

	public Room() {
		this.bookings = new HashSet<>();
	}

	public int getBookingCount() {
		return bookingCount;
	}

	public void setBookingCount(int bookingCount) {
		this.bookingCount = bookingCount;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public String getImageSrc() {
		return imageSrc;
	}

	public void setImageSrc(String image) {
		this.imageSrc = image;
	}

	public boolean getActiveStatus() {
		return activeStatus;
	}

	public void setActiveStatus(boolean status) {
		this.activeStatus = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isBookForMultipleDays() {
		return bookForMultipleDays;
	}

	public void setBookForMultipleDays(boolean bookForMultipleDays) {
		this.bookForMultipleDays = bookForMultipleDays;
	}

	public boolean isBookForHalfDay() {
		return bookForHalfDay;
	}

	public void setBookForHalfDay(boolean bookForHalfDay) {
		this.bookForHalfDay = bookForHalfDay;
	}

	public boolean isBookForHour() {
		return bookForHour;
	}

	public void setBookForHour(boolean bookForHour) {
		this.bookForHour = bookForHour;
	}

	public double getPricePerHour() {
		return pricePerHour;
	}

	public void setPricePerHour(double pricePerHour) {
		this.pricePerHour = pricePerHour;
	}

	public double getPricePerHalfDay() {
		return pricePerHalfDay;
	}

	public void setPricePerHalfDay(double pricePerHalfDay) {
		this.pricePerHalfDay = pricePerHalfDay;
	}

	public double getPricePerDay() {
		return pricePerDay;
	}

	public void setPricePerDay(double pricePerDay) {
		this.pricePerDay = pricePerDay;
	}

	public List<String> getLayouts() {
		return this.layouts;
	}

	public void setLayouts(List<String> layouts) {
		this.layouts = layouts;
	}

	public int getId() {
		return roomId;
	}

	public void setId(int id) {
		this.roomId = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Set<Integer> getBookings() {
		return bookings;
	}

	public void setBookings(Set<Integer> bookings) {
		this.bookings = bookings;
	}

	public boolean isActiveStatus() {
		return activeStatus;
	}
}
