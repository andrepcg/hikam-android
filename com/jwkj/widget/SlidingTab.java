package com.jwkj.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.View.BaseSavedState;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import com.hikam.C0291R;
import java.util.Locale;

public class SlidingTab extends HorizontalScrollView {
    private static final int[] ATTRS = new int[]{16842901, 16842904};
    private int currentPosition;
    private float currentPositionOffset;
    private LayoutParams defaultTabLayoutParams;
    public OnPageChangeListener delegatePageListener;
    private int dividerColor;
    private int dividerPadding;
    private Paint dividerPaint;
    private int dividerWidth;
    private LayoutParams expandedTabLayoutParams;
    private int indicatorColor;
    private int indicatorHeight;
    private int lastScrollX;
    private Locale locale;
    public OnTabClickListener onTabClickListener;
    private final PageListener pageListener;
    private ViewPager pager;
    private Paint rectPaint;
    private int scrollOffset;
    private boolean shouldExpand;
    private int tabBackgroundResId;
    private int tabCount;
    private int tabPadding;
    private int tabTextColor;
    private int tabTextSize;
    private Typeface tabTypeface;
    private int tabTypefaceStyle;
    private LinearLayout tabsContainer;
    private boolean textAllCaps;
    private int underlineColor;
    private int underlineHeight;

    class C06111 implements OnGlobalLayoutListener {
        C06111() {
        }

        @SuppressLint({"NewApi"})
        public void onGlobalLayout() {
            if (VERSION.SDK_INT < 16) {
                SlidingTab.this.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            } else {
                SlidingTab.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
            SlidingTab.this.currentPosition = SlidingTab.this.pager.getCurrentItem();
            SlidingTab.this.scrollToChild(SlidingTab.this.currentPosition, 0);
        }
    }

    public interface IconTabProvider {
        int getPageIconResId(int i);
    }

    public interface OnTabClickListener {
        void onTabClick(int i);
    }

    static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR = new C06131();
        int currentPosition;

        static class C06131 implements Creator<SavedState> {
            C06131() {
            }

            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.currentPosition = in.readInt();
        }

        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(this.currentPosition);
        }
    }

    private class PageListener implements OnPageChangeListener {
        private PageListener() {
        }

        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            SlidingTab.this.currentPosition = position;
            SlidingTab.this.currentPositionOffset = positionOffset;
            SlidingTab.this.scrollToChild(position, (int) (((float) SlidingTab.this.tabsContainer.getChildAt(position).getWidth()) * positionOffset));
            SlidingTab.this.invalidate();
            if (SlidingTab.this.delegatePageListener != null) {
                SlidingTab.this.delegatePageListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }

        public void onPageScrollStateChanged(int state) {
            if (state == 0) {
                SlidingTab.this.scrollToChild(SlidingTab.this.pager.getCurrentItem(), 0);
            }
            if (SlidingTab.this.delegatePageListener != null) {
                SlidingTab.this.delegatePageListener.onPageScrollStateChanged(state);
            }
        }

        public void onPageSelected(int position) {
            if (SlidingTab.this.delegatePageListener != null) {
                SlidingTab.this.delegatePageListener.onPageSelected(position);
            }
            for (int i = 0; i < SlidingTab.this.tabCount; i++) {
                View v = SlidingTab.this.tabsContainer.getChildAt(i);
                if (v instanceof TextView) {
                    TextView tab = (TextView) v;
                    if (i == position) {
                        tab.setTextColor(SlidingTab.this.tabTextColor);
                    } else {
                        tab.setTextColor(SlidingTab.this.getResources().getColor(C0291R.color.gray));
                    }
                }
            }
        }
    }

    public SlidingTab(Context context) {
        this(context, null);
    }

    public SlidingTab(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingTab(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.pageListener = new PageListener();
        this.currentPosition = 0;
        this.currentPositionOffset = 0.0f;
        this.indicatorColor = -10066330;
        this.underlineColor = 436207616;
        this.dividerColor = 436207616;
        this.shouldExpand = false;
        this.textAllCaps = true;
        this.scrollOffset = 52;
        this.indicatorHeight = 8;
        this.underlineHeight = 2;
        this.dividerPadding = 12;
        this.tabPadding = 24;
        this.dividerWidth = 1;
        this.tabTextSize = 12;
        this.tabTextColor = -10066330;
        this.tabTypeface = null;
        this.tabTypefaceStyle = 1;
        this.lastScrollX = 0;
        this.tabBackgroundResId = C0291R.drawable.background_tab;
        setFillViewport(true);
        setWillNotDraw(false);
        this.tabsContainer = new LinearLayout(context);
        this.tabsContainer.setOrientation(0);
        this.tabsContainer.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        addView(this.tabsContainer);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        this.scrollOffset = (int) TypedValue.applyDimension(1, (float) this.scrollOffset, dm);
        this.indicatorHeight = (int) TypedValue.applyDimension(1, (float) this.indicatorHeight, dm);
        this.underlineHeight = (int) TypedValue.applyDimension(1, (float) this.underlineHeight, dm);
        this.dividerPadding = (int) TypedValue.applyDimension(1, (float) this.dividerPadding, dm);
        this.tabPadding = (int) TypedValue.applyDimension(1, (float) this.tabPadding, dm);
        this.dividerWidth = (int) TypedValue.applyDimension(1, (float) this.dividerWidth, dm);
        this.tabTextSize = (int) TypedValue.applyDimension(2, (float) this.tabTextSize, dm);
        TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);
        this.tabTextSize = a.getDimensionPixelSize(0, this.tabTextSize);
        this.tabTextColor = a.getColor(1, this.tabTextColor);
        a.recycle();
        a = context.obtainStyledAttributes(attrs, C0291R.styleable.SlidingTab);
        this.indicatorColor = a.getColor(2, this.indicatorColor);
        this.underlineColor = a.getColor(9, this.underlineColor);
        this.dividerColor = a.getColor(0, this.dividerColor);
        this.indicatorHeight = a.getDimensionPixelSize(3, this.indicatorHeight);
        this.underlineHeight = a.getDimensionPixelSize(10, this.underlineHeight);
        this.dividerPadding = a.getDimensionPixelSize(1, this.dividerPadding);
        this.tabPadding = a.getDimensionPixelSize(7, this.tabPadding);
        this.tabBackgroundResId = a.getResourceId(6, this.tabBackgroundResId);
        this.shouldExpand = a.getBoolean(5, this.shouldExpand);
        this.scrollOffset = a.getDimensionPixelSize(4, this.scrollOffset);
        this.textAllCaps = a.getBoolean(8, this.textAllCaps);
        a.recycle();
        this.rectPaint = new Paint();
        this.rectPaint.setAntiAlias(true);
        this.rectPaint.setStyle(Style.FILL);
        this.dividerPaint = new Paint();
        this.dividerPaint.setAntiAlias(true);
        this.dividerPaint.setStrokeWidth((float) this.dividerWidth);
        this.defaultTabLayoutParams = new LayoutParams(-2, -1);
        this.expandedTabLayoutParams = new LayoutParams(0, -1, 1.0f);
        if (this.locale == null) {
            this.locale = getResources().getConfiguration().locale;
        }
    }

    public void setViewPager(ViewPager pager) {
        this.pager = pager;
        if (pager.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }
        pager.setOnPageChangeListener(this.pageListener);
        notifyDataSetChanged();
    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.delegatePageListener = listener;
    }

    public void notifyDataSetChanged() {
        this.tabsContainer.removeAllViews();
        this.tabCount = this.pager.getAdapter().getCount();
        for (int i = 0; i < this.tabCount; i++) {
            if (this.pager.getAdapter() instanceof IconTabProvider) {
                addIconTab(i, ((IconTabProvider) this.pager.getAdapter()).getPageIconResId(i));
            } else {
                addTextTab(i, this.pager.getAdapter().getPageTitle(i).toString());
            }
        }
        updateTabStyles();
        getViewTreeObserver().addOnGlobalLayoutListener(new C06111());
    }

    private void addTextTab(int position, String title) {
        TextView tab = new TextView(getContext());
        tab.setText(title);
        tab.setGravity(17);
        tab.setSingleLine();
        addTab(position, tab);
    }

    private void addIconTab(int position, int resId) {
        ImageButton tab = new ImageButton(getContext());
        tab.setImageResource(resId);
        addTab(position, tab);
    }

    public void SetOnTabClickListener(OnTabClickListener onTabClickListener) {
        this.onTabClickListener = onTabClickListener;
    }

    private void addTab(final int position, View tab) {
        tab.setFocusable(true);
        tab.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SlidingTab.this.pager.setCurrentItem(position);
                if (SlidingTab.this.onTabClickListener != null) {
                    SlidingTab.this.onTabClickListener.onTabClick(position);
                }
            }
        });
        tab.setPadding(this.tabPadding, 0, this.tabPadding, 0);
        this.tabsContainer.addView(tab, position, this.shouldExpand ? this.expandedTabLayoutParams : this.defaultTabLayoutParams);
    }

    private void updateTabStyles() {
        for (int i = 0; i < this.tabCount; i++) {
            View v = this.tabsContainer.getChildAt(i);
            v.setBackgroundResource(this.tabBackgroundResId);
            if (v instanceof TextView) {
                TextView tab = (TextView) v;
                tab.setTextSize(0, (float) this.tabTextSize);
                tab.setTypeface(this.tabTypeface, this.tabTypefaceStyle);
                if (i == this.currentPosition) {
                    tab.setTextColor(this.tabTextColor);
                } else {
                    tab.setTextColor(getResources().getColor(C0291R.color.gray));
                }
                if (this.textAllCaps) {
                    if (VERSION.SDK_INT >= 14) {
                        tab.setAllCaps(true);
                    } else {
                        tab.setText(tab.getText().toString().toUpperCase(this.locale));
                    }
                }
            }
        }
    }

    private void scrollToChild(int position, int offset) {
        if (this.tabCount != 0) {
            int newScrollX = this.tabsContainer.getChildAt(position).getLeft() + offset;
            if (position > 0 || offset > 0) {
                newScrollX -= this.scrollOffset;
            }
            if (newScrollX != this.lastScrollX) {
                this.lastScrollX = newScrollX;
                scrollTo(newScrollX, 0);
            }
        }
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isInEditMode() && this.tabCount != 0) {
            int height = getHeight();
            this.rectPaint.setColor(this.indicatorColor);
            View currentTab = this.tabsContainer.getChildAt(this.currentPosition);
            float lineLeft = (float) currentTab.getLeft();
            float lineRight = (float) currentTab.getRight();
            if (this.currentPositionOffset > 0.0f && this.currentPosition < this.tabCount - 1) {
                View nextTab = this.tabsContainer.getChildAt(this.currentPosition + 1);
                lineLeft = (this.currentPositionOffset * ((float) nextTab.getLeft())) + ((1.0f - this.currentPositionOffset) * lineLeft);
                lineRight = (this.currentPositionOffset * ((float) nextTab.getRight())) + ((1.0f - this.currentPositionOffset) * lineRight);
            }
            canvas.drawRect(lineLeft, (float) (height - this.indicatorHeight), lineRight, (float) height, this.rectPaint);
            this.rectPaint.setColor(this.underlineColor);
            canvas.drawRect(0.0f, (float) (height - this.underlineHeight), (float) this.tabsContainer.getWidth(), (float) height, this.rectPaint);
            this.dividerPaint.setColor(this.dividerColor);
            for (int i = 0; i < this.tabCount - 1; i++) {
                View tab = this.tabsContainer.getChildAt(i);
                canvas.drawLine((float) tab.getRight(), (float) this.dividerPadding, (float) tab.getRight(), (float) (height - this.dividerPadding), this.dividerPaint);
            }
        }
    }

    public void setIndicatorColor(int indicatorColor) {
        this.indicatorColor = indicatorColor;
        invalidate();
    }

    public void setIndicatorColorResource(int resId) {
        this.indicatorColor = getResources().getColor(resId);
        invalidate();
    }

    public int getIndicatorColor() {
        return this.indicatorColor;
    }

    public void setIndicatorHeight(int indicatorLineHeightPx) {
        this.indicatorHeight = indicatorLineHeightPx;
        invalidate();
    }

    public int getIndicatorHeight() {
        return this.indicatorHeight;
    }

    public void setUnderlineColor(int underlineColor) {
        this.underlineColor = underlineColor;
        invalidate();
    }

    public void setUnderlineColorResource(int resId) {
        this.underlineColor = getResources().getColor(resId);
        invalidate();
    }

    public int getUnderlineColor() {
        return this.underlineColor;
    }

    public void setDividerColor(int dividerColor) {
        this.dividerColor = dividerColor;
        invalidate();
    }

    public void setDividerColorResource(int resId) {
        this.dividerColor = getResources().getColor(resId);
        invalidate();
    }

    public int getDividerColor() {
        return this.dividerColor;
    }

    public void setUnderlineHeight(int underlineHeightPx) {
        this.underlineHeight = underlineHeightPx;
        invalidate();
    }

    public int getUnderlineHeight() {
        return this.underlineHeight;
    }

    public void setDividerPadding(int dividerPaddingPx) {
        this.dividerPadding = dividerPaddingPx;
        invalidate();
    }

    public int getDividerPadding() {
        return this.dividerPadding;
    }

    public void setScrollOffset(int scrollOffsetPx) {
        this.scrollOffset = scrollOffsetPx;
        invalidate();
    }

    public int getScrollOffset() {
        return this.scrollOffset;
    }

    public void setShouldExpand(boolean shouldExpand) {
        this.shouldExpand = shouldExpand;
        requestLayout();
    }

    public boolean getShouldExpand() {
        return this.shouldExpand;
    }

    public boolean isTextAllCaps() {
        return this.textAllCaps;
    }

    public void setAllCaps(boolean textAllCaps) {
        this.textAllCaps = textAllCaps;
    }

    public void setTextSize(int textSizePx) {
        this.tabTextSize = textSizePx;
        updateTabStyles();
    }

    public int getTextSize() {
        return this.tabTextSize;
    }

    public void setTextColor(int textColor) {
        this.tabTextColor = textColor;
        updateTabStyles();
    }

    public void setTextColorResource(int resId) {
        this.tabTextColor = getResources().getColor(resId);
        updateTabStyles();
    }

    public int getTextColor() {
        return this.tabTextColor;
    }

    public void setTypeface(Typeface typeface, int style) {
        this.tabTypeface = typeface;
        this.tabTypefaceStyle = style;
        updateTabStyles();
    }

    public void setTabBackground(int resId) {
        this.tabBackgroundResId = resId;
    }

    public int getTabBackground() {
        return this.tabBackgroundResId;
    }

    public void setTabPaddingLeftRight(int paddingPx) {
        this.tabPadding = paddingPx;
        updateTabStyles();
    }

    public int getTabPaddingLeftRight() {
        return this.tabPadding;
    }

    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        this.currentPosition = savedState.currentPosition;
        requestLayout();
    }

    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.currentPosition = this.currentPosition;
        return savedState;
    }
}
