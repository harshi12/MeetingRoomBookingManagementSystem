package com.adobe.MiniProject.domain;

import javax.persistence.*;

import org.springframework.lang.NonNull;

import java.util.List;

@Entity
public class Equipment {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@NonNull private String title;
	private double price;
	private boolean perHour = false;
	private boolean canBookMultiple = false;

	public Equipment() {}

	public Equipment(int id, String title, double price, boolean perHour, boolean canBookMultiple) {
		this.id = id;
		this.title = title;
		this.price = price;
		this.perHour = perHour;
		this.canBookMultiple = canBookMultiple;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public boolean isPerHour() {
		return this.perHour;
	}

	public void setPerHour(boolean perHour) {
		this.perHour = perHour;
	}

	public boolean isCanBookMultiple() {
		return canBookMultiple;
	}

	public void setCanBookMultiple(boolean canBookMultiple) {
		this.canBookMultiple = canBookMultiple;
	}
	
}
