package com.myandb.singsong.dialog;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.myandb.singsong.R;
import com.myandb.singsong.fragment.BaseFragment;
import com.myandb.singsong.model.Profile;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.JSONErrorListener;
import com.myandb.singsong.net.JSONObjectRequest;
import com.myandb.singsong.net.JSONObjectSuccessListener;
import com.myandb.singsong.secure.Authenticator;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ChangeKakaoDialog extends BaseDialog {
	
	public static final String EXTRA_USER_KAKAO = "user_kakao";
	
	private String originalKakao;
	private EditText etUserKakao;
	private Button btnChangeKakao;

	@Override
	protected int getResourceId() {
		return R.layout.dialog_change_kakao;
	}

	@Override
	protected void onArgumentsReceived(Bundle bundle) {
		super.onArgumentsReceived(bundle);
		originalKakao = bundle.getString(EXTRA_USER_KAKAO);
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		etUserKakao = (EditText) view.findViewById(R.id.et_user_kakao);
		btnChangeKakao = (Button) view.findViewById(R.id.btn_change_kakao);
	}

	@Override
	protected void initialize(Activity activity) {
		setProgressDialogMessage("변경 중입니다.");
		originalKakao = originalKakao != null ? originalKakao : "";
	}

	@Override
	protected void setupViews() {
		etUserKakao.setText(originalKakao);
		btnChangeKakao.setOnClickListener(changeKakaoClickListener);
	}
	
	private OnClickListener changeKakaoClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			String newKakao = etUserKakao.getText().toString();
			if (hasKakaoChanged(newKakao)) {
				showProgressDialog();
				changeKakao(newKakao);
			}
		}
		
	};
	
	private boolean hasKakaoChanged(String kakao) {
		return !originalKakao.equals(kakao);
	}
	
	private void changeKakao(String kakao) {
		try {
			JSONObject message = new JSONObject();
			message.put("kakaotalk", kakao);
			
			JSONObjectRequest request = new JSONObjectRequest(
					Method.PUT, "profile", message,
					new JSONObjectSuccessListener(this, "onChangedSuccess", Profile.class),
					new JSONErrorListener(this, "onChangedError"));
			addRequest(request);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void onChangedSuccess(Profile profile) {
		User user = Authenticator.getUser();
		user.setProfile(profile);
		new Authenticator().update(user);
		if (getParentFragment() instanceof BaseFragment) {
			((BaseFragment) getParentFragment()).notifyDataChanged();
		}
		dismiss();
	}
	
	public void onChangedError() {
		makeToast("카카오톡 아이디 변경에 실패하였습니다.");
		dismissProgressDialog();
	}

}
