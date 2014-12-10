package com.myandb.singsong.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.myandb.singsong.R;
import com.myandb.singsong.model.Contact;

public class ContactAdapter extends HolderAdapter<Contact, ContactAdapter.ContactHolder> {

	public ContactAdapter() {
		super(Contact.class);
	}

	@Override
	public ContactHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = View.inflate(parent.getContext(), R.layout.row_contact, null);
		return new ContactHolder(view);
	}

	@Override
	public void onBindViewHolder(Context context, ContactHolder viewHolder, int position) {
		final Contact contact = getItem(position);
		
		viewHolder.tvContactName.setText(contact.getName());
		viewHolder.btnInvite.setTag(contact);
		viewHolder.btnInvite.setOnClickListener(inviteClickListener);
	}
	
	private OnClickListener inviteClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			final Contact contact = (Contact) v.getTag();
			if (contact != null) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				String body = "같이 즐겨요! 콜라보 노래방";
				intent.putExtra("sms_body", body);
				intent.putExtra("address", contact.getPhoneNumber());
				intent.setType("vnd.android-dir/mms-sms");
				v.getContext().startActivity(intent);
			}
		}
	};
	
	public static final class ContactHolder extends ViewHolder {
		
		public TextView tvContactName;
		public Button btnInvite;

		public ContactHolder(View view) {
			super(view);
			tvContactName = (TextView) view.findViewById(R.id.tv_contact_name);
			btnInvite = (Button) view.findViewById(R.id.btn_invite);
		}
		
	}

}
