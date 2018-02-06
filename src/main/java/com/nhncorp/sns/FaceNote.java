/*
 * @(#)Post.java $version 2014. 8. 7.
 *
 * Copyright 2007 NHN Corp. All rights Reserved. 
 * NHN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.nhncorp.sns;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Stack;

import com.nhncorp.sns.model.ImagePath;
import com.nhncorp.sns.model.Path;
import com.nhncorp.sns.model.Post;
import com.nhncorp.sns.model.TextPath;
import com.nhncorp.sns.model.User;
import com.nhncorp.sns.service.PostService;

/**
 * @author taeshik.heo
 */
public class FaceNote {
	public static final int FRIENDS_COUNT_LIMIT = 10000;
	public static final String NEW_LINE = "\r\n";

	public static final int TYPE = 0;
	public static final int WRITE_TIME = 1;
	public static final int WRITER = 2;
	public static final int RECEIVER = 3;
	public static final int CONTENT = 4;
	public static final int PATH = 5;

	private File workDirectory = new File(System.getProperty("user.home"));
	HeavyUserPostRepository heavyUserPostRepository;

	public void setWorkDirectory(File workDirectory) {
		this.workDirectory = workDirectory;
	}

	public File getWorkDirectory() {
		return this.workDirectory;
	}

	public FaceNote() {
		heavyUserPostRepository = HeavyUserPostRepository.getInstance();
	}

	/**
	 * 사용자아이디를 입력받아 해당사용자의 타임라인목록을 리턴.
	 * 
	 * @param userId
	 * @return
	 */
	public Collection<Post> getTimeLineList(String userId) {
		Stack<Post> stackFile = new Stack<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(getWorkDirectory() + "\\" + userId))) {
			while (true) {
				Post post = new Post();
				String postInfo = reader.readLine();
				if (postInfo == null) {
					break;
				}
				List<String> postSplitInfo = Arrays.asList(postInfo.split(Post.FIELD_SEPERATOR));
				String type = postSplitInfo.get(TYPE);
				if (type == "text") {
					Path path = new TextPath();
					post.setPathType(path);
				} else {
					Path path = new ImagePath();
					post.setPathType(path);
				}

				post.setType(type);

				post.setWriteTime(postSplitInfo.get(WRITE_TIME));
				post.setWriter(postSplitInfo.get(WRITER));
				String receiver = postSplitInfo.get(RECEIVER);
				receiver = receiver.substring(1, receiver.length() - 1);
				List<String> receiverList = Arrays.asList(postInfo.split(", "));
				post.setReceiver(receiverList);

				post.setContent(postSplitInfo.get(CONTENT));

				if (type.equals("image")) {
					post.setPathContent(postSplitInfo.get(PATH));
				}
				stackFile.push(post);
			}
		} catch (IOException e) {
			e.getStackTrace();
		}

		List<Post> heavyUserPostList = heavyUserPostRepository.getPosts();
		List<Post> list = new ArrayList<>();
		if (heavyUserPostList.size() == 0) {
			while(stackFile.size() != 0) {
				list.add(stackFile.pop());
			}
			return list;
		}
		
		Stack<Post> stackRepository = new Stack<>();
		for (Post post : heavyUserPostList) {
			stackRepository.push(post);
		}

		PostService service = new PostService();
		List<Post> sortingList = service.Sort(stackFile, stackRepository);

		return sortingList;
	}

	/**
	 * @param writer
	 * @param post
	 */
	public void writePost(User writer, Post post) {
		Post nowPost = null;
		PostService service = new PostService();
		if (post.getPath().equals("")) {
			nowPost = service.wirteTextPost(writer, post);
		} else {
			nowPost = service.wirteImagePost(writer, post);
		}

		List<String> postList = writer.getFriends();
		String userId = writer.getUserId();
		String postAllContents = nowPost.toString();

		try (BufferedWriter bufferedWriter = new BufferedWriter(
				new FileWriter(getWorkDirectory() + "\\" + userId, true))) {
			bufferedWriter.write(postAllContents);
			bufferedWriter.write(NEW_LINE);
		} catch (Exception e) {
			e.getStackTrace();
		}
		if (writer.getFriends().size() < FRIENDS_COUNT_LIMIT) {

			for (String listName : postList) {
				try (BufferedWriter bufferedWriter = new BufferedWriter(
						new FileWriter(getWorkDirectory() + "\\" + listName, true))) {
					bufferedWriter.write(postAllContents);
					bufferedWriter.write(NEW_LINE);
				} catch (Exception e) {
					e.getStackTrace();
				}
			}
		} else {
			heavyUserPostRepository.addPosts(nowPost);
		}
	}
}
