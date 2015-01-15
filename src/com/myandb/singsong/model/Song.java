package com.myandb.singsong.model;

import java.util.List;
import java.util.Random;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.myandb.singsong.App;
import com.myandb.singsong.R;
import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.activity.RootActivity;
import com.myandb.singsong.activity.UpActivity;
import com.myandb.singsong.audio.OnPlayEventListener;
import com.myandb.singsong.audio.PlayEvent;
import com.myandb.singsong.event.ActivateOnlyClickListener;
import com.myandb.singsong.fragment.ChildrenSongFragment;
import com.myandb.singsong.fragment.KaraokeFragment;
import com.myandb.singsong.fragment.ListFragment;
import com.myandb.singsong.net.JSONObjectRequest;
import com.myandb.singsong.service.PlayerService;
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
		"저랑 같이 노래부르실 분~",
		"콜라보해주세요!",
		"저랑 콜라보하실 분!",
		"꺼져가는 제 노래에,\n새 생명 불어넣어주실 분!",
		"콜라보하실 분!\n여기여기 붙어라~",
		"콜라보해주세요!\n뿌잉뿌잉 *-_-*",
		"저랑 콜라보하실 분~",
		"남은 파트를 불러주세요!"
	};
	private static String[] randomLeafMessages = {
		"남은 파트 완성해봤어요!",
		"같이 부르니까 완전 재밌네요!",
		"남은 파트 완성!\n같이 부르니까 더 재밌네요 :)",
		"콜라보 완성!",
		"같이 부르는 재미가 쏠쏠하네요!"
	};
	
	private User user;
	private Music music;
	private Song song;
	private List<Song> children;
	private String file;
	private String message;
	private List<Image> photos;
	private Category category;
	private int duration;
	private int lyric_part;
	private int collabo_num;
	private int comment_num;
	private int liking_num;
	private int song_id;
	private int genre_id;
	
	public String getAudioUrl() {
		return STORAGE_HOST + STORAGE_SONG + file;
	}
	
	public String getSampleUrl() {
		return STORAGE_HOST + STORAGE_SONG + file.replace(".ogg", ".sample.ogg");
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
		if (isRoot()) {
			return this;
		} else {
			song.setMusic(getMusic());
			return song;
		}
	}
	
	public List<Song> getChildren() {
		return children;
	}
	
	public int getCollaboNum() {
		return collabo_num;
	}
	
	public String getWorkedCollaboNum() {
		return safeString(collabo_num);
	}
	
	public int getCommentNum() {
		return comment_num;
	}
	
	public String getWorkedCommentNum() {
		return safeString(comment_num);
	}
	
	public int getLikeNum() {
		return liking_num;
	}
	
	public String getWorkedLikeNum() {
		return safeString(liking_num);
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
		if (hasPhoto()) {
			return photos.get(0).getUrl();
		} else {
			return randomPhotos[random.nextInt(randomPhotos.length)];
		}
	}
	
	public boolean hasPhoto() {
		return photos != null && photos.size() > 0;
	}
	
	public int getPhotoSize() {
		if (hasPhoto()) {
			return photos.size();
		} else {
			return 0;
		}
	}
	
	public int getTotalPhotoSize() {
		if (isRoot()) {
			return getPhotoSize();
		} else {
			return getPhotoSize() + song.getPhotoSize();
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
	
	public Category getCategory() {
		if (category == null) {
			category = new Category(genre_id);
		}
		return category;
	}

	public OnClickListener getPlayClickListener() {
		return new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				BaseActivity activity = (BaseActivity) v.getContext();
				PlayerService service = activity.getPlayerService();
				service.startPlaying(Song.this);
			}
		};
	}
	
	public OnClickListener getSampleClickListener() {
		return new OnClickListener() {
			
			private ImageView icon;
			private View parentView;
			
			@Override
			public void onClick(final View view) {
				BaseActivity activity = (BaseActivity) view.getContext();
				PlayerService service = activity.getPlayerService();
				parentView = view;
				icon = getIcon(parentView);
				
				if (isPlaying(parentView)) {
					service.stopSample();
				} else {
					service.startSample(Song.this, eventListener);
				}
			}
			
			private ImageView getIcon(View v) {
				if (v instanceof ImageView) {
					return (ImageView) v;
				} else if (v instanceof ViewGroup) {
					View child = ((ViewGroup) v).getChildAt(0);
					if (child instanceof ImageView) {
						return (ImageView) child;
					}
				}
				return null;
			}
			
			private boolean isPlaying(View view) {
				if (view == null) {
					return false;
				}
				return view.getTag() == null ? false : (Boolean) view.getTag();
			}
			
			private OnPlayEventListener eventListener = new OnPlayEventListener() {
				
				@Override
				public void onPlay(PlayEvent event) {
					if (parentView == null) {
						return;
					}
					
					switch (event) {
					case PLAY:
						if (icon != null) {
							icon.setImageResource(R.drawable.ic_pause_basic);
						}
						parentView.setTag(true);
						break;
						
					case COMPLETED:
					case PAUSE:
						if (icon != null) {
							icon.setImageResource(R.drawable.ic_play_basic);
						}
						parentView.setTag(false);
						break;
						
					case ERROR:
						Toast.makeText(parentView.getContext(), "미리듣기가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
						break;
						
					default:
						break;
					}
				}
			};
		};
	}
	
	public OnClickListener getCollaboClickListner() {
		return new ActivateOnlyClickListener() {
			
			private Context context;
			
			@Override
			public void onActivated(View v, User user) {
				context = v.getContext();
				JSONObjectRequest request = new JSONObjectRequest(
						"songs/" + getParentSong().getId(), null,
						successListener, errorListener);
				((App) context.getApplicationContext()).addShortLivedRequest(context, request);
			}
			
			private Listener<JSONObject> successListener = new Listener<JSONObject>() {

				@Override
				public void onResponse(JSONObject response) {
					Bundle bundle = new Bundle();
					bundle.putString(KaraokeFragment.EXTRA_PARENT_SONG, getParentSong().toString());
					Intent intent = new Intent(context, UpActivity.class);
					intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, KaraokeFragment.class.getName());
					intent.putExtra(BaseActivity.EXTRA_FRAGMENT_BUNDLE, bundle);
					intent.putExtra(UpActivity.EXTRA_FULL_SCREEN, true);
					intent.putExtra(UpActivity.EXTRA_SHOULD_STOP, true);
					context.startActivity(intent);
				}
			};
			
			private ErrorListener errorListener = new ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					Toast.makeText(context, context.getString(R.string.t_deleted_song), Toast.LENGTH_SHORT).show();
				}
			};
			
		};
	}
	
	public OnClickListener getChildrenClickListener() {
		return new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Context context = v.getContext();
				Bundle bundle = new Bundle();
				bundle.putString(ChildrenSongFragment.EXTRA_ROOT_SONG, getParentSong().toString());
				bundle.putInt(ListFragment.EXTRA_COLUMN_NUM, 2);
				Intent intent = new Intent(context, RootActivity.class);
				intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, ChildrenSongFragment.class.getName());
				intent.putExtra(BaseActivity.EXTRA_FRAGMENT_BUNDLE, bundle);
				((BaseActivity) context).changePage(intent);
			}
		};
	}
	
}
