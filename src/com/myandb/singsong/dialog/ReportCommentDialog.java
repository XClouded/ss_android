package com.myandb.singsong.dialog;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.myandb.singsong.App;
import com.myandb.singsong.R;
import com.myandb.singsong.image.ImageHelper;
import com.myandb.singsong.model.Comment;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.OAuthJustRequest;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.secure.Authenticator;

public class ReportCommentDialog extends BaseDialog {
	
	private Comment<?> comment;
	private ImageView ivReporterPhoto;
	private Button btnSubmitReport;
	private TextView tvTargetCommentContent;
	private EditText etReportContent;
	private User user;

	public ReportCommentDialog(Context context) {
		super(context);
	}

	@Override
	protected void initialize() {
		this.user = Authenticator.getUser();
	}

	@Override
	protected int getResourceId() {
		return R.layout.dialog_report_comment;
	}

	@Override
	protected void onViewInflated() {
		ivReporterPhoto = (ImageView) findViewById(R.id.iv_reporter_photo);
		btnSubmitReport = (Button) findViewById(R.id.btn_submit);
		tvTargetCommentContent = (TextView) findViewById(R.id.tv_target_comment_content);
		etReportContent = (EditText) findViewById(R.id.et_report_content);
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
					OAuthJustRequest request = new OAuthJustRequest(url, message);
					RequestQueue queue = ((App) getContext().getApplicationContext()).getQueueInstance();
					queue.add(request);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				dismiss();
				Toast.makeText(getContext(), getContext().getString(R.string.t_report_has_accepted), Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getContext(), getContext().getString(R.string.t_report_length_policy), Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	@Override
	public void show() {
		super.show();
		
		if (comment != null) {
			tvTargetCommentContent.setText(comment.getContent());
		}
	}
	
	@Override
	public void dismiss() {
		super.dismiss();
//		parent.closeEditText(etReportContent);
	}

	public void setComment(Comment<?> comment) {
		this.comment = comment;
	}

}
