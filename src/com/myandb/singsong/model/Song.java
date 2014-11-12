package com.myandb.singsong.model;

import java.util.List;
import java.util.Random;

import com.myandb.singsong.util.StringFormatter;

public class Song extends Model {
	
	private static final int MESSAGE_MAX_DISPLAYED_LENGTH = 30;
	
	private static Random random = new Random(); 
	private static String[] randomPhotos = {
		"http://14.63.171.91:8880/ss_api/public/img/random/1.jpg",
		"http://14.63.171.91:8880/ss_api/public/img/random/2.jpg",
		"http://14.63.171.91:8880/ss_api/public/img/random/3.jpg",
		"http://14.63.171.91:8880/ss_api/public/img/random/4.jpg",
		"http://14.63.171.91:8880/ss_api/public/img/random/5.jpg",
		"http://14.63.171.91:8880/ss_api/public/img/random/6.jpg",
		"http://14.63.171.91:8880/ss_api/public/img/random/7.jpg",
		"http://14.63.171.91:8880/ss_api/public/img/random/8.jpg",
		"http://14.63.171.91:8880/ss_api/public/img/random/9.jpg"
	};
	private static String[] randomRootMessages = {
		"���� ���� �뷡�θ��� ��~",
		"�ݶ����ּ���!",
		"���� �ݶ��Ͻ� ��!",
		"�������� �� �뷡��,\n�� ���� �Ҿ�־��ֽ� ��!",
		"�ݶ��Ͻ� ��!\n���⿩�� �پ��~",
		"�ݶ����ּ���!\n���׻��� *-_-*",
		"���� �ݶ��Ͻ� ��~",
		"���� ��Ʈ�� �ҷ��ּ���!"
	};
	private static String[] randomLeafMessages = {
		"���� ��Ʈ �ϼ��غþ��!",
		"���� �θ��ϱ� ���� ��ճ׿�!",
		"���� ��Ʈ �ϼ�!\n���� �θ��ϱ� �� ��ճ׿� :)",
		"�ݶ� �ϼ�!",
		"���� �θ��� ��̰� ����ϳ׿�!"
	};
	
	private User user;
	private Music music;
	private Song song;
	private List<Song> children;
	private String file;
	private String message;
	private List<Image> photos;
	private int duration;
	private int lyric_part;
	private int collabo_num;
	private int comment_num;
	private int liking_num;
	private int song_id;
	
	public String getAudioUrl() {
		return STORAGE_HOST + STORAGE_SONG + file;
	}
	
	public User getParentUser() {
		if (isRoot()) {
			return getCreator();
		} else {
			return song != null ? song.getCreator() : null;
		}
	}
	
	public boolean isRoot() {
		return song_id <= 0;
	}
	
	public String getCroppedMessage() {
		String original = getMessage();
		
		if (original.length() > MESSAGE_MAX_DISPLAYED_LENGTH) {
			return original.substring(0, MESSAGE_MAX_DISPLAYED_LENGTH) + "..";
		} else {
			return original;
		}
	}
	
	public String getMessage() {
		if (message != null && !message.isEmpty()) {
			return message;
		} else {
			if (isRoot()) {
				return randomRootMessages[random.nextInt(randomRootMessages.length)];
			} else {
				return randomLeafMessages[random.nextInt(randomLeafMessages.length)];
			}
		}
	}
	
	public User getCreator() {
		return user;
	}
	
	public Music getMusic() {
		return music;
	}
	
	public void setMusic(Music music) {
		this.music = music;
	}
	
	public Song getParentSong() {
		return isRoot() ? this : song;
	}
	
	public List<Song> getChildren() {
		return children;
	}
	
	public int getCollaboNum() {
		return collabo_num;
	}
	
	public String getWorkedCollaboNum() {
		return toString(collabo_num);
	}
	
	public int getCommentNum() {
		return comment_num;
	}
	
	public String getWorkedCommentNum() {
		return toString(comment_num);
	}
	
	public int getLikeNum() {
		return liking_num;
	}
	
	public String getWorkedLikeNum() {
		return toString(liking_num);
	}
	
	public int getLyricPart() {
		return lyric_part;
	}
	
	public int getPartnerLyricPart() {
		return getLyricPart() == Music.PART_MALE ? Music.PART_FEMALE : Music.PART_MALE;
	}
	
	public String getPartName() {
		if (music != null) {
			return getLyricPart() == Music.PART_MALE ? music.getMalePart() : music.getFemalePart();
		} else {
			return "music is null";
		}
	}
	
	public String getParentPartName() {
		if (music != null) {
			if (isRoot()) {
				return getPartName();
			} else {
				return getLyricPart() == Music.PART_MALE ? music.getFemalePart() : music.getMalePart();
			}
		} else {
			return "music is null";
		}
	}
	
	public int getDuration() {
		return duration;
	}
	
	public String getWorkedDuration() {
		return StringFormatter.getDuration(duration);
	}
	
	public List<Image> getPhotos() {
		return photos;
	}
	
	public String getPhotoUrl() {
		if (photos != null && photos.size() > 0) {
			return photos.get(0).getUrl();
		} else {
			return randomPhotos[random.nextInt(randomPhotos.length)];
		}
	}
	
	public void incrementCommentNum() {
		comment_num++;
	}
	
	public void decrementCommentNum() {
		comment_num--;
	}
	
	public void incrementLikeNum() {
		liking_num++;
	}
	
	public void decrementLikeNum() {
		liking_num--;
	}
}
