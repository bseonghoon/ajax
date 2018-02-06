package com.nhncorp.sns.model;

public class TextPath implements Path {

	String pathContent;
	public TextPath() {
		pathContent = "";
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public String getPath() {
		return pathContent;
	}

	@Override
	public void setPath(String pathContent) {
		
	}

}
