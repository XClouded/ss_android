package com.myandb.singsong.adapter;

import java.io.File;
import java.util.ArrayList;

import com.myandb.singsong.R;
import com.myandb.singsong.file.FileManager;
import com.myandb.singsong.file.Storage;
import com.myandb.singsong.model.MenuData;
import com.myandb.singsong.model.MenuData.PageName;
import com.myandb.singsong.util.ImageHelper.BitmapBuilder;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuAdapter extends BaseAdapter {
	
	private ArrayList<MenuData> menuDatas;
	private LayoutInflater inflater;
	
	public MenuAdapter(Context context, ArrayList<MenuData> menuDatas) {
		this.menuDatas = menuDatas;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		return menuDatas.size();
	}

	@Override
	public Object getItem(int position) {
		return menuDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		MenuData menu = menuDatas.get(position);
		
		if (menu.getPageName() == PageName.MY_PAGE) {
			view = inflater.inflate(R.layout.row_menu_profile, null);
		} else {
			view = inflater.inflate(R.layout.row_menu, null);
		}
		
		TextView tvGnbTitle = (TextView) view.findViewById(R.id.tv_gnb_title);
		ImageView ivGnbIcon = (ImageView)view.findViewById(R.id.iv_gnb_icon);
		View rlGnbRow = view.findViewById(R.id.rl_gnb_row);
		
		tvGnbTitle.setText(menu.getOutText());
		
		if (VERSION.SDK_INT > VERSION_CODES.GINGERBREAD_MR1 && menu.getPageType() == MenuData.FRAGMENT) {
			rlGnbRow.setBackgroundResource(R.drawable.menu_fragment_selector);
		} else {
			rlGnbRow.setBackgroundResource(R.drawable.menu_activity_selector);
		}
		
		if (menu.getPageName() == PageName.MY_PAGE) {
			if (FileManager.isExist(FileManager.USER_PHOTO)) {
				ivGnbIcon.setImageBitmap(getUserBitmap());
			}
		} else {
			ivGnbIcon.setImageBitmap(menu.getIcon());
		}
		
		if (menu.getPageName() == PageName.NOTIFICATION) {
			TextView tvPushNum = (TextView) view.findViewById(R.id.tv_push_num);
			Storage session = new Storage();
			
			int unreadPushNum = session.getUnreadPushNum();
			if (unreadPushNum > 0) {
				tvPushNum.setVisibility(View.VISIBLE);
				tvPushNum.setText(String.valueOf(unreadPushNum));
			} else {
				tvPushNum.setVisibility(View.GONE);
			}
		}
		
		return view;
	}
	
	private Bitmap getUserBitmap() {
		File userPhotoFile = FileManager.get(FileManager.USER_PHOTO);
		BitmapBuilder bitmapBuilder = new BitmapBuilder();
		Bitmap bitmap = bitmapBuilder.setSource(userPhotoFile).enableCrop(false).build();
		
		return bitmap;
	}

}
