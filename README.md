# Java-Backend
This is an Java backend API server for Room Booking System
Here is the tentative structure for the Java Backend

## Screen - 1,2,3

Main
Admin
* Dal
	* AdminLoginDAO.java / Impl
		* Save, FindByLoginId (to reset passwd), remove

	* ReservationDAO.java / Impl
		* Save, FindByEntity (name, date....), remove, update, etc.

* Domain
	* AdminLogin.java
		* Entity elements – loginId, passwd
		* Setter, getter methods

	* Reservation.java
		* Entity elements – Room type, Date (when booking is done), Date (for which the booking is done), Duration, Attendees, Layout, Equipment's, Name, Phone, Address, Payment method, Amount, Status
		* Setter, getter methods

* Service
	* AdminLoginService.java
		* Verify login credentials from login form and take appropriate response
		* Add, delete from adminCreds db table
		* Change passwd for given loginId

	* ReservationService.java
		* Add, delete, edit reservation
		* Search by name, date, room type, status, edit (query), etc.

* Web
	* AdminLoginController.java
		* Get request for login form
		* Post request to submit credentials & login
		* Post request to reset passwd

	* DashboardController.java
		* Get request for dashboard page with all statistics (total booking, todays booking, last 3 booking)
		* Get request to enter date and print all booking for that date

	* ReservationController.java
		* Get request for list of all bookings
		* Get request to search for a booking by keyword(not decided yet), current status
		* Get request for booking page when Add booking option is selected
		* Put request for update reservation

Entity of SQL Table:
1. Admin Login
	LoginId
	Passwd

## Screen 4, 5
1. New Booking Page:
	* http://miniproject.com/bookings/ - POST request
		* Request Headers:
			* Body: {JSON containing new booking details}
		* Response Headers:
			* 201
			* Body containing previous JSON string + newly created id
	* http://miniproject.com/bookings/ - GET request
		* Response Headers:
			* 200
			* Body: JSON containing all booking details
	* http://miniproject.com/bookings/id - GET Request
		* Response Headers:
			* 200
			* JSON string containing details for booking ‘id’
	*  http://miniproject.com/bookings/id - POST Request
		* Request Headers:
			* Body: {JSON string containing details for booking 'id'}
		* Response Headers:
			* 201
	* http://miniproject.com/bookings/delete/id - DELETE
		* To delete information related to booking 'id'

2. Meeting Room Page:
	* http://miniproject.com/rooms/ - POST request
		* Request Headers:
			* Body: {JSON containing new meeting-room details}
		* Response Headers:
			* 201
			* Body containing previous JSON string + newly created id
	* http://miniproject.com/rooms/ - GET request
		* Response Headers:
			* 200
			* Body: JSON containing all meeting-room details
	* http://miniproject.com/rooms/id - GET Request
		* Response Headers:
			* 200
			* JSON string containing details for meeting-room ‘id’
	* http://miniproject.com/rooms/id - POST Request
		* Request Headers:
			* Body: {JSON string containing details for meeting-room 'id'}
		* Response Headers:
			* 201
	* http://miniproject.com/rooms/delete/id - DELETE
		* To delete information related to meeting-room 'id'

-------------------------------------------------------------------------------------
Admin
* DAL
	* BookingDAO.java
		* listAll, findById, save, remove
	* MeetingRoomDAO.java
		* listAll, findById, save, remove
* Entity
	* Booking.java
		* Entity elements - date, attendees, layout, etc
		* Getter and setter methods
	* MeetingRoom.java
		* Entity elements - Title, image, layout, etc
		* Getter and setter methods
* Web
	* BookingController.java
		* GET request to list booking, either all or filter by id
		* POST request to add a new booking or updating an existing booking details
		* Delete request to delete an existing booking
	* MeetingRoomController.java
		* GET request to list meeting rooms, either all or filter by id
		* POST request to add a new meeting room or updating an existing meeting room details
		* Delete request to delete an existing meeting rooms
* Service
	* BookingService.java
		* addBooking, deleteBooking, updateBooking
	* MeetingRoomService
		* addRoom, deleteRoom, updateRoom
    
Entity of SQL Table:
1.  Booking Table:
	CreationDate
	IP Address
	Date
	Attendees - Number
	Room - string
	Layout - string
	Duration - string
	Status - string
	PaymentMethod - string
	RoomPrice - number
	EquipmentPrice - number
	FoodDrinkPrice - number
	SubTotal - number
	Tax - number
	Total - number
	Deposit - number
	ClientDetails - primary key of client table
	
2.  BookedEquipments
	Booking ID - number
	Equipment ID - number
	Quantity - number

3.  BookedSnacks
	Booking ID - number
	FoodDrink ID - number
	Quantity - number

4.  ClientDetails
	Title - string
	Name - string
	Email ID - string
	PhoneNumber - number
	Notes - string
	Company - string
	Address - string
	City - string
	State - string
	Zip - number
	Country - string

## Screen - 6, 7

For Admin
* com.miniproject.dal
	* EquipmentDao.java/EquipmentDaoImpl.java
		* findAll(),updateById(),deleteById(),findById(),save()
	* RoomLayoutDao.java/RoomLayoutDaoImpl.java
		* findAll(),updateById(),deleteById(),findById(),save()
* com.miniproject.entity
	* Equipment.java
		* title, pricePerBooking, pricePerHour, pricePerHalfDay, canBookMultipleUnits and getter, setter methods
	* RoomLayout.java
		* image,title,roomsTitle and getter,setter methods
* com.miniproject.service
	* EquipmentService.java/EquipmentServiceImpl.java
		* addNewEquipment,removeEquipment,updateEquipment,findAll
	* RoomLayoutService.java/RoomLayoutService.java
		* addRoomLayout, findAllRoomLayout, removeRoomLayout, updateRoomLayout
* com.miniproject.web
	* EquipmentController.java
		* GET request to SHOW ALL the equipments
		* POST request to ADD NEW Equipment
		* PUT request to UPDATE existing Equipment
		* DELETE request to REMOVE Equipment
	* RoomLayoutController.java
		* GET request to SHOW ALL the Room Layouts
		* POST request to ADD NEW Room Layout
		* PUT request to UPDATE existing Room Layout
		* DELETE request to REMOVE Room Layout

Database:

1. Room_Layout
	id:int
	image_src:String
	title:String
	
2. Equipment
	title:String
	pricePerHour:double
	pricePerBooking:double
	pricePerHalfDay:double
	canBookMultipleUnits:int (0 or 1)
