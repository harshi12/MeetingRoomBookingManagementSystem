package com.adobe.MiniProject.dal;

import com.adobe.MiniProject.domain.BookedFood;
import java.util.List;

public interface BookedFoodDAO {
    BookedFood save(BookedFood bookedFood);
    List<BookedFood> findAllByBookingID(int bookingID);
    void update(BookedFood bookedFood);
    void deleteByBookingID(int bookingID);
}
