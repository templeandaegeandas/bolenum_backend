package com.bolenum.enums;

public enum DocumentType {
	
	NATIONAL_ID("NATIONAL_ID") , RESIDENCE_PROOF("RESIDENCE_PROOF");
	
	private String fileType;
	
	private DocumentType(String fileType)
	{
		this.fileType=fileType;
	}
	
	public String getFileType() {
		return fileType;
	}

}