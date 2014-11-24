package com.myandb.singsong.dialog;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.myandb.singsong.R;
import com.myandb.singsong.activity.PlayerActivity;
import com.myandb.singsong.image.ImageHelper;
import com.myandb.singsong.model.User;

public class KakaotalkDialog extends BaseDialog {
	
	private String baseMessage;
	private ImageView ivCancel;
	private ImageView ivWriterPhoto;
	private TextView tvBaseMessage;
	private EditText etOptionalMessage;
	private Button btnSubmit;
	private User user;
	private PlayerActivity parent;

	public KakaotalkDialog(Context context, User user) {
		super(context, android.R.style.Theme_Translucent_NoTitleBar);
		
		parent = (PlayerActivity) context;
		this.user = user;
	}
	
	public void setBaseMessage(String msg) {
		baseMessage = msg;
	}

	@Override
	protected void initializeView() {
		setContentView(R.layout.dialog_kakaotalk);
		
		ivCancel = (ImageView)findViewById(R.id.iv_cancel);
		ivWriterPhoto = (ImageView)findViewById(R.id.iv_writer_photo);
		etOptionalMessage = (EditText)findViewById(R.id.et_optional_message);
		tvBaseMessage = (TextView)findViewById(R.id.tv_base_message);
		btnSubmit = (Button)findViewById(R.id.btn_submit);
	}

	@Override
	protected void setupView() {
		if (user != null) {
			ImageHelper.displayPhoto(user, ivWriterPhoto);
			btnSubmit.setOnClickListener(submitClickListener);
			tvBaseMessage.setText(baseMessage);
		}
		
		ivCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				KakaotalkDialog.this.dismiss();
			}
		});
	}
	
	@Override
	public void show() {
		super.show();
		
		if (baseMessage != null && tvBaseMessage != null) {
			tvBaseMessage.setText(baseMessage);
		}
	}

	@Override
	public void dismiss() {
		super.dismiss();
		
		if (etOptionalMessage != null) {
			etOptionalMessage.setText("");
			InputMethodManager imm = (InputMethodManager) parent.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(etOptionalMessage.getWindowToken(), 0);
		}
	}
	
	private View.OnClickListener submitClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			String message = "[콜라보 노래방]\n\n";
			String optionalMessage = etOptionalMessage.getText().toString();
			if (optionalMessage != null && !"".equals(optionalMessage)) {
				message += optionalMessage;
				message += "\n\n";
			}
			message += baseMessage;
			
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("text/plain"); 
			intent.putExtra(Intent.EXTRA_TEXT, message);
			intent.setPackage("com.kakao.talk");
			
			parent.startActivity(intent);
			
			KakaotalkDialog.this.dismiss();
		}
	};

}
