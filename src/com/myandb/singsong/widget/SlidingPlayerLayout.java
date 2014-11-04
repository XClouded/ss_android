package com.myandb.singsong.widget;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.myandb.singsong.R;
import com.myandb.singsong.activity.RootActivity;
import com.myandb.singsong.service.PlayerService;
import com.nineoldandroids.view.animation.AnimatorProxy;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class SlidingPlayerLayout extends SlidingUpPanelLayout {
	
	private PlayerService service;
	private ViewGroup slidingContainer; 
	private int actionBarHeight;

	public SlidingPlayerLayout(Context context) {
		super(context);
	}
	
	public SlidingPlayerLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SlidingPlayerLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public void setPlayerService(PlayerService service) {
		this.service = service;
		
		setSlidingContainer(R.id.fl_sliding_container);
		
		setContentView(R.layout.activity_play_song_header);
		
		setDragView(R.id.music_info);
		
		setPanelSlideListener(panelSlideListener);
		
		initiateChildViews();
		
		styleChildViews();
		
		bindEventOnChildViews();
	}
	
	public void setSlidingContainer(int containerId) {
		if (slidingContainer == null) {
			slidingContainer = (ViewGroup) findViewById(containerId);
		}
	}
	
	public void setContentView(int layoutId) {
		View.inflate(getContext(), layoutId, slidingContainer);
	}
	
	private void setDragView(int dragViewId) {
		View dragView = findViewById(dragViewId);
		setDragView(dragView);
	}
	
	private void initiateChildViews() {
		
	}
	
	private void styleChildViews() {
		
	}
	
	private void bindEventOnChildViews() {
		
	}

	public void hideActionBarWhenSliding(boolean enable) {
		if (enable) {
			actionBarHeight = getActionBarHeight();
		} else {
			actionBarHeight = 0;
		}
	}

    private int getActionBarHeight(){
    	return getResources().getDimensionPixelSize(R.dimen.abc_action_bar_default_height);
    }

    private PanelSlideListener panelSlideListener = new PanelSlideListener() {
		
		@Override
		public void onPanelSlide(View panel, float slideOffset) {
			if (actionBarHeight > 0) {
				int offset = getCurrentParalaxOffset();
				setActionBarTranslation(offset);
			}
		}
		
		@Override
		public void onPanelHidden(View panel) {}
		
		@Override
		public void onPanelExpanded(View panel) {
			if (getContext() instanceof RootActivity) {
				((RootActivity) getContext()).onContentInvisible();
			}
		}
		
		@Override
		public void onPanelCollapsed(View panel) {
			if (getContext() instanceof RootActivity) {
				((RootActivity) getContext()).onContentVisible();
			}
		}
		
		@Override
		public void onPanelAnchored(View panel) {}
	};

	public void setActionBarTranslation(float y) {
		final int actionBarContentId = android.R.id.content;
		
		View content = ((Activity) getContext()).findViewById(actionBarContentId);
		ViewGroup window = (ViewGroup) content.getParent();
        for (int i = 0, l = window.getChildCount(); i < l; i++) {
            View child = window.getChildAt(i);
            if (child.getId() != actionBarContentId) {
                if (y <= -actionBarHeight) {
                    child.setVisibility(View.GONE);
                } else {
                    child.setVisibility(View.VISIBLE);
                    setCompatTranslationY(child, y);
                }
            }
        }
    }
    
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setCompatTranslationY(View view, float y) {
    	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            view.setTranslationY(y);
        } else {
            AnimatorProxy.wrap(view).setTranslationY(y);
        }
    }
	
}
