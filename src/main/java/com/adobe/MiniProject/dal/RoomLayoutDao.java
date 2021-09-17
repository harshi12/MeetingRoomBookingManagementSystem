package com.adobe.MiniProject.dal;

import java.util.List;
import com.adobe.MiniProject.domain.RoomLayout;

public interface RoomLayoutDao {
	RoomLayout save(RoomLayout roomLayout);
	List<RoomLayout> findAll();
	void deleteById(int roomLayoutId);
	int updateById(int roomLayoutId, RoomLayout newRoomLayout);
	RoomLayout findById(int roomLayoutId);
	RoomLayout findByTitle(String title);
	int getIdFromTitle(String title);
	String getTitleFromId(int roomLayoutId);
}
