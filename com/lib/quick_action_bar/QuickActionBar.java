package com.lib.quick_action_bar;

import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hikam.C0291R;
import com.lib.quick_action_bar.QuickActionWidget.OnQuickActionClickListener;
import java.lang.ref.WeakReference;
import java.util.List;

public class QuickActionBar extends QuickActionWidget {
    private OnClickListener mClickHandlerInternal = new C06682();
    private ViewGroup mQuickActionItems;
    private List<QuickAction> mQuickActions;
    private RelativeLayout mRack;
    private Animation mRackAnimation;
    private HorizontalScrollView mScrollView;

    class C06671 implements Interpolator {
        C06671() {
        }

        public float getInterpolation(float t) {
            float inner = (1.55f * t) - 1.1f;
            return 1.2f - (inner * inner);
        }
    }

    class C06682 implements OnClickListener {
        C06682() {
        }

        public void onClick(View view) {
            OnQuickActionClickListener listener = QuickActionBar.this.getOnQuickActionClickListener();
            if (listener != null) {
                int itemCount = QuickActionBar.this.mQuickActions.size();
                for (int i = 0; i < itemCount; i++) {
                    if (view == ((QuickAction) QuickActionBar.this.mQuickActions.get(i)).mView.get()) {
                        listener.onQuickActionClicked(QuickActionBar.this, i);
                        break;
                    }
                }
            }
            if (QuickActionBar.this.getDismissOnClick()) {
                QuickActionBar.this.dismiss();
            }
        }
    }

    public QuickActionBar(Context context) {
        super(context);
        this.mRackAnimation = AnimationUtils.loadAnimation(context, C0291R.anim.gd_rack);
        this.mRackAnimation.setInterpolator(new C06671());
        setContentView(C0291R.layout.gd_quick_action_bar);
        View v = getContentView();
        this.mRack = (RelativeLayout) v.findViewById(C0291R.id.gdi_rack);
        this.mQuickActionItems = (ViewGroup) v.findViewById(C0291R.id.gdi_quick_action_items);
        this.mScrollView = (HorizontalScrollView) v.findViewById(C0291R.id.gdi_scroll);
    }

    public void show(View anchor) {
        super.show(anchor);
        this.mScrollView.scrollTo(0, 0);
        this.mRack.startAnimation(this.mRackAnimation);
    }

    protected void onMeasureAndLayout(Rect anchorRect, View contentView) {
        contentView.setLayoutParams(new LayoutParams(-2, -2));
        contentView.measure(MeasureSpec.makeMeasureSpec(getScreenWidth(), 1073741824), -2);
        int rootHeight = contentView.getMeasuredHeight();
        int offsetY = getArrowOffsetY();
        boolean onTop = anchorRect.top > getScreenHeight() - anchorRect.bottom;
        setWidgetSpecs(onTop ? (anchorRect.top - rootHeight) + offsetY : anchorRect.bottom - offsetY, onTop);
    }

    protected void populateQuickActions(List<QuickAction> quickActions) {
        this.mQuickActions = quickActions;
        LayoutInflater inflater = LayoutInflater.from(getContext());
        for (QuickAction action : quickActions) {
            LinearLayout layout = (LinearLayout) inflater.inflate(C0291R.layout.gd_quick_action_bar_item, this.mQuickActionItems, false);
            ((TextView) layout.findViewById(C0291R.id.item_name)).setText(action.mTitle);
            ((ImageView) layout.findViewById(C0291R.id.item_img)).setImageDrawable(action.mDrawable);
            layout.setOnClickListener(this.mClickHandlerInternal);
            this.mQuickActionItems.addView(layout);
            action.mView = new WeakReference(layout);
        }
    }

    protected void onClearQuickActions() {
        super.onClearQuickActions();
        this.mQuickActionItems.removeAllViews();
    }
}
