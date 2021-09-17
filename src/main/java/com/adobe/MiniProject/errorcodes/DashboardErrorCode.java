package com.adobe.MiniProject.errorcodes;

public enum DashboardErrorCode {
	PDF_NOT_CREATED ("No bookings found for this date. Please try entering another date."),
	PDF_DOES_NOT_EXIST ("No such pdf file with the given name exist. Please search with another name.");
	private final String message;
	DashboardErrorCode(String string) {
		this.message = string;
	}
	
	public String value() {
		return this.message;
	}
}
