package com.bolenum.enums;

public enum DocumentType {
	
	NATIONAL_ID("national_id") , RESIDENCE_PROOF("residence_proof");
	
	private String docType;
	
	private DocumentType(String documentType)
	{
		this.docType=documentType;
	}
	
	/**
	 * This method is use to get document type.
	 * @param Nothing
	 * @return docType
	 */
	public String getDocumentType() {
		return docType;
	}

}