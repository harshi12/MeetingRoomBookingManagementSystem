package com.adobe.MiniProject.service;

import com.adobe.MiniProject.domain.Client;
import org.json.simple.JSONObject;

public interface ClientService {
    void addNewClient(Client client, int bookingID);
    void updateClient(Client client);
    Client findByBookingID(int bookingID);
    void deleteByBookingID(int bookingID);
    JSONObject checkErrorForNewBooking(Client client);
    JSONObject checkErrorForUpdateBooking(Client client);
}
