package com.myandb.singsong.fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.myandb.singsong.R;
import com.myandb.singsong.adapter.CommentAdapter;
import com.myandb.singsong.adapter.SimpleSongAdapter;
import com.myandb.singsong.event.ActivateOnlyClickListener;
import com.myandb.singsong.image.ImageHelper;
import com.myandb.singsong.model.Artist;
import com.myandb.singsong.model.Comment;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.GradualLoader;
import com.myandb.singsong.net.JSONErrorListener;
import com.myandb.singsong.net.JSONObjectRequest;
import com.myandb.singsong.net.JSONObjectSuccessListener;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.net.GradualLoader.OnLoadCompleteListener;
import com.myandb.singsong.pager.PagerWrappingAdapter;
import com.myandb.singsong.util.Utility;

public class ArtistDetailFragment extends ListFragment {
	
	public static final String EXTRA_ARTIST = "artist";
	
	private TextView tvUserNickname;
	private TextView tvFollowersNum;
	private TextView tvArtistNum;
	private TextView tvArtistSongs;
	private TextView tvArtistCommentNum;
	private TextView tvArtistIntroduction;
	private ImageView ivArtistPhoto;
	private Button btnSubmitComment;
	private EditText etComment;
	private ViewPager vpArtistSongs;
	private Artist artist;
	private PagerWrappingAdapter artistSongAdapter;

	@Override
	protected int getListHeaderViewResId() {
		return R.layout.fragment_artist_detail_header;
	}

	@Override
	protected void onArgumentsReceived(Bundle bundle) {
		super.onArgumentsReceived(bundle);
		Gson gson = Utility.getGsonInstance();
		String artistInJson = bundle.getString(EXTRA_ARTIST);
		artist = gson.fromJson(artistInJson, Artist.class);
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		super.onViewInflated(view, inflater);
		
		view = getListHeaderView();
		tvUserNickname = (TextView) view.findViewById(R.id.tv_artist_user_nickname);
		tvFollowersNum = (TextView) view.findViewById(R.id.tv_artist_followers_num);
		tvArtistNum = (TextView) view.findViewById(R.id.tv_artist_num);
		tvArtistSongs = (TextView) view.findViewById(R.id.tv_artist_songs);
		tvArtistCommentNum = (TextView) view.findViewById(R.id.tv_artist_comment_num);
		tvArtistIntroduction = (TextView) view.findViewById(R.id.tv_artist_introduction);
		
		ivArtistPhoto = (ImageView) view.findViewById(R.id.iv_artist_photo);
		btnSubmitComment = (Button) view.findViewById(R.id.btn_submit_comment);
		etComment = (EditText) view.findViewById(R.id.et_comment);
		vpArtistSongs = (ViewPager) view.findViewById(R.id.vp_artist_songs);
	}

	@Override
	protected ListAdapter instantiateAdapter(Activity activity) {
		return new CommentAdapter();
	}

	@Override
	protected UrlBuilder instantiateUrlBuilder(Activity activity) {
		return new UrlBuilder().s("artists").s(artist.getId()).s("comments");
	}

	@Override
	protected void initialize(Activity activity) {
		super.initialize(activity);
		setListShown(true);
	}

	@Override
	protected void setupViews(Bundle savedInstanceState) {
		super.setupViews(savedInstanceState);
		
		User user = artist.getUser();
		tvArtistNum.setText(getString(R.string.fragment_artist_num_prefix) + String.valueOf(artist.getId()));
		tvUserNickname.setText(user.getNickname());
		tvArtistIntroduction.setText("\"" + artist.getIntroduction() + "\"");
		if (user.getProfile() != null) {
			tvFollowersNum.setText(String.valueOf(user.getProfile().getFollowersNum()));
		}
		ImageHelper.displayPhoto(user, ivArtistPhoto);
		ivArtistPhoto.setOnClickListener(user.getProfileClickListener());
		
		tvArtistSongs.setText(user.getNickname() + getString(R.string.fragment_artist_song_title_suffix));
		tvArtistCommentNum.setText(user.getNickname() + getString(R.string.fragment_artist_comment_title_suffix));
		tvArtistCommentNum.append(" (");
		tvArtistCommentNum.append(artist.getCommentNum());
		tvArtistCommentNum.append(")");
		
		btnSubmitComment.setOnClickListener(submitCommentClickListner);
		
		loadUserSongs(user);
		
		enableFadingActionBar(false);
		
		int padding = getResources().getDimensionPixelSize(R.dimen.margin);
		vpArtistSongs.setPadding(padding, 0, padding, 0);
		vpArtistSongs.setClipToPadding(false);
		vpArtistSongs.setPageMargin(padding / 2);
	}
	
	private void loadUserSongs(User user) {
		if (artistSongAdapter == null) {
			final SimpleSongAdapter adapter = new SimpleSongAdapter();
			artistSongAdapter = new PagerWrappingAdapter(adapter);
			final UrlBuilder urlBuilder = new UrlBuilder().s("users").s(user.getId()).s("songs").s("all").p("order", "liking_num").take(5);
			final GradualLoader loader = new GradualLoader(getActivity());
			loader.setUrlBuilder(urlBuilder);
			loader.setOnLoadCompleteListener(new OnLoadCompleteListener() {
				
				@Override
				public void onComplete(JSONArray response) {
					adapter.addAll(response);
					vpArtistSongs.setAdapter(artistSongAdapter);
				}
			});
			loader.load();
		} else {
			vpArtistSongs.setAdapter(artistSongAdapter);
		}
	}
	
	private OnClickListener submitCommentClickListner = new ActivateOnlyClickListener() {
		
		@Override
		public void onActivated(View v, User user) {
			String comment = etComment.getText().toString();
			if (comment.trim().length() > 0) {
				JSONObject message = new JSONObject();
				try {
					message.put("user_id", user.getId());
					message.put("content", comment);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				JSONObjectRequest request = new JSONObjectRequest(
						"artists/" + artist.getId() + "/comments", null, message,
						new JSONObjectSuccessListener(ArtistDetailFragment.this, "onSubmitSuccess", Comment.class),
						new JSONErrorListener(ArtistDetailFragment.this, "onSubmitError")
				);
				addRequest(request);
				etComment.setText("");
			} else {
				makeToast(R.string.t_alert_comment_validation_failed);
			}
		}
	};
	
	public void onSubmitSuccess(Comment<?> response) {
		addComment(response);
	}
	
	public void onSubmitError() {
		
	}
	
	private void addComment(Comment<?> comment) {
		((CommentAdapter) getAdapter()).addItemToHead(comment);
	}

	@Override
	public void onResume() {
		super.onResume();
		setFadingActionBarTitle(artist.getNickname());
	}

	@Override
	public void onDestroyView() {
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE); 
		imm.hideSoftInputFromWindow(etComment.getWindowToken(), 0);
		super.onDestroyView();
	}

}
