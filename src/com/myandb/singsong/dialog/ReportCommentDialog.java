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
import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.model.Comment;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.OAuthJustRequest;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.util.ImageHelper;

public class ReportCommentDialog extends BaseDiaglog {
	
	private User user;
	private Comment<?> comment;
	private ImageView ivCancel;
	private ImageView ivReporterPhoto;
	private Button btnSubmitReport;
	private TextView tvTargetCommentContent;
	private EditText etReportContent;
	private BaseActivity parent;

	public ReportCommentDialog(Context context, User user) {
		super(context, android.R.style.Theme_Translucent_NoTitleBar);
		
		this.user = user;
		this.parent = (BaseActivity) context;
	}

	@Override
	protected void initializeView() {
		setContentView(R.layout.dialog_report_comment);
		
		ivCancel = (ImageView) findViewById(R.id.iv_cancel);
		ivReporterPhoto = (ImageView) findViewById(R.id.iv_reporter_photo);
		btnSubmitReport = (Button) findViewById(R.id.btn_submit);
		tvTargetCommentContent = (TextView) findViewById(R.id.tv_target_comment_content);
		etReportContent = (EditText) findViewById(R.id.et_report_content);
	}

	@Override
	protected void setupView() {
		if (user != null) {
			ImageHelper.displayPhoto(user, ivReporterPhoto);
		}
		
		ivCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ReportCommentDialog.this.dismiss();
			}
		});
	}
	
	@Override
	public void show() {
		super.show();
		
		if (comment != null) {
			tvTargetCommentContent.setText(comment.getContent());
			btnSubmitReport.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String reportContent = etReportContent.getText().toString();
					
					if (reportContent != null && reportContent.length() > 10) {
						try {
							JSONObject message = new JSONObject();
							message.put("user_id", user.getId());
							message.put("comment_id", comment.getId());
							message.put("message", reportContent);
							
							UrlBuilder urlBuilder = UrlBuilder.getInstance();
							String url = urlBuilder.l("reports").build();
							OAuthJustRequest request = new OAuthJustRequest(url, message);
							RequestQueue queue = ((App) getContext().getApplicationContext()).getQueueInstance();
							queue.add(request);
						} catch (JSONException e) {
							e.printStackTrace();
						}
						
						ReportCommentDialog.this.dismiss();
						
						Toast.makeText(getContext(), "신고가 접수되었습니다. :)", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(getContext(), "신고사유를 10자 이상 써주세요.", Toast.LENGTH_SHORT).show();
					}
				}
			});
		}
	}
	
	@Override
	public void dismiss() {
		super.dismiss();
		
		parent.closeEditText(etReportContent);
	}

	public void setComment(Comment<?> comment) {
		this.comment = comment;
	}

}
