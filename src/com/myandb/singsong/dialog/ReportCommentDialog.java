package com.myandb.singsong.dialog;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.myandb.singsong.R;
import com.myandb.singsong.net.JustRequest;
import com.myandb.singsong.secure.Authenticator;

public class ReportCommentDialog extends BaseDialog {
	
	public static final String EXTRA_COMMENT_ID = "comment_id";
	public static final String EXTRA_COMMENT_CONTENT = "comment_content";
	
	private Button btnReport;
	private TextView tvCommentContent;
	private EditText etReportContent;
	private int commentId;
	private String commentContent;
	
	@Override
	protected int getResourceId() {
		return R.layout.dialog_report_comment;
	}

	@Override
	protected void onArgumentsReceived(Bundle bundle) {
		super.onArgumentsReceived(bundle);
		commentId = bundle.getInt(EXTRA_COMMENT_ID);
		commentContent = bundle.getString(EXTRA_COMMENT_CONTENT);
	}

	@Override
	protected void initialize(Activity activity) {}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		btnReport = (Button) view.findViewById(R.id.btn_report);
		tvCommentContent = (TextView) view.findViewById(R.id.tv_comment_content);
		etReportContent = (EditText) view.findViewById(R.id.et_report_content);
	}

	@Override
	protected void setupViews() {
		btnReport.setOnClickListener(reportClickListener);
		tvCommentContent.setText(commentContent);
	}
	
	private View.OnClickListener reportClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			String reportContent = etReportContent.getText().toString();
			
			if (reportContent.length() >= 10) {
				try {
					JSONObject message = new JSONObject();
					message.put("user_id", Authenticator.getUser().getId());
					message.put("comment_id", commentId);
					message.put("message", reportContent);
					
					JustRequest request = new JustRequest("reports", null, message);
					addRequest(request);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				dismiss();
				makeToast(R.string.t_notify_report_accepted);
			} else {
				makeToast(R.string.t_alert_report_reason_validation_failed);
			}
		}
	};

}
