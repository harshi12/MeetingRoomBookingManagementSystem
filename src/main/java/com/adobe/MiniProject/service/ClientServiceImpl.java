package com.adobe.MiniProject.service;

import com.adobe.MiniProject.dal.ClientDAO;
import com.adobe.MiniProject.domain.Client;
import com.adobe.MiniProject.errorcodes.BookingError;
import com.adobe.MiniProject.errorcodes.Constants;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientServiceImpl implements ClientService{
    ClientDAO clientDAO;
    @Autowired
    public void setDAO(ClientDAO dao){
        this.clientDAO = dao;
    }

    @Override
    public void addNewClient(Client client, int bookingID) {
        client.setBookingID(bookingID);
        clientDAO.save(client);
    }

    @Override
    public void updateClient(Client client) {
        clientDAO.updateByID(client);
    }

    @Override
    public Client findByBookingID(int bookingID) {
        Client client = clientDAO.findByBookingId(bookingID);
        return client;
    }

    @Override
    public void deleteByBookingID(int bookingID) {
        clientDAO.deleteByBookingID(bookingID);
    }

    @Override
    public JSONObject checkErrorForNewBooking(Client client) {
        JSONObject error = new JSONObject();
        if(client.getBookingID() != 0){
            error.put(Constants.ERROR_CODE_KEY, BookingError.BOOKING_JSON_HAS_BOOKING_ID.name());
            error.put(Constants.DEBUG_MESSAGE_KEY, BookingError.BOOKING_JSON_HAS_BOOKING_ID.value());
            return error;

        }
        if(client.getClientID() != 0){
            error.put(Constants.ERROR_CODE_KEY, BookingError.BOOKING_JSON_HAS_CLIENT_ID.name());
            error.put(Constants.DEBUG_MESSAGE_KEY, BookingError.BOOKING_JSON_HAS_CLIENT_ID.value());
            return error;
        }
        return error;
    }

    @Override
    public JSONObject checkErrorForUpdateBooking(Client client) {
        JSONObject error = new JSONObject();
        if(client.getClientID() == 0){
            error.put(Constants.ERROR_CODE_KEY, BookingError.CLIENT_ID_NOT_FOUND.name());
            error.put(Constants.DEBUG_MESSAGE_KEY, BookingError.CLIENT_ID_NOT_FOUND.value());
            return error;
        }
        return error;
    }
}
