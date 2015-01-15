package com.myandb.singsong.fragment;

import org.json.JSONArray;

import com.myandb.singsong.R;
import com.myandb.singsong.adapter.ChildrenSongAdapter;
import com.myandb.singsong.net.GradualLoader;
import com.myandb.singsong.net.GradualLoader.OnLoadCompleteListener;
import com.myandb.singsong.net.UrlBuilder;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;

public class MusicDetailListFragment extends BaseFragment {
	
	public enum Type {
		POPULAR,
		RECENT,
		FRIEND;
	}
	
	public static final String EXTRA_LIST_TYPE = "list_type";
	public static final String EXTRA_MUSIC_ID = "music_id";
	
	private GridView gridView;
	private ChildrenSongAdapter adapter;
	private UrlBuilder urlBuilder;
	private GradualLoader loader;

	@Override
	protected int getResourceId() {
		return R.layout.fragment_music_detail_list;
	}

	@Override
	protected void onArgumentsReceived(Bundle bundle) {
		super.onArgumentsReceived(bundle);
		Type type = (Type) bundle.getSerializable(EXTRA_LIST_TYPE);
		int musicId = bundle.getInt(EXTRA_MUSIC_ID);
		urlBuilder = new UrlBuilder();
		
		switch (type) {
		case POPULAR:
			urlBuilder.s("musics").s(musicId).s("songs").s("root").p("order", "liking_num");
			break;
			
		case RECENT:
			urlBuilder.s("musics").s(musicId).s("songs").s("root").p("order", "created_at");
			break;
			
		case FRIEND:
			urlBuilder.s("followings").s("musics").s(musicId).s("songs").s("root");
			break;
		}
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		gridView = (GridView) view.findViewById(R.id.gridview);
	}

	@Override
	protected void initialize(Activity activity) {}

	@Override
	protected void setupViews(Bundle savedInstanceState) {
		if (adapter == null) {
			adapter = new ChildrenSongAdapter();
			loader = new GradualLoader(getActivity());
			loader.setUrlBuilder(urlBuilder);
			loader.setOnLoadCompleteListener(new OnLoadCompleteListener() {
				
				@Override
				public void onComplete(JSONArray response) {
					adapter.addAll(response);
					if (gridView.getAdapter() == null) {
						gridView.setAdapter(adapter);
					}
				}
			});
		} else {
			gridView.setAdapter(adapter);
		}
		
		if (loader != null) {
			gridView.setOnScrollListener(loader);
		}
	}

	@Override
	protected void onDataChanged() {}

}
