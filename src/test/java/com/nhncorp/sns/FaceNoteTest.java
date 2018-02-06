package com.nhncorp.sns;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.nhncorp.sns.model.ImagePath;
import com.nhncorp.sns.model.Path;
import com.nhncorp.sns.model.Post;
import com.nhncorp.sns.model.TextPath;
import com.nhncorp.sns.model.User;

/**
 * @author taeshik.heo
 */
public class FaceNoteTest {
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	FaceNote faceNote = new FaceNote();

	@Before
	public void setUp() {
		faceNote.setWorkDirectory(folder.getRoot());
	}

	/**
	 * 1. 타임라인 목록은 사용자별 파일로 저장되며, 저장될 파일명은 사용자의 userId입니다.
	 * 
	 * @throws Exception
	 */
	@Test
	public void writePost() throws Exception {
		// given
		writeTextPost("hong", new String[]{"jobs", "mike"}, "Welcome to FaceNote!");
		// when

		//then
		assertTrue(new File(faceNote.getWorkDirectory(), "hong").exists());
		assertTrue(new File(faceNote.getWorkDirectory(), "jobs").exists());
		assertTrue(new File(faceNote.getWorkDirectory(), "mike").exists());
		
	}
	
	@Test
	public void writePost2() throws Exception {
		// given
		writeImagePost("hong2", new String[]{"jobs2", "mike2"}, "Welcome to FaceNote!", "Welcome to FaceNote!");
		// when

		//then
		
		assertTrue(new File(faceNote.getWorkDirectory(), "hong2").exists());
		assertTrue(new File(faceNote.getWorkDirectory(), "jobs2").exists());
		assertTrue(new File(faceNote.getWorkDirectory(), "mike2").exists());
	}

	
	/**
	 * 2. 사용자는 타임라인 목록에서 친구들이 작성한 글들과 자신이 작성한 글을 볼 수 있어야 합니다.
	 * @throws Exception
	 */
	@Test
	public void getTimeLineList() throws Exception {
		// given
		writeTextPost("hong", new String[]{"jobs", "bill"}, "Welcome to FaceNote!");
		writeImagePost("mike", new String[]{"hong", "jobs"}, "Hello FaceNote!!", "http://naver.com/noimage.jpg");

		// when
		Collection<Post> billTimeLineList = faceNote.getTimeLineList("bill");
		Collection<Post> mikeTimeLineList = faceNote.getTimeLineList("mike");
		Collection<Post> hongTimeLineList = faceNote.getTimeLineList("hong");
		Collection<Post> jobsTimeLineList = faceNote.getTimeLineList("jobs");
		
		// then
		assertThat(hongTimeLineList.size(), is(2));
		assertThat(jobsTimeLineList.size(), is(2));
		assertThat(billTimeLineList.size(), is(1));
		assertThat(mikeTimeLineList.size(), is(1));
	}
	
	
	/**
	 * 3. 사용자의 타임라인 목록은 작성시간 내림차순으로 정렬되어야 합니다.( 최신글이 상위에 조회)
	 * @throws Exception
	 */
	@Test
	public void getTimeLineListWithOrdering() throws Exception {
		// given
		writeTextPost("hong", new String[]{"jobs", "bill"}, "This is a first Post");
		writeImagePost("mike", new String[]{"hong", "jobs"}, "This is a second Post", "http://naver.com/noimage.jpg");
		
		// when
		Collection<Post> hongTimeLineList = faceNote.getTimeLineList("hong");
		Collection<Post> jobsTimeLineList = faceNote.getTimeLineList("jobs");
		
		// then
		Object [] hongTimeLineArray = hongTimeLineList.toArray();
		assertThat(((Post)hongTimeLineArray[0]).toString(), containsString("This is a second Post"));
		assertThat(((Post)hongTimeLineArray[1]).toString(), containsString("This is a first Post")); 
		
		Object [] jobsTimeLineArray = jobsTimeLineList.toArray();
		assertThat(((Post)jobsTimeLineArray[0]).toString(), containsString("This is a second Post"));
		assertThat(((Post)jobsTimeLineArray[1]).toString(), containsString("This is a first Post")); 
	}

	
	/**
	 * 4. 친구 수가 1만 명이 넘는 사용자가 등록한 글은 별도의 목록으로 메모리에 저장합니다.
	 * - Singleton pattern 사용.
	 * @throws Exception
	 */
	@Test
	public void writeHeavyUserPost() throws Exception {
		// given
		User heavyUser = createHeavyUser("hong", "hongsFriend_");
		writeHeavyuserTextPost(heavyUser, "I'm heavy user.");
		
		// when

		// then
		assertThat(HeavyUserPostRepository.getPosts().size(), is(1));
		assertTrue(new File(faceNote.getWorkDirectory(), "hong").exists());
		assertFalse(new File(faceNote.getWorkDirectory(), "hongsFriend_1").exists());
	}

	
	/**
	 * 5. 사용자는 타임라인 목록에서 친구 수가 1만 명이 넘는 친구의 글도 정렬된 상태로 함께 볼 수 있어야 합니다. 
	 * 작성시간 내림차순으로 정렬되어야 합니다.( 최신글이 상위에 조회)
 	 * 
	 * @throws Exception
	 */
	@Test
	public void getNormalUserAndHeavyUserList() throws Exception {
		// given
		User heavyUser = createHeavyUser("tsheo", "friend_"); //friend_1,friend_2, friend_3,... 
		writeHeavyuserTextPost(heavyUser, "Hey guys, I'm heavy user.");

		writeTextPost("friend_1", new String[]{"friend_2", "friend_3"}, "You've got a Message From Friend_1");
		writeTextPost("friend_2", new String[]{"anotherFriend_X", "tsheo"}, "You've got a Message From Friend_2");
		
		// when
		Collection<Post> tsheoTimeLineList = faceNote.getTimeLineList("tsheo");
		Collection<Post> friend1TimeLineList = faceNote.getTimeLineList("friend_1");
		Collection<Post> friend2TimeLineList = faceNote.getTimeLineList("friend_2");
		Collection<Post> anotherFriendTimeLineList = faceNote.getTimeLineList("anotherFriend_X");
		
		// then
		assertThat(tsheoTimeLineList.size(), is(2)); //post : tsheo, friends_2 
		assertThat(friend1TimeLineList.size(), is(2)); //post : tsheo, friends_1
		assertThat(friend2TimeLineList.size(), is(3)); //post : tsheo, friends_1, friends_2
		assertThat(anotherFriendTimeLineList.size(), is(1)); //post : friends_2
		
 		//정렬순서 확인
		Object [] objArray = friend2TimeLineList.toArray();

		assertThat(((Post)objArray[0]).toString(), containsString("You've got a Message From Friend_2")); 
		assertThat(((Post)objArray[1]).toString(), containsString("You've got a Message From Friend_1")); 
		assertThat(((Post)objArray[2]).toString(), containsString("Hey guys, I'm heavy user.")); 
	}

	/**
	 * HeavyUser 생성하는 private helper method
	 * @param userId
	 * @param prefix
	 * @return
	 */
	private User createHeavyUser(String userId, String prefix) {
		List<String> heavyFriends = new ArrayList<String>();
		for (int i = 0; i < FaceNote.FRIENDS_COUNT_LIMIT; i++) {
			heavyFriends.add(prefix + i);
		}
		User heavyUser = new User(userId, heavyFriends);
		return heavyUser;
	}
	
	/**
	 * text post 쓰는 private method. 
	 * 클래스 구조 설계하고 수정해야 합니다. 
	 * @param writerId
	 * @param receiverIds
	 * @param content
	 */
	private void writeTextPost(String writerId, String[] receiverIds, String content) {
		List<String> friends = Arrays.asList(receiverIds);
		User writer = new User(writerId, friends);
		Post textPost = new Post();
		Path textPath = new TextPath();
		textPost.setPathType(textPath);
		textPost.setContent(content);
		faceNote.writePost(writer, textPost);
	}

	public void writeHeavyuserTextPost(User writer, String content) {
		faceNote.writePost(writer, new Post());
	}
	
	/**
	 * image post 쓰기위한 private method. 
	 * 클래스 구조 설계하고 수정해야 합니다. 
	 * @param writerId
	 * @param receiverIds
	 * @param content
	 */
	private void writeImagePost(String writerId, String[] receiverIds, String content, String imageUrl) {
		List<String> friends = Arrays.asList(receiverIds);
		User writer = new User(writerId, friends);
		
		Post imagePost = new Post();
		Path imagePath = new ImagePath();
		imagePost.setPathType(imagePath);
		
		
		imagePost.setContent(content);
		imagePost.setPathContent(imageUrl);
		
		faceNote.writePost(writer, imagePost);
	}
}
