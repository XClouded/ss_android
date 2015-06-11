package com.myandb.singsong.util;

import org.json.JSONException;
import org.json.JSONObject;

import com.myandb.singsong.App;
import com.myandb.singsong.model.Model;
import com.myandb.singsong.model.Music;
import com.myandb.singsong.model.Song;
import com.myandb.singsong.net.JustRequest;
import com.myandb.singsong.net.MelonHttpScheme;
import com.myandb.singsong.secure.Authenticator;

import android.content.Context;

public class PlayCounter {
	
	private static Model savedEntity;
	private static int second = 0;
	
	public static void countAsync(Context context, Model entity) {
		if (savedEntity != null) {
			if (savedEntity.equals(entity)) {
				second++;
			} else {
				second = 0;
			}
		}
		
		savedEntity = entity;
		
		if (second == 0 || second == 60) {
			String segment = new StringBuilder()
			.append(getEntityName(savedEntity))
			.append("/")
			.append(savedEntity.getId())
			.append("/logs").toString();
			
			try {
				Music music = null;
				if (savedEntity instanceof Song) {
					music = ((Song) savedEntity).getMusic();
				} else {
					music = (Music) savedEntity;
				}
				
				JSONObject message = new JSONObject();
				message.put("MCONTSID", music.getMelonContentsId());
				message.put("MRID", music.getId());
				message.put("USERMKEY", Authenticator.getUser().getMelonId());
				message.put("CONTSTYPE", "3C0007");
				message.put("POCID", MelonHttpScheme.POC_CODE);
				message.put("DLYN", "N");
				message.put("TIME", System.currentTimeMillis());
				message.put("TOKEN", Authenticator.getAccessToken());
				message.put("USERID", Authenticator.getUser().getMelonUsername());
				message.put("DEVICEID", Authenticator.getDeviceUuid());
				
				if (savedEntity instanceof Song) {
					message.put("USERCONTSID", savedEntity.getId());
				}
				
				JustRequest request = new JustRequest(segment, null, message);
				((App) context.getApplicationContext()).addShortLivedRequest(context, request);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static String getEntityName(Model entity) {
		String entityName = "";
		if (entity instanceof Song) {
			entityName = "songs";
		} else if (entity instanceof Music) {
			entityName = "musics";
		} else {
			entityName = "songs";
		}
		return entityName;
	}
	
}
