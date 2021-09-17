package com.adobe.MiniProject.dal;

import com.adobe.MiniProject.domain.Client;

public interface ClientDAO {
    void save(Client client);
    Client findByBookingId(int bookingID);
    void updateByID(Client Client);
    void deleteByBookingID(int bookingID);
}
