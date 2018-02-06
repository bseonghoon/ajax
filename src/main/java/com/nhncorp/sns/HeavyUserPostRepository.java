/*
 * @(#)HeavyUserMessages.java $version 2014. 8. 7.
 *
 * Copyright 2007 NHN Corp. All rights Reserved. 
 * NHN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.nhncorp.sns;

import java.util.ArrayList;
import java.util.List;

import com.nhncorp.sns.model.Post;

/**
 * @author taeshik.heo
 */
public class HeavyUserPostRepository {
	static List<Post> heavyUserPost;

	private static HeavyUserPostRepository instance = new HeavyUserPostRepository();

	private HeavyUserPostRepository() {
		heavyUserPost = new ArrayList<>();
	}

	public synchronized static HeavyUserPostRepository getInstance() {
		if (instance == null) {
			instance = new HeavyUserPostRepository();
		}
		return instance;
	}

	public static List<Post> getPosts() {
		return heavyUserPost;
	}
	
	public void addPosts(Post post) {
		heavyUserPost.add(post);
	}

}
