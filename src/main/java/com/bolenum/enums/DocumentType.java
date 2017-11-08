package com.bolenum.enums;

public enum DocumentType {
	
	NATIONAL_ID("national_id") , RESIDENCE_PROOF("residence_proof");
	
	private String documentType;
	
	private DocumentType(String documentType)
	{
		this.documentType=documentType;
	}
	
	public String getDocumentType() {
		return documentType;
	}

}