package com.adobe.MiniProject.domain;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.springframework.lang.NonNull;

@Entity
public class RoomLayout {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String imageSrc;
	@NonNull private String title;
	@Transient
	private List<String> rooms = null;

	@Override
	public String toString() {
		return "RoomLayout [id=" + id + ", image=" + imageSrc + ", title=" + title + ", roomTitles=" + rooms + "]";
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public RoomLayout() {}
	
	public RoomLayout(int id, String image, String title, List<String> rooms) {
		super();
		this.id = id;
		this.imageSrc = image;
		this.title = title;
		this.rooms = rooms;
	}

	public String getImageSrc() {
		return imageSrc;
	}

	public void setImageSrc(String imageSrc) {
		this.imageSrc = imageSrc;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getRooms() {
		return this.rooms;
	}

	public void setRooms(List<String> rooms) {
		this.rooms = rooms;
	}
	
}
