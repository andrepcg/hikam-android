package com.lib.quick_action_bar;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowManager;
import android.widget.PopupWindow;
import com.hikam.C0291R;
import java.util.ArrayList;
import java.util.List;

public abstract class QuickActionWidget extends PopupWindow {
    private static final int MEASURE_AND_LAYOUT_DONE = 2;
    private int mArrowOffsetY;
    private Context mContext;
    private boolean mDismissOnClick;
    private boolean mIsDirty;
    private boolean mIsOnTop;
    private final int[] mLocation = new int[2];
    private OnQuickActionClickListener mOnQuickActionClickListener;
    private int mPopupY;
    private int mPrivateFlags;
    private ArrayList<QuickAction> mQuickActions = new ArrayList();
    private final Rect mRect = new Rect();
    private int mScreenHeight;
    private int mScreenWidth;

    public interface OnQuickActionClickListener {
        void onQuickActionClicked(QuickActionWidget quickActionWidget, int i);
    }

    protected abstract void onMeasureAndLayout(Rect rect, View view);

    protected abstract void populateQuickActions(List<QuickAction> list);

    public QuickActionWidget(Context context) {
        super(context);
        this.mContext = context;
        initializeDefault();
        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true);
        setWidth(-2);
        setHeight(-2);
        WindowManager windowManager = (WindowManager) this.mContext.getSystemService("window");
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        this.mScreenWidth = dm.widthPixels;
        this.mScreenHeight = dm.heightPixels;
    }

    public void setContentView(int layoutId) {
        setContentView(LayoutInflater.from(this.mContext).inflate(layoutId, null));
    }

    private void initializeDefault() {
        this.mDismissOnClick = true;
        this.mArrowOffsetY = this.mContext.getResources().getDimensionPixelSize(C0291R.dimen.gd_arrow_offset);
    }

    public int getArrowOffsetY() {
        return this.mArrowOffsetY;
    }

    public void setArrowOffsetY(int offsetY) {
        this.mArrowOffsetY = offsetY;
    }

    protected int getScreenWidth() {
        return this.mScreenWidth;
    }

    protected int getScreenHeight() {
        return this.mScreenHeight;
    }

    public void setDismissOnClick(boolean dismissOnClick) {
        this.mDismissOnClick = dismissOnClick;
    }

    public boolean getDismissOnClick() {
        return this.mDismissOnClick;
    }

    public void setOnQuickActionClickListener(OnQuickActionClickListener listener) {
        this.mOnQuickActionClickListener = listener;
    }

    public void addQuickAction(QuickAction action) {
        if (action != null) {
            this.mQuickActions.add(action);
            this.mIsDirty = true;
        }
    }

    public void clearAllQuickActions() {
        if (!this.mQuickActions.isEmpty()) {
            this.mQuickActions.clear();
            this.mIsDirty = true;
        }
    }

    public void show(View anchor) {
        View contentView = getContentView();
        if (contentView == null) {
            throw new IllegalStateException("You need to set the content view using the setContentView method");
        }
        setBackgroundDrawable(new ColorDrawable(0));
        int[] loc = this.mLocation;
        anchor.getLocationOnScreen(loc);
        this.mRect.set(loc[0], loc[1], loc[0] + anchor.getWidth(), loc[1] + anchor.getHeight());
        if (this.mIsDirty) {
            clearQuickActions();
            populateQuickActions(this.mQuickActions);
        }
        onMeasureAndLayout(this.mRect, contentView);
        if ((this.mPrivateFlags & 2) != 2) {
            throw new IllegalStateException("onMeasureAndLayout() did not set the widget specification by calling setWidgetSpecs()");
        }
        showArrow();
        prepareAnimationStyle();
        showAtLocation(anchor, 0, 0, this.mPopupY);
    }

    protected void clearQuickActions() {
        if (!this.mQuickActions.isEmpty()) {
            onClearQuickActions();
        }
    }

    protected void onClearQuickActions() {
    }

    protected void setWidgetSpecs(int popupY, boolean isOnTop) {
        this.mPopupY = popupY;
        this.mIsOnTop = isOnTop;
        this.mPrivateFlags |= 2;
    }

    private void showArrow() {
        View contentView = getContentView();
        int arrowId = this.mIsOnTop ? C0291R.id.gdi_arrow_down : C0291R.id.gdi_arrow_up;
        View arrow = contentView.findViewById(arrowId);
        View arrowUp = contentView.findViewById(C0291R.id.gdi_arrow_up);
        View arrowDown = contentView.findViewById(C0291R.id.gdi_arrow_down);
        if (arrowId == C0291R.id.gdi_arrow_up) {
            arrowUp.setVisibility(0);
            arrowDown.setVisibility(4);
        } else if (arrowId == C0291R.id.gdi_arrow_down) {
            arrowUp.setVisibility(4);
            arrowDown.setVisibility(0);
        }
        ((MarginLayoutParams) arrow.getLayoutParams()).leftMargin = this.mRect.centerX() - (arrow.getMeasuredWidth() / 2);
    }

    private void prepareAnimationStyle() {
        int screenWidth = this.mScreenWidth;
        boolean onTop = this.mIsOnTop;
        int arrowPointX = this.mRect.centerX();
        if (arrowPointX <= screenWidth / 4) {
            setAnimationStyle(onTop ? C0291R.style.popup_left : C0291R.style.popdown_left);
        } else if (arrowPointX >= (screenWidth * 3) / 4) {
            setAnimationStyle(onTop ? C0291R.style.popup_right : C0291R.style.popdown_right);
        } else {
            setAnimationStyle(onTop ? C0291R.style.popup_center : C0291R.style.popdown_center);
        }
    }

    protected Context getContext() {
        return this.mContext;
    }

    protected OnQuickActionClickListener getOnQuickActionClickListener() {
        return this.mOnQuickActionClickListener;
    }
}
