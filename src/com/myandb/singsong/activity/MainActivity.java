package com.myandb.singsong.activity;

import java.util.ArrayList;
import java.util.Date;

import com.google.android.gcm.GCMRegistrar;
import com.myandb.singsong.App;
import com.myandb.singsong.GCMIntentService;
import com.myandb.singsong.R;
import com.myandb.singsong.activity.SearchActivity.SearchType;
import com.myandb.singsong.activity.SimpleListActivity.SimpleListType;
import com.myandb.singsong.adapter.MenuAdapter;
import com.myandb.singsong.dialog.FrontNoticeDialog;
import com.myandb.singsong.file.Storage;
import com.myandb.singsong.fragment.CollaboratedFragment;
import com.myandb.singsong.fragment.LegendFragment;
import com.myandb.singsong.fragment.MusicFragment;
import com.myandb.singsong.fragment.ProfileRootFragment;
import com.myandb.singsong.fragment.WaitingFragment;
import com.myandb.singsong.model.MenuData;
import com.myandb.singsong.model.MenuData.PageName;
import com.myandb.singsong.model.Notice;
import com.myandb.singsong.secure.Auth;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends BaseActivity {
	
	public static final String INTENT_PAGE_REQUEST = "_page_req_my_";

	private static boolean isRunning = false;
	
	private DrawerLayout drawer;
	private ArrayList<MenuData> menuDatas;
	private ListView lvNavigation;
	private Toast toast;
	private Thread countThread;
	private MenuAdapter menuAdapter;
	private boolean pressedBackButton  = false;
	private boolean performedMenuClick = false;
	private int currentPosition = -1;
	private int requestCode;
	private FrontNoticeDialog noticeDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		isRunning = true;
		requestCode = getIntent().getIntExtra(INTENT_PAGE_REQUEST, -1);
		
		if (Auth.isLoggedIn()) {
			
			initializeMenuData();
			
			initializeMenu();
			
			initializeDrawer();
			
			setDrawerOnGnb(drawer, lvNavigation);
			
			checkGCM();
			
			checkNewNotice();  
		} else {
			finish();
		}
	}

	private void initializeMenuData() {
		menuDatas = new ArrayList<MenuData>();
		
		menuDatas.add(new MenuData(Auth.getUser().getNickname(), null, PageName.MY_PAGE));
		menuDatas.add(new MenuData("새로운 소식", BitmapFactory.decodeResource(getResources(), R.drawable.ic_megaphone_menu), PageName.NOTIFICATION));
		menuDatas.add(new MenuData("완성된 콜라보 듣기", BitmapFactory.decodeResource(getResources(), R.drawable.ic_collabo_menu), PageName.WORLD_SONG));
		menuDatas.add(new MenuData("MR(반주) 목록", BitmapFactory.decodeResource(getResources(), R.drawable.ic_mic_menu), PageName.MUSIC_LIST));
		menuDatas.add(new MenuData("콜라보를 기다려요!", BitmapFactory.decodeResource(getResources(), R.drawable.ic_waiting_menu), PageName.WAITING_COLLABO));
		menuDatas.add(new MenuData("레전드 콜라보", BitmapFactory.decodeResource(getResources(), R.drawable.ic_crown_menu), PageName.LEGEND));
		menuDatas.add(new MenuData("친구 찾기", BitmapFactory.decodeResource(getResources(), R.drawable.ic_magnifier_menu), PageName.FIND_USER));
		menuDatas.add(new MenuData("콜라보 아티스트", BitmapFactory.decodeResource(getResources(), R.drawable.ic_magnifier_menu), PageName.ARTIST));
		menuDatas.add(new MenuData("공지사항", BitmapFactory.decodeResource(getResources(), R.drawable.ic_balloon_menu), PageName.NOTICE));
		menuDatas.add(new MenuData("설정", BitmapFactory.decodeResource(getResources(), R.drawable.ic_wheel_menu), PageName.SETTING));
	}

	private void initializeMenu() {
		lvNavigation = (ListView) findViewById(R.id.lv_home_menu);

		menuAdapter = new MenuAdapter(this, menuDatas);
		lvNavigation.setAdapter(menuAdapter);
		lvNavigation.setOnItemClickListener(menuClickListener);
	}
	
	private void initializeDrawer() {
		drawer = (DrawerLayout) findViewById(R.id.dl_home_drawer);
		drawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		drawer.setDrawerListener(new DrawerListener() {
			
			@Override
			public void onDrawerStateChanged(int arg0) {
				menuAdapter.notifyDataSetChanged();
			}
			
			@Override
			public void onDrawerSlide(View arg0, float arg1) {}
			
			@Override
			public void onDrawerOpened(View arg0) {}
			
			@TargetApi(Build.VERSION_CODES.HONEYCOMB)
			@Override
			public void onDrawerClosed(View arg0) {
				if (VERSION.SDK_INT > VERSION_CODES.GINGERBREAD_MR1) {
					if (currentPosition >= 0 && currentPosition < menuDatas.size() && lvNavigation != null) {
						lvNavigation.getChildAt(currentPosition).setActivated(true);
					}
				}
			}
			
		});
	}
	
	private void checkGCM() {
		try {
			GCMRegistrar.checkDevice(this);
			GCMRegistrar.checkManifest(this);
			String registrationId = GCMRegistrar.getRegistrationId(this);
			
			if ("".equals(registrationId)) {
				GCMRegistrar.register(this, GCMIntentService.PROJECT_ID);
			}
		} catch (Exception e) {
			// Device does not have package com.google.android.gsf
		}
	}
	
	private void checkNewNotice() {
		Storage preferences = new Storage();
		Notice notice = preferences.getUnreadNotice();
		
		if (notice != null) {
			Date now = new Date();
			
			if (now.before(notice.getStartedTime()) && now.after(notice.getFinishTime())) {
				if (!notice.getFrontImageUrl().isEmpty()) {
					noticeDialog = new FrontNoticeDialog(this, notice);
					noticeDialog.show();
				}
			}
		}
	}
	
	private OnItemClickListener menuClickListener = new OnItemClickListener() {

		@Override 
		public void onItemClick(AdapterView<?> parent, View v, int position, long arg3) {
			drawer.closeDrawers();
			
			MenuData menu = (MenuData) parent.getItemAtPosition(position);
			if (menu != null) {
				if (menu.getPageType() == MenuData.FRAGMENT) {
					Fragment fragment = null;
					
					switch (menu.getPageName()) {
					case MY_PAGE:
						fragment = new ProfileRootFragment();
						((ProfileRootFragment)fragment).setUser(Auth.getUser());
						break;
						
					case WORLD_SONG:
						fragment = new CollaboratedFragment();
						break;
						
					case MUSIC_LIST:
						fragment = new MusicFragment();
						break;
						
					case WAITING_COLLABO:
						fragment = new WaitingFragment();
						break;
						
					case LEGEND:
						fragment = new LegendFragment();
						break;
						
					default:
						break;
						
					}
					
					replaceFragment(fragment);
					currentPosition = position;
				} else if (menu.getPageType() == MenuData.ACTIVITY) {
					Intent intent = new Intent();
					
					switch (menu.getPageName()) {
					case NOTIFICATION:
						intent.setClass(MainActivity.this, SimpleListActivity.class);
						intent.putExtra(SimpleListActivity.INTENT_LIST_TYPE, SimpleListType.NOTIFICATION);
						intent.putExtra(SimpleListActivity.INTENT_USER, Auth.getUserInJson());
						
						break;
						
					case FIND_USER:
						intent.setClass(MainActivity.this, SearchActivity.class);
						intent.putExtra(SearchActivity.INTENT_SEARCH_TYPE, SearchType.USER);
						break;
						
					case ARTIST:
						intent.setClass(MainActivity.this, ArtistActivity.class);
						break;
						
					case NOTICE:
						intent.setClass(MainActivity.this, NoticeActivity.class);
						break;
					
					case SETTING:
						intent.setClass(MainActivity.this, SettingActivity.class);
						break;
						
					default:
						break;
					}
					
					startActivity(intent);
				}
			}
		}
	};

	@Override
	public void onBackPressed() {
		if (pressedBackButton) {
			if (countThread != null && countThread.isAlive()) {
				countThread.interrupt();
				countThread = null;
			}
			if (toast != null) {
				toast.cancel();
			}
			
			finish();
		} else {
			pressedBackButton = true;
			
			if (toast != null) {
				toast.cancel();
			}
			
			toast = Toast.makeText(this, getResources().getString(R.string.ask_app_finish), Toast.LENGTH_SHORT);
			toast.show();
			countThread = null;
			initializeCountThread();
			countThread.start();
		}
	}
	
	private void initializeCountThread() {
		countThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					pressedBackButton = false;
				}
			}
		}, "count-thread");
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		requestCode = intent.getIntExtra(INTENT_PAGE_REQUEST, -1);
		performedMenuClick = false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (drawer.isDrawerOpen(lvNavigation)) {
			drawer.closeDrawers();
		} else {
			drawer.openDrawer(lvNavigation);
		}
		
		return false;
	}

	@Override
	protected void onResumeFragments() {
		super.onResumeFragments();
		
		if (Auth.isLoggedIn()) {
			if (!performedMenuClick) {
				int position = 0;
				
				if (App.REQUEST_MY_PROFILE_FRAGMENT == requestCode) {
					position = 0;
				} else if (App.REQUEST_NOTIFICATION_ACTIVITY == requestCode) {
					position = 1;
				} else {
					position = 2;
				}
				
				performClickGNB(position);
				performedMenuClick = true;
			}
		} else {
			finish();
		}
	}
	
	public void performClickGNB(int position) {
		if (lvNavigation != null && menuAdapter != null && menuAdapter.getCount() >= position) {
			lvNavigation.performItemClick(lvNavigation, position, position);
		}
	}
	
	public void updateNickname(String nickName) {
		try {
			menuDatas.get(0).setOutText(nickName);
			menuAdapter.notifyDataSetChanged();
			performClickGNB(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isMainActivityRunning() {
		return isRunning;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if (noticeDialog != null) {
			noticeDialog.dismiss();
			noticeDialog = null;
		}
		
		isRunning = false;
	}

	@Override
	protected int getChildLayoutResourceId() {
		return R.layout.activity_home;
	}

	@Override
	protected boolean isRootActivity() {
		return true;
	}

	@Override
	protected boolean enablePlayingThumb() {
		return true;
	}

} 
