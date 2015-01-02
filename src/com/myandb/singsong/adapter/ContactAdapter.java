package com.myandb.singsong.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.myandb.singsong.R;
import com.myandb.singsong.model.Contact;
import com.myandb.singsong.net.UrlBuilder;

public class ContactAdapter extends HolderAdapter<Contact, ContactAdapter.ContactHolder> {

	public ContactAdapter() {
		super(Contact.class);
	}

	@Override
	public ContactHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
		View view = inflater.inflate(R.layout.row_contact, parent, false);
		return new ContactHolder(view);
	}

	@Override
	public void onBindViewHolder(Context context, ContactHolder viewHolder, int position) {
		final Contact contact = getItem(position);
		
		viewHolder.tvContactName.setText(contact.getName());
		viewHolder.vInvite.setTag(contact);
		viewHolder.vInvite.setOnClickListener(inviteClickListener);
	}
	
	private OnClickListener inviteClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			final Contact contact = (Contact) v.getTag();
			if (contact != null) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				String body = "�뷡�� �θ��� ¦�� ã�� �뷡�� ���ø����̼�!\n";
				body += new UrlBuilder().s("w").s("invitation").toString();
				body += "\n���� �÷��� ������ �ٿ� �޾� ���� �뷡 �ҷ��� :)";
				intent.putExtra("sms_body", body);
				intent.putExtra("address", contact.getPhoneNumber());
				intent.setType("vnd.android-dir/mms-sms");
				v.getContext().startActivity(intent);
			}
		}
	};
	
	public static final class ContactHolder extends ViewHolder {
		
		public TextView tvContactName;
		public View vInvite;

		public ContactHolder(View view) {
			super(view);
			tvContactName = (TextView) view.findViewById(R.id.tv_contact_name);
			vInvite = view.findViewById(R.id.ll_invite);
		}
		
	}

}
