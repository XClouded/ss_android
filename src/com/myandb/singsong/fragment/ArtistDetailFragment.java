package com.myandb.singsong.fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.myandb.singsong.R;
import com.myandb.singsong.adapter.CollaboratedAdapter;
import com.myandb.singsong.adapter.CommentAdapter;
import com.myandb.singsong.adapter.QnaAdapter;
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
import com.myandb.singsong.util.Utility;
import com.myandb.singsong.widget.HorizontalAdapterView;

public class ArtistDetailFragment extends ListFragment {
	
	public static final String EXTRA_ARTIST = "artist";
	
	private TextView tvUserNickname;
	private TextView tvArtistNickname;
	private TextView tvFollowersNum;
	private TextView tvArtistNum;
	private TextView tvArtistIntroduction;
	private TextView tvArtistSongs;
	private TextView tvArtistCommentNum;
	private ImageView ivArtistPhoto;
	private Button btnSubmitComment;
	private EditText etComment;
	private ViewGroup vgQnaContainer;
	private HorizontalAdapterView havArtistSongs;
	private Artist artist;

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
		tvArtistNickname = (TextView) view.findViewById(R.id.tv_artist_nickname);
		tvFollowersNum = (TextView) view.findViewById(R.id.tv_artist_followers_num);
		tvArtistNum = (TextView) view.findViewById(R.id.tv_artist_num);
		tvArtistIntroduction = (TextView) view.findViewById(R.id.tv_artist_introduction);
		tvArtistSongs = (TextView) view.findViewById(R.id.tv_artist_songs);
		tvArtistCommentNum = (TextView) view.findViewById(R.id.tv_artist_comment_num);
		
		ivArtistPhoto = (ImageView) view.findViewById(R.id.iv_artist_photo);
		btnSubmitComment = (Button) view.findViewById(R.id.btn_submit_comment);
		etComment = (EditText) view.findViewById(R.id.et_comment);
		vgQnaContainer = (ViewGroup) view.findViewById(R.id.ll_qna_container);
		havArtistSongs = (HorizontalAdapterView) view.findViewById(R.id.hav_artist_songs);
	}

	@Override
	protected ListAdapter instantiateAdapter(Activity activity) {
		return new CommentAdapter();
	}

	@Override
	protected UrlBuilder instantiateUrlBuilder(Activity activity) {
		return new UrlBuilder().s("artist").s(artist.getId()).s("comments");
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
		tvUserNickname.setText(user.getNickname());
		tvArtistNickname.setText(artist.getNickname());
//		tvFollowersNum.setText(user.getProfile().getFollowersNum());
		tvArtistNum.setText(String.valueOf(artist.getId()));
		tvArtistIntroduction.setText(artist.getIntroduction());
		ImageHelper.displayPhoto(user, ivArtistPhoto);
		
		tvArtistSongs.setText(user.getNickname() + "´ÔÀÇ ³ë·¡ µè±â");
		tvArtistCommentNum.setText(user.getNickname() + "´Ô¿¡°Ô ÇÑ¸¶µð");
		tvArtistCommentNum.append(" (");
		tvArtistCommentNum.append(artist.getCommentNum());
		tvArtistCommentNum.append(")");
		
		btnSubmitComment.setOnClickListener(submitCommentClickListner);
		
		loadUserSongs(user);
		
		QnaAdapter qnaAdapter = new QnaAdapter();
		qnaAdapter.addAll(artist.getQna());
		for (int i = 0, l = qnaAdapter.getCount(); i < l; i++) {
			View child = qnaAdapter.getView(i, null, vgQnaContainer);
			vgQnaContainer.addView(child);
		}
	}
	
	private void loadUserSongs(User user) {
		final CollaboratedAdapter adapter = new CollaboratedAdapter();
		havArtistSongs.setAdapter(adapter);
		final UrlBuilder urlBuilder = new UrlBuilder().s("users").s(user.getId()).s("songs").s("leaf").p("order", "liking_num").take(5);
		final GradualLoader loader = new GradualLoader(getActivity());
		loader.setUrlBuilder(urlBuilder);
		loader.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			
			@Override
			public void onComplete(JSONArray response) {
				adapter.addAll(response);
			}
		});
		loader.load();
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
						"artist/" + artist.getId() + "/comments", message,
						new JSONObjectSuccessListener(ArtistDetailFragment.this, "onSubmitSuccess", Comment.class),
						new JSONErrorListener(ArtistDetailFragment.this, "onSubmitError")
				);
				addRequest(request);
				etComment.setText("");
			} else {
				makeToast(R.string.t_comment_length_policy);
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

}
