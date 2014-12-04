package com.myandb.singsong.dialog;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.myandb.singsong.R;
import com.myandb.singsong.image.ImageHelper;
import com.myandb.singsong.model.Comment;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.JustRequest;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.secure.Authenticator;

public class ReportCommentDialog extends BaseDialog {
	
	private Comment<?> comment;
	private ImageView ivReporterPhoto;
	private Button btnSubmitReport;
	private TextView tvTargetCommentContent;
	private EditText etReportContent;
	private User user;

	@Override
	protected void initialize(Activity activity) {
		this.user = Authenticator.getUser();
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		ivReporterPhoto = (ImageView) view.findViewById(R.id.iv_reporter_photo);
		btnSubmitReport = (Button) view.findViewById(R.id.btn_submit);
		tvTargetCommentContent = (TextView) view.findViewById(R.id.tv_target_comment_content);
		etReportContent = (EditText) view.findViewById(R.id.et_report_content);
	}

	@Override
	protected int getResourceId() {
		return R.layout.dialog_report_comment;
	}

	@Override
	protected void setupViews() {
		if (user != null) {
			ImageHelper.displayPhoto(user, ivReporterPhoto);
		}
		btnSubmitReport.setOnClickListener(reportClickListener);
	}
	
	private View.OnClickListener reportClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			String reportContent = etReportContent.getText().toString();
			
			if (reportContent != null && reportContent.length() > 10) {
				try {
					JSONObject message = new JSONObject();
					message.put("user_id", user.getId());
					message.put("comment_id", comment.getId());
					message.put("message", reportContent);
					
					UrlBuilder urlBuilder = new UrlBuilder();
					String url = urlBuilder.s("reports").toString();
					JustRequest request = new JustRequest(url, message);
					addRequest(request);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				dismiss();
				makeToast(R.string.t_report_has_accepted);
			} else {
				makeToast(R.string.t_report_length_policy);
			}
		}
	};

}
