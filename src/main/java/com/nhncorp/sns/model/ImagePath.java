package com.nhncorp.sns.model;

public class ImagePath implements Path {

	private String imagePath;
	
	@Override
	public String getPath() {
		// TODO Auto-generated method stub
		return imagePath;
	}

	@Override
	public void setPath(String pathContent) {
		// TODO Auto-generated method stub
		imagePath = pathContent;
	}

}
