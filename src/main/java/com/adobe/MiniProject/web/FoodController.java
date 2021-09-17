package com.adobe.MiniProject.web;

import java.util.List;
import com.adobe.MiniProject.domain.Food;
import com.adobe.MiniProject.errorcodes.AdminErrorCode;
import com.adobe.MiniProject.errorcodes.Constants;
import com.adobe.MiniProject.service.AuthenticationService;
import com.adobe.MiniProject.service.FoodService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class FoodController {
    FoodService service;
    AuthenticationService authenticationService;
    @Autowired
    public void setService(FoodService service, AuthenticationService authService) {
        this.authenticationService = authService;
        this.service = service;
    }

    @PostMapping("/food")
    public ResponseEntity<Food> addNewFood(@RequestBody Food newFood, @RequestHeader(required = false) String token){
        ResponseEntity exception = verification(token);
        if (exception != null) {
            return exception;
        }
        Food food = service.addNewFood(newFood);
        return new ResponseEntity<Food>(food, HttpStatus.CREATED);
    }

    @RequestMapping(method=RequestMethod.GET, value="/food")
    public List<Food> getAllFood(){
        return service.findAll();
    }

    @DeleteMapping("food/{id}")
    public ResponseEntity<Food> removeBookingById(@PathVariable("id") int id, @RequestHeader(required = false) String token){
        ResponseEntity exception = verification(token);
        if (exception != null) {
            return exception;
        }
        Food food = service.findById(id);
        if(food == null){
            return new ResponseEntity("Food Item does not exists", HttpStatus.NOT_FOUND);
        }
        service.removeFood(id);
        return new ResponseEntity<Food>(HttpStatus.OK);
    }

    @PostMapping("/updateFood")
    public ResponseEntity<Food> updateFood(@RequestBody Food food, @RequestHeader(required = false) String token){
        ResponseEntity exception = verification(token);
        if (exception != null) {
            return exception;
        }
        if(service.findById(food.getFoodID()) != null){
            service.updateFood(food);
            return new ResponseEntity<Food>(food, HttpStatus.OK);
        }
        return new ResponseEntity("Food Item not found", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/food/{id}")
    public ResponseEntity<Food> getFoodByID(@PathVariable("id") int id){
        Food food = service.findById(id);
        if(food == null){
            return new ResponseEntity("Food item does not exist", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Food>(food, HttpStatus.OK);
    }

    private ResponseEntity verification(String token) {
        JSONObject error = new JSONObject();
        if(token == null) {
            error.put(Constants.ERROR_CODE_KEY, AdminErrorCode.TOKEN_NOT_FOUND);
            error.put(Constants.DEBUG_MESSAGE_KEY,AdminErrorCode.TOKEN_NOT_FOUND.value());
            return new ResponseEntity(error,HttpStatus.FORBIDDEN);
        }
        if(authenticationService.authenticate(token) == false){
            error.put(Constants.ERROR_CODE_KEY, AdminErrorCode.INVALID_TOKEN);
            error.put(Constants.DEBUG_MESSAGE_KEY,AdminErrorCode.INVALID_TOKEN.value());
            return new ResponseEntity(error,HttpStatus.UNAUTHORIZED);
        }
        return null;
    }
}
