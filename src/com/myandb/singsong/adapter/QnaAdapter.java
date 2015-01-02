package com.myandb.singsong.adapter;

import com.myandb.singsong.R;
import com.myandb.singsong.image.ImageHelper;
import com.myandb.singsong.model.Artist.Qna;
import com.myandb.singsong.model.User;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class QnaAdapter extends HolderAdapter<Qna, QnaAdapter.QnaHolder> {
	
	public QnaAdapter() {
		super(Qna.class);
	}

	@Override
	public QnaHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
		View view = inflater.inflate(R.layout.row_qna, parent, false);
		return new QnaHolder(view);
	}

	@Override
	public void onBindViewHolder(Context context, QnaHolder viewHolder, int position) {
		final Qna qna = getItem(position);
		final User user = qna.getUser();
		
		viewHolder.tvQuestion.setText("Q. " + qna.getQuestion());
		viewHolder.tvAnswer.setText("A. " + qna.getAnswer());
		viewHolder.ivUserPhoto.setOnClickListener(user.getProfileClickListener());

		ImageHelper.displayPhoto(user, viewHolder.ivUserPhoto);
		
	}

	public static final class QnaHolder extends ViewHolder {
		
		public TextView tvQuestion;
		public TextView tvAnswer;
		public ImageView ivUserPhoto;

		public QnaHolder(View view) {
			super(view);
			
			tvQuestion = (TextView) view.findViewById(R.id.tv_question);
			tvAnswer = (TextView) view.findViewById(R.id.tv_answer);
			ivUserPhoto = (ImageView) view.findViewById(R.id.iv_user_photo);
		}
		
	}

}
