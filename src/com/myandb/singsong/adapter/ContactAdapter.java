package com.myandb.singsong.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;
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
	public void onBindViewHolder(Context context, ContactHolder viewHolder, Contact contact, int position) {
		viewHolder.tvContactName.setText(contact.getName());
		viewHolder.vInvite.setTag(contact);
		viewHolder.vInvite.setOnClickListener(inviteClickListener);
	}
	
	private OnClickListener inviteClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			final Contact contact = (Contact) v.getTag();
			if (contact != null) {
				String phoneNumber = contact.getPhoneNumber();
				String message = getMessage(v.getContext());
				Intent intent = null;
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
					intent = getSmsIntentPostKitkat(v.getContext(), phoneNumber, message);
				} else {
					intent = getSmsIntentPreKitkat(phoneNumber, message);
				}
				
				try {
					v.getContext().startActivity(intent);
				} catch (Exception e) {
					e.printStackTrace();
					// This will not happened;
				}
			}
		}
		
		private String getMessage(Context context) {
			String message = context.getString(R.string.invitation_message_header);
			message += "\n" + new UrlBuilder().s("w").s("invitation").toString() + "\n";
			message += context.getString(R.string.invitation_message_footer);
			return message;
		}
		
		private Intent getSmsIntentPostKitkat(Context context, String phoneNumber, String message) {
		    Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + Uri.encode(phoneNumber)));
		    intent.putExtra("sms_body", message);

		    String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(context);
		    if (defaultSmsPackageName != null) {
		        intent.setPackage(defaultSmsPackageName);
		    }
		    return intent;
		}
		
		private Intent getSmsIntentPreKitkat(String phoneNumbner, String message) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setType("vnd.android-dir/mms-sms");
			intent.putExtra("sms_body", message);
			intent.putExtra("address", message);
			return intent;
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
