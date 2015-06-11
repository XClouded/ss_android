package com.myandb.singsong.fragment;

import org.json.JSONArray;

import com.myandb.singsong.R;
import com.myandb.singsong.adapter.HolderAdapter;
import com.myandb.singsong.net.GradualLoader;
import com.myandb.singsong.net.GradualLoader.OnLoadCompleteListener;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.widget.FadingActionBarHelper;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ListFragment extends BaseFragment {
	
	public static final String EXTRA_URL_SEGMENT = "url_segment";
	public static final String EXTRA_QUERY_PARAMS = "query_params";
	public static final String EXTRA_ADAPTER_NAME = "adapter_name";
	public static final String EXTRA_HORIZONTAL_PADDING = "is_padding";
	public static final String EXTRA_VERTICAL_PADDING = "is_divider";
	public static final String EXTRA_COLUMN_NUM = "column_num";
	
	private AbsListView absListView;
	private ViewGroup fixedHeaderContainer;
	private ViewGroup listContainer;
	private View listHeaderView;
	private View fixedHeaderView;
	private View progressContainer;
	private Animation fadeIn;
	private Animation fadeOut;
	private GradualLoader loader;
	private FadingActionBarHelper fadingActionBarHelper;
	private UrlBuilder urlBuilder;
	private ListAdapter adapter;
	private OnEmptyListener emptyListener;
	private int listViewIndex;
	private int listViewTop;
	private int columnNum = 1;
	private boolean enableFadingActionBar = false;
	protected boolean horizontalPadding = false;
	protected boolean verticalPadding = false;

	@Override
	protected final int getResourceId() {
		return R.layout.fragment_list;
	}

	@Override
	protected void onArgumentsReceived(Bundle bundle) {
		super.onArgumentsReceived(bundle);
		
		if (isFragmentCreated()) {
			urlBuilder = extractUrlBuilderFromBundle(bundle);
			adapter = extractAdapterFromBundle(bundle);
		}
		
		columnNum = bundle.getInt(EXTRA_COLUMN_NUM, 1);
		horizontalPadding = bundle.getBoolean(EXTRA_HORIZONTAL_PADDING, false);
		verticalPadding = bundle.getBoolean(EXTRA_VERTICAL_PADDING, false);
	}
	
	private boolean isFragmentCreated() {
		return urlBuilder == null && adapter == null;
	}
	
	private UrlBuilder extractUrlBuilderFromBundle(Bundle bundle) {
		final String segment = bundle.getString(EXTRA_URL_SEGMENT);
		final Bundle params = bundle.getBundle(EXTRA_QUERY_PARAMS);
		
		if (segment != null) {
			UrlBuilder builder = new UrlBuilder();
			builder.s(segment);
			if (params != null) {
				builder.p(params);
			}
			return builder;
		}
		return null;
	}
	
	private ListAdapter extractAdapterFromBundle(Bundle bundle) {
		final String adapterName = bundle.getString(EXTRA_ADAPTER_NAME);
		if (adapterName != null) {
			return instantiateAdapterFromName(adapterName);
		}
		return null;
	}
	
	private ListAdapter instantiateAdapterFromName(String adapterName) {
		if (adapterName != null) {
			try {
				Class<?> classForAdapter = Class.forName(adapterName);
				return (ListAdapter) classForAdapter.newInstance();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (java.lang.InstantiationException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		fixedHeaderContainer = (ViewGroup) view.findViewById(R.id.fl_fixed_header_container);
		listContainer = (ViewGroup) view.findViewById(R.id.fl_list_container);
		progressContainer = view.findViewById(R.id.fl_progress_container);
		absListView = makeAbsListView(inflater, columnNum);
		listContainer.addView(absListView);
		addHeaderView(inflater, absListView);
		addFixedHeaderView(inflater, fixedHeaderContainer);
		addFooterView(inflater, absListView);
	}
	
	private AbsListView makeAbsListView(LayoutInflater inflater, int columnNum) {
		int layout = columnNum == 1 ? R.layout.listview : R.layout.gridview;
		AbsListView absListView = (AbsListView) inflater.inflate(layout, listContainer, false);
		if (columnNum > 1) {
			((GridView) absListView).setNumColumns(columnNum);
		}
		return absListView;
	}
	
	private void addHeaderView(LayoutInflater inflater, AbsListView listView) {
		if (listView instanceof ListView && getListHeaderViewResId() != View.NO_ID) {
			listHeaderView = inflater.inflate(getListHeaderViewResId(), listView, false);
			((ListView) listView).addHeaderView(listHeaderView);
			enableFadingActionBar = true;
		}
	}
	
	private void addFixedHeaderView(LayoutInflater inflater, ViewGroup container) {
		if (getFixedHeaderViewResId() != View.NO_ID) {
			container.setVisibility(View.VISIBLE);
			fixedHeaderView = inflater.inflate(getFixedHeaderViewResId(), container, false);
			container.addView(fixedHeaderView);
			enableFadingActionBar = false;
		} else {
			container.setVisibility(View.GONE);
		}
	}
	
	private void addFooterView(LayoutInflater inflater, AbsListView listView) {
		if (listView instanceof ListView) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				View footer = inflater.inflate(R.layout.footer, listView, false);
				((ListView) listView).addFooterView(footer);
			}
		}
	}

	@Override
	protected void initialize(Activity activity) {
		fadeIn = AnimationUtils.loadAnimation(activity, android.R.anim.fade_in);
		fadeOut = AnimationUtils.loadAnimation(activity, android.R.anim.fade_out);
		
		if (adapter == null) {
			adapter = instantiateAdapter(activity);
		}
		
		if (urlBuilder == null) {
			urlBuilder = instantiateUrlBuilder(activity);
		}
		
		if (loader == null) {
			loader = new GradualLoader(activity);
			setUrlBuilder(urlBuilder);
		}
	}
	
	protected ListAdapter instantiateAdapter(Activity activity) {
		return null;
	}
	
	protected UrlBuilder instantiateUrlBuilder(Activity activity) {
		return null;
	}
	
	@Override
	protected void setupViews(Bundle savedInstanceState) {
		if (absListView instanceof GridView) {
			((GridView) absListView).setAdapter(adapter);
		} else if (absListView instanceof ListView) {
			((ListView) absListView).setAdapter(adapter);
		}
		absListView.setOnScrollListener(onScrollListener);
		setListViewHorizontalPadding(absListView);
		setListViewVerticalPadding(absListView);
	}
	
	private void setListViewHorizontalPadding(AbsListView listView) {
		int padding = getResources().getDimensionPixelSize(R.dimen.margin);
		if (listView instanceof GridView || horizontalPadding) {
			listView.setPadding(padding, 0, padding, 0);
			listView.setVerticalScrollBarEnabled(false);
		}
	}
	
	private void setListViewVerticalPadding(AbsListView listView) {
		int padding = getResources().getDimensionPixelSize(R.dimen.margin);
		if (listView instanceof GridView) {
			listView.setPadding(listView.getPaddingLeft(), padding, listView.getPaddingRight(), padding);
		}
		if (listView instanceof ListView && verticalPadding) {
			if (listHeaderView == null && fixedHeaderView == null) {
				listView.setPadding(listView.getPaddingLeft(), padding, listView.getPaddingRight(), padding);
			}
			int height = padding;
			((ListView) listView).setDivider(null);
			((ListView) listView).setDividerHeight(height);
		}
	}
	
	private OnScrollListener onScrollListener = new OnScrollListener() {
		
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {}
		
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			if (loader != null) {
				loader.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
			}
			
			if (fadingActionBarHelper != null) {
				fadingActionBarHelper.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
			}
		}
	}; 

	@Override
	protected void onDataChanged() {}
	
	@Override
	public void onResume() {
		super.onResume();
		
		if (isFragmentStateRestore()) {
			restoreListViewState();
		} else {
			load();
		}
		
		if (enableFadingActionBar && listHeaderView != null) {
			setActionBarOverlay(true);
			listHeaderView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
			fadingActionBarHelper = new FadingActionBarHelper();
			fadingActionBarHelper.setBackground(R.drawable.actionbar_background)
			.setFullyVisiblePosition(listHeaderView.getMeasuredHeight())
			.initialize(getActivity());
		}
	}
	
	private boolean isFragmentStateRestore() {
		return adapter.getCount() > 0 || (adapter.getCount() == 0 && loader.isNothingToLoad());
	}
	
	private void restoreListViewState() {
		dispatchEmptyListener();
		restoreListViewPosition();
	}
	
	private void dispatchEmptyListener() {
		if (adapter.getCount() == 0 && emptyListener != null) {
			emptyListener.onEmpty();
		}
	}
	
	private void restoreListViewPosition() {
		if (absListView instanceof ListView && listViewIndex > 0) {
			((ListView) absListView).setSelectionFromTop(listViewIndex, listViewTop);
		}
		setListShown(true, false);
	}

	@Override
	public void onDestroyView() {
		saveCurrentPosition();
		super.onDestroyView();
	}
	
	private void saveCurrentPosition() {
		listViewIndex = absListView.getFirstVisiblePosition();
		View child = absListView.getChildAt(0);
		listViewTop = (child == null) ? 0 : child.getTop();
	}

	public void setFixedHeaderShown(boolean shown) {
		if (fixedHeaderContainer.isShown() == shown) {
			return;
		}
		
		if (shown) {
			fixedHeaderContainer.setVisibility(View.VISIBLE);
		} else {
			fixedHeaderContainer.setVisibility(View.GONE);
		}
	}
	
	public ListAdapter getAdapter() {
		return adapter;
	}
	
	public AbsListView getListView() {
		return absListView;
	}
	
	public void setUrlBuilder(UrlBuilder urlBuilder) {
		if (urlBuilder == null) {
			return;
		}
		
		setListShown(false);
		loader.setUrlBuilder(urlBuilder);
		loader.setOnLoadCompleteListener(loadCompleteListener);
		if (absListView != null) {
			absListView.smoothScrollToPosition(0);
		}
		if (adapter != null) {
			if (adapter instanceof HolderAdapter) {
				((HolderAdapter<?, ?>) adapter).clear();
			}
		}
	}
	
	private OnLoadCompleteListener loadCompleteListener = new OnLoadCompleteListener() {
		
		@Override
		public void onComplete(JSONArray response) {
			if (adapter == null) {
				return;
			}
			
			if (adapter instanceof HolderAdapter) {
				setListShown(true);
				((HolderAdapter<?, ?>) adapter).addAll(response);
			}
			
			dispatchEmptyListener();
		}
	};
	
	public void setOnEmptyListener(OnEmptyListener listener) {
		this.emptyListener = listener;
	}
	
	public void load() {
		loader.load();
	}
	
	public void setListShown(boolean shown) {
		setListShown(shown, true);
    }
	
	private void setListShown(boolean shown, boolean animate) {
		if (listContainer.isShown() == shown) {
			return;
		}
		
		if (shown) {
			if (animate) {
				listContainer.startAnimation(fadeIn);
			}
			progressContainer.setVisibility(View.GONE);
			listContainer.setVisibility(View.VISIBLE);
		} else {
			if (animate) {
				listContainer.startAnimation(fadeOut);
			}
			progressContainer.setVisibility(View.VISIBLE);
			listContainer.setVisibility(View.GONE);
		}
	}
	
	public void enableFadingActionBar(boolean enable) {
		enableFadingActionBar = enable;
	}
	
	public void setFadingActionBarTitle(CharSequence title) {
		if (fadingActionBarHelper != null) {
			fadingActionBarHelper.setTitle(title);
		}
	}
	
	public void setFadingActionBarIcon(Menu menu, int resId) {
		if (menu == null || fadingActionBarHelper == null) {
			return;
		}
		
		MenuItem menuItem = menu.findItem(resId);
		if (menuItem != null) {
			fadingActionBarHelper.setOverflow(menuItem);
		}
	}

	public View getListHeaderView() {
		return listHeaderView;
	}
	
	public View getFixedHeaderView() {
		return fixedHeaderView;
	}
	
	protected int getListHeaderViewResId() {
		return View.NO_ID;
	}
	
	protected int getFixedHeaderViewResId() {
		return View.NO_ID;
	}
	
	public interface OnEmptyListener {
		
		public void onEmpty();
		
	}

}
