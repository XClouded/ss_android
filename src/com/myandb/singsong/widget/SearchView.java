package com.myandb.singsong.widget;

import com.myandb.singsong.R;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class SearchView extends RelativeLayout {
	
	private EditText search;
	private ImageView clear;
	private OnTextChangedListener changedListener;
	private OnTextEmptyListener emptyListener;
	private OnActionSearchListener searchListener;

	public SearchView(Context context) {
		super(context);
	}
	
	public SearchView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SearchView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		initializeChildViews();
	}
	
	private void initializeChildViews() {
		search = (EditText) findViewById(R.id.et_search);
		clear = (ImageView) findViewById(R.id.iv_clear);
		search.addTextChangedListener(textChangedWatcher);
		search.setOnEditorActionListener(editorActionListener);
		search.setMaxLines(1);
		clear.setOnClickListener(clearClickListener);
	}
	
	private OnEditorActionListener editorActionListener = new OnEditorActionListener() {
		
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			switch (actionId) {
			case EditorInfo.IME_ACTION_SEARCH:
				if (searchListener != null) {
					searchListener.onSearch(search.getText().toString());
				}
				return true;

			default:
				return false;
			}
		}
	};
	
	public void showSoftKeyboard() {
		search.requestFocus();
		InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(search, 0);
	}
	
	public void hideSoftKeyboard() {
		InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE); 
		imm.hideSoftInputFromWindow(search.getWindowToken(), 0);
	}
	
	private TextWatcher textChangedWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if (s != null) {
				if (s.length() > 0) {
					dispatchOnTextChanged(s);
				} else {
					dispatchOnTextEmpty();
				}
			}
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
		
		@Override
		public void afterTextChanged(Editable s) {}
	};
	
	private OnClickListener clearClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (search != null) {
				search.setText("");
			}
		}
	};
	
	private void dispatchOnTextChanged(CharSequence text) {
		if (clear != null) {
			clear.setVisibility(View.VISIBLE);
		}
		
		if (changedListener != null) {
			changedListener.onChanged(text);
		}
	}
	
	private void dispatchOnTextEmpty() {
		if (clear != null) {
			clear.setVisibility(View.GONE);
		}
		
		if (emptyListener != null) {
			emptyListener.onEmpty();
		}
	}

	public void setSearchHint(String hint) {
		if (search != null) {
			search.setHint(hint);
		}
	}
	
	public void setOnTextChangedListener(OnTextChangedListener listener) {
		this.changedListener = listener;
	}
	
	public void setOnTextEmptyListener(OnTextEmptyListener listener) {
		this.emptyListener = listener;
	}
	
	public void setOnActionSearchListener(OnActionSearchListener listener) {
		this.searchListener = listener;
	}
	
	public static interface OnTextChangedListener {
		
		public void onChanged(CharSequence text);
		
	}
	
	public static interface OnTextEmptyListener {
		
		public void onEmpty();
		
	}
	
	public static interface OnActionSearchListener {
		
		public void onSearch(CharSequence text);
		
	}

}
