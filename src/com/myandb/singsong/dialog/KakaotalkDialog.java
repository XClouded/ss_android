package com.myandb.singsong.dialog;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.myandb.singsong.R;
import com.myandb.singsong.image.ImageHelper;
import com.myandb.singsong.model.User;
import com.myandb.singsong.secure.Authenticator;

public class KakaotalkDialog extends BaseDialog {
	
	private String baseMessage;
	private ImageView ivWriterPhoto;
	private TextView tvBaseMessage;
	private EditText etOptionalMessage;
	private Button btnSubmit;
	private User user;

	public KakaotalkDialog(Context context) {
		super(context, android.R.style.Theme_Translucent_NoTitleBar);
	}

	@Override
	protected void initialize() {
		this.user = Authenticator.getUser();
	}

	@Override
	protected int getResourceId() {
		return R.layout.dialog_kakaotalk;
	}

	@Override
	protected void onViewInflated() {
		ivWriterPhoto = (ImageView)findViewById(R.id.iv_writer_photo);
		etOptionalMessage = (EditText)findViewById(R.id.et_optional_message);
		tvBaseMessage = (TextView)findViewById(R.id.tv_base_message);
		btnSubmit = (Button)findViewById(R.id.btn_submit);
	}

	@Override
	protected void setupViews() {
		if (user != null) {
			ImageHelper.displayPhoto(user, ivWriterPhoto);
			btnSubmit.setOnClickListener(submitClickListener);
		}
	}
	
	public void setBaseMessage(String message) {
		baseMessage = message;
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
//			InputMethodManager imm = (InputMethodManager) parent.getSystemService(Context.INPUT_METHOD_SERVICE);
//			imm.hideSoftInputFromWindow(etOptionalMessage.getWindowToken(), 0);
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
			
//			parent.startActivity(intent);
			
			KakaotalkDialog.this.dismiss();
		}
	};

}
