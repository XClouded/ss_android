package com.myandb.singsong.fragment;

import java.util.ArrayList;
import java.util.List;

import com.myandb.singsong.R;
import com.myandb.singsong.adapter.ContactAdapter;
import com.myandb.singsong.model.Contact;
import com.myandb.singsong.widget.SearchView;
import com.myandb.singsong.widget.SearchView.OnTextChangedListener;
import com.myandb.singsong.widget.SearchView.OnTextEmptyListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.view.LayoutInflater;
import android.view.View;

public class InviteContactFragment extends ListFragment {
	
	private ContactAdapter adapter;
	private SearchView searchView;

	@SuppressLint("InflateParams")
	@Override
	protected View inflateFixedHeaderView(LayoutInflater inflater) {
		View view = inflater.inflate(R.layout.search_view, null);
		if (view instanceof SearchView) {
			searchView = (SearchView) view;
		}
		return view;
	}

	@Override
	protected void initialize(Activity activity) {
		super.initialize(activity);
		adapter = new ContactAdapter();
		setAdapter(adapter);
	}

	@Override
	protected void setupViews() {
		super.setupViews();
		searchView.setSearchHint(getString(R.string.hint_search_user));
		searchView.setOnTextChangedListener(textChangedListener);
		searchView.setOnTextEmptyListener(textEmptyListner);
	}
	
	private OnTextChangedListener textChangedListener = new OnTextChangedListener() {

		@Override
		public void onChanged(CharSequence text) {
			updateAdapter(getContactsFromPhone(text.toString()));
		}
	};
	
	private OnTextEmptyListener textEmptyListner = new OnTextEmptyListener() {
		
		@Override
		public void onEmpty() {
			notifyDataChanged();
		}
	};

	@Override
	protected void onDataChanged() {
		updateAdapter(getContactsFromPhone());
	}
	
	private void updateAdapter(List<Contact> contacts) {
		adapter.clear();
		adapter.addAll(contacts);
		setListShown(true);
	}
	
	private List<Contact> getContactsFromPhone() {
		return getContactsFromPhone(null);
	}
	
	private List<Contact> getContactsFromPhone(String searchName) {
		List<Contact> contacts = new ArrayList<Contact>();
		
		Cursor cursor = null;
		try {
			cursor = getContactCursor(searchName);
			if (cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					String name = getContactDisplayName(cursor);
					if (hasContactPhoneNumber(cursor)) {
						String phoneNumber = getContactPhoneNumber(cursor);
						contacts.add(new Contact(name, phoneNumber));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return contacts;
	}
	
	private ContentResolver getContentResolver() {
		return getActivity().getContentResolver();
	}
	
	private Cursor getContactCursor(String name) {
		ContentResolver resolver = getContentResolver();
		String selection = null;
		String[] selectionArgs = null;
		if (name != null && name.length() > 0) {
			selection = Phone.DISPLAY_NAME + " like ?";
			selectionArgs = new String[] { "%" + name + "%" };
		}
		return resolver.query(
				Phone.CONTENT_URI,
				new String[] {Phone.DISPLAY_NAME, Phone.NUMBER, Phone.HAS_PHONE_NUMBER},
				selection,
				selectionArgs,
				Phone.DISPLAY_NAME);
	}
	
	private String getContactDisplayName(Cursor cursor) {
		int displayNameIndex = cursor.getColumnIndex(Phone.DISPLAY_NAME);
		return cursor.getString(displayNameIndex);
	}
	
	private String getContactPhoneNumber(Cursor cursor) {
		int phoneIndex = cursor.getColumnIndex(Phone.NUMBER);
		return cursor.getString(phoneIndex);
	}
	
	private boolean hasContactPhoneNumber(Cursor cursor) {
		int hasPhoneNumberIndex = cursor.getColumnIndex(Phone.HAS_PHONE_NUMBER);
		String hasPhoneNumber = cursor.getString(hasPhoneNumberIndex);
		try {
			return Integer.parseInt(hasPhoneNumber) > 0;
		} catch (NumberFormatException e) {
			return false;
		}
	}

}
