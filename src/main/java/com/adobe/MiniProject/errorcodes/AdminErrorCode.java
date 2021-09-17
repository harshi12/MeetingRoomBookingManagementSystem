package com.adobe.MiniProject.errorcodes;

public enum AdminErrorCode {
	USER_INVALID ("User with this email does not exists. Please enter the correct email."),
	INCORRECT_PASSWORD ("Password entered is incorrect. Try again with the correct password."),
	USER_EXISTS_CANNOT_BE_ADDED ("This user already exists, try adding using different email."),
	INVALID_USER_CHANGE ("Changing the username is not allowed. Other user related fields can be changed."),
	INVALID_TOKEN ("Token received is invalid. Try logging-in again."),
	TOKEN_NOT_FOUND ("Did not received any authorization token. Please provide a valid token.");
	
	private final String message;
	AdminErrorCode(String string) {
		this.message = string;
	}
	
	public String value() {
		return this.message;
	}
}
