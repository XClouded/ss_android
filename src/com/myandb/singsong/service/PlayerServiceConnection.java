package com.myandb.singsong.service;

import java.lang.ref.WeakReference;

import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.service.PlayerService.PlayerBinder;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

public class PlayerServiceConnection implements ServiceConnection {
	
	private WeakReference<Activity> weakReference;
	private PlayerService service;
	public boolean bind;
	
	public PlayerServiceConnection(Activity context) {
		this.weakReference = new WeakReference<Activity>(context);
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder binder) {
        this.service = ((PlayerBinder) binder).getService();
        
        if (weakReference.get() instanceof BaseActivity) {
        	BaseActivity activity = (BaseActivity) weakReference.get();
        	if (activity != null && service != null) {
        		activity.onPlayerServiceConnected(service);
        		bind = true;
        	}
        }
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		weakReference.clear();
		bind = false;
	}

}
