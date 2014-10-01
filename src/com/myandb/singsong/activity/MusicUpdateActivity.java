package com.myandb.singsong.activity;

import java.util.Calendar;

import com.myandb.singsong.R;
import com.myandb.singsong.adapter.MusicSquareAdapter;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.util.TimeHelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

public class MusicUpdateActivity extends Activity {
	
	private ListView lvUpdate;
	private Button btnStart;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update);
		
		lvUpdate = (ListView) findViewById(R.id.lv_full_width);
		btnStart = (Button) findViewById(R.id.btn_start);
		
		final String startDate = TimeHelper.getDateString(Calendar.DATE, -1);
		UrlBuilder urlBuilder = UrlBuilder.create();
		urlBuilder.l("musics").start(startDate).q("order", "created_at");
		
		MusicSquareAdapter adapter = new MusicSquareAdapter(this, urlBuilder);
		lvUpdate.setAdapter(adapter);
		
		btnStart.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MusicUpdateActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}

}
