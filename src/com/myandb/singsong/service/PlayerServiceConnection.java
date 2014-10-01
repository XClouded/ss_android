package com.myandb.singsong.service;

import java.lang.ref.WeakReference;

import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.service.PlayerService.PlayerBinder;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

public class PlayerServiceConnection implements ServiceConnection {
	
	private WeakReference<BaseActivity> weakReference;
	private PlayerService service;
	public boolean bind;
	
	public PlayerServiceConnection(BaseActivity context) {
		this.weakReference = new WeakReference<BaseActivity>(context);
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder binder) {
        this.service = ((PlayerBinder) binder).getService();
        
        BaseActivity activity = weakReference.get();
        if (activity != null && service != null) {
        	activity.onPlayerConnected(service);
        	bind = true;
        }
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		weakReference.clear();
		bind = false;
	}

}
