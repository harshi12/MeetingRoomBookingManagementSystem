package com.adobe.MiniProject.errorcodes;

public enum UploadErrorCode {
	INVALID_UPLOAD ("No file uploaded. Please upload a valid file.");
	
	private final String message;
	UploadErrorCode(String string) {
		this.message = string;
	}
	
	public String value() {
		return this.message;
	}
}
