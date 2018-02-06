/*
 * @(#)Post.java $version 2014. 8. 8.
 *
 * Copyright 2007 NHN Corp. All rights Reserved. 
 * NHN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.nhncorp.sns.model;

import java.util.List;

/**
 * @author taeshik.heo
 */
public class Post {
	public static String FIELD_SEPERATOR = "~";
	
	private String type;
	private String writeTime;
	private String writer;
	private List<String> receiver;
	private String content;
	private Path path;
	
	public Post(){
		path = new TextPath();
	}

	public static String getFIELD_SEPERATOR() {
		return FIELD_SEPERATOR;
	}

	public static void setFIELD_SEPERATOR(String fIELD_SEPERATOR) {
		FIELD_SEPERATOR = fIELD_SEPERATOR;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getWriteTime() {
		return writeTime;
	}

	public void setWriteTime(String writeTime) {
		this.writeTime = writeTime;
	}

	public String getWriter() {
		return writer;
	}

	public void setWriter(String writer) {
		this.writer = writer;
	}

	public List<String> getReceiver() {
		return receiver;
	}

	public void setReceiver(List<String> receiver) {
		this.receiver = receiver;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPath() {
		return path.getPath();
	}

	public void setPathContent(String pathContent) {
		path.setPath(pathContent);
	}
	
	public void setPathType(Path path) {
		this.path = path;
	}
	

	@Override
	public String toString() {

		String postContents = type + FIELD_SEPERATOR + writeTime + FIELD_SEPERATOR + writer + FIELD_SEPERATOR +receiver.toString() + FIELD_SEPERATOR + content;
		if(type.equals("text")) {
			return postContents;
		}else {			
			postContents += FIELD_SEPERATOR + path.getPath();			
		}
		return postContents;
	}
}