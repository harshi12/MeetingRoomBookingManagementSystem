package com.adobe.MiniProject.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sun.istack.Nullable;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

@Entity
public class Booking {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int bookingID;
	@JsonFormat(pattern="yyyy-MM-dd")
	private Date createdOn;
	@JsonFormat(pattern="yyyy-MM-dd")
	private Date endDate;
	@Nullable private  String bookingTime;
	private String ipAddress;
	@JsonFormat(pattern="yyyy-MM-dd")
	private Date bookingDate;
	private int attendees;
	private String layout;
	private String roomType;
	@Nullable private  String duration;
	private String status;
	private String paymentMethod;
	private double roomPrice;
	private double equipmentPrice;
	private double foodPrice;
	private double subTotal;
	private double tax;
	private double total;
	private double deposit;

	@OneToMany(mappedBy = "bookingID")
	private List<BookedEquipment> equipments;

	@OneToMany(mappedBy = "bookingID")
	private List<BookedFood> food;
  
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "bookingID")
	private Client client;

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getBookingTime() {
		return bookingTime;
	}

	public void setBookingTime(String bookingTime) {
		this.bookingTime = bookingTime;
	}

	public List<BookedFood> getFood() {
		return food;
	}

	public void setFood(List<BookedFood> food) {
		this.food = food;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public List<BookedEquipment> getEquipments() {
		return equipments;
	}

	public void setEquipments(List<BookedEquipment> equipments) {
		this.equipments = equipments;
	}

	public Date getBookingDate() {
		return bookingDate;
	}

	public void setBookingDate(Date bookingDate) {
		this.bookingDate = bookingDate;
	}

	public int getBookingID() {
		return bookingID;
	}

	public void setBookingID(int id) {
		this.bookingID = id;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public int getAttendees() {
		return attendees;
	}

	public void setAttendees(int attendees) {
		this.attendees = attendees;
	}

	public String getLayout() {
		return layout;
	}

	public void setLayout(String layout) {
		this.layout = layout;
	}

	public String getRoomType() {
		return roomType;
	}

	public void setRoomType(String roomType) {
		this.roomType = roomType;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public double getRoomPrice() {
		return roomPrice;
	}

	public void setRoomPrice(double roomPrice) {
		this.roomPrice = roomPrice;
	}

	public double getEquipmentPrice() {
		return equipmentPrice;
	}

	public void setEquipmentPrice(double equipmentPrice) {
		this.equipmentPrice = equipmentPrice;
	}

	public double getFoodPrice() {
		return foodPrice;
	}

	public void setFoodPrice(double foodPrice) {
		this.foodPrice = foodPrice;
	}

	public double getSubTotal() {
		return subTotal;
	}

	public void setSubTotal(double subTotal) {
		this.subTotal = subTotal;
	}

	public double getTax() {
		return tax;
	}

	public void setTax(double tax) {
		this.tax = tax;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public double getDeposit() {
		return deposit;
	}

	public void setDeposit(double deposit) {
		this.deposit = deposit;
	}
}
