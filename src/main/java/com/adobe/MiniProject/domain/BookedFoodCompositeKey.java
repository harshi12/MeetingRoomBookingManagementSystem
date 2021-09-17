package com.adobe.MiniProject.domain;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Embeddable;
import javax.persistence.IdClass;

@Embeddable
@IdClass(BookedFoodCompositeKey.class)
public class BookedFoodCompositeKey implements Serializable {
    private int bookingID;
    private int foodID;

    public BookedFoodCompositeKey() {
    }

    public BookedFoodCompositeKey(int bookingID, int foodID) {
        this.bookingID = bookingID;
        this.foodID = foodID;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookedFoodCompositeKey compositeObj = (BookedFoodCompositeKey) o;
        if(this.bookingID != compositeObj.bookingID) return false;
        return this.foodID == compositeObj.foodID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookingID, foodID);
    }
}
