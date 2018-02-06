package com.nhncorp.sns.service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

import com.nhncorp.sns.model.ImagePath;
import com.nhncorp.sns.model.Path;
import com.nhncorp.sns.model.Post;
import com.nhncorp.sns.model.TextPath;
import com.nhncorp.sns.model.User;

public class PostService {
	private Scanner sc; 

	public PostService() {
		// TODO Auto-generated constructor stub
		sc = new Scanner(System.in);
	}
	
	public Post wirteTextPost(User user,Post post) {
		Post textPost= new Post();
		Path textPath = new TextPath();
		textPost.setPathType(textPath);
		
		//타입
		textPost.setType("text");
		
		//작성시간
		Date today = new Date();
		SimpleDateFormat date = new SimpleDateFormat("yyyyMMdd");
	    SimpleDateFormat time = new SimpleDateFormat("hhmmss");
	    String writeTime = date.format(today) + time.format(today);
	    textPost.setWriteTime(writeTime);
		
		//작성자
		textPost.setWriter(user.getUserId());
		
		//수신자
		List<String> friendList = user.getFriends();
		textPost.setReceiver(friendList);
		
		//본문
		String content = post.getContent();
		textPost.setContent(content);
		
		return textPost;
	}
	
	public Post wirteImagePost(User user,Post post) {
		Post imagePost= new Post();
		Path imagePath = new ImagePath();
		imagePost.setPathType(imagePath);
		
		//타입
		imagePost.setType("image");
		
		//작성시간
		Date today = new Date();
		SimpleDateFormat date = new SimpleDateFormat("yyyyMMdd");
	    SimpleDateFormat time = new SimpleDateFormat("hhmmss");
	    String writeTime = date.format(today) + time.format(today);
	    imagePost.setWriteTime(writeTime);
		
		//작성자
		imagePost.setWriter(user.getUserId());
		
		//수신자
		List<String> friendList = user.getFriends();
		imagePost.setReceiver(friendList);
		
		//본문
		String content = post.getContent();
		imagePost.setContent(content);
		
		String path = post.getPath();
		imagePost.setPathContent(path);
		return imagePost;
	}
	
	
	//현재 모든 파일... 글 조회 이상하게 되는 중에 끝났어요..
	public List<Post> Sort(Stack<Post> file, Stack<Post> repository) {
		List<Post> list = new ArrayList<>();
		while(((file.size() != 0) && (repository.size() != 0))){
			String fileDate = file.get(file.size()-1).getWriteTime();
			String repoDate = repository.get(repository.size()-1).getWriteTime();
			BigInteger fillDateInt = new BigInteger(fileDate);
			BigInteger repoDateInt = new BigInteger(repoDate);
			if(fillDateInt.compareTo(repoDateInt) == 1) {
				list.add(file.pop());
			}else {
				list.add(repository.pop());
			}
		}
		while(file.size() != 0)
			list.add(file.pop());
		while(repository.size() != 0) {
			list.add(repository.pop());
		}
		
		return list;
	}
}
