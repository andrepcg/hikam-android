package com.jwkj.widget.stepview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hikam.C0291R;
import com.jwkj.widget.stepview.HorizontalStepsViewIndicator.OnDrawIndicatorListener;
import java.util.List;

public class HorizontalStepView extends LinearLayout implements OnDrawIndicatorListener {
    private int mComplectedTextColor;
    private int mComplectingPosition;
    private List<StepBean> mStepBeanList;
    private HorizontalStepsViewIndicator mStepsViewIndicator;
    private RelativeLayout mTextContainer;
    private int mTextSize;
    private TextView mTextView;
    private int mUnComplectedTextColor;

    public HorizontalStepView(Context context) {
        this(context, null);
    }

    public HorizontalStepView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalStepView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mUnComplectedTextColor = ContextCompat.getColor(getContext(), C0291R.color.uncompleted_text_color);
        this.mComplectedTextColor = ContextCompat.getColor(getContext(), 17170443);
        this.mTextSize = 14;
        init();
    }

    private void init() {
        View rootView = LayoutInflater.from(getContext()).inflate(C0291R.layout.widget_horizontal_stepsview, this);
        this.mStepsViewIndicator = (HorizontalStepsViewIndicator) rootView.findViewById(C0291R.id.steps_indicator);
        this.mStepsViewIndicator.setOnDrawListener(this);
        this.mTextContainer = (RelativeLayout) rootView.findViewById(C0291R.id.rl_text_container);
    }

    public HorizontalStepView setStepViewTexts(List<StepBean> stepsBeanList) {
        this.mStepBeanList = stepsBeanList;
        this.mStepsViewIndicator.setStepNum(this.mStepBeanList);
        return this;
    }

    public void nextStep(int current_step) {
        int size = this.mStepBeanList.size();
        for (int i = 0; i < size; i++) {
            if (i < current_step) {
                ((StepBean) this.mStepBeanList.get(i)).setState(1);
            } else {
                ((StepBean) this.mStepBeanList.get(i)).setState(-1);
            }
        }
        this.mStepsViewIndicator.setNext(this.mStepBeanList);
        invalidate();
    }

    public HorizontalStepView setStepViewUnComplectedTextColor(int unComplectedTextColor) {
        this.mUnComplectedTextColor = unComplectedTextColor;
        return this;
    }

    public HorizontalStepView setStepViewComplectedTextColor(int complectedTextColor) {
        this.mComplectedTextColor = complectedTextColor;
        return this;
    }

    public HorizontalStepView setStepsViewIndicatorUnCompletedLineColor(int unCompletedLineColor) {
        this.mStepsViewIndicator.setUnCompletedLineColor(unCompletedLineColor);
        return this;
    }

    public HorizontalStepView setStepsViewIndicatorCompletedLineColor(int completedLineColor) {
        this.mStepsViewIndicator.setCompletedLineColor(completedLineColor);
        return this;
    }

    public HorizontalStepView setStepsViewIndicatorDefaultIcon(Drawable defaultIcon) {
        this.mStepsViewIndicator.setDefaultIcon(defaultIcon);
        return this;
    }

    public HorizontalStepView setStepsViewIndicatorCompleteIcon(Drawable completeIcon) {
        this.mStepsViewIndicator.setCompleteIcon(completeIcon);
        return this;
    }

    public HorizontalStepView setStepsViewIndicatorAttentionIcon(Drawable attentionIcon) {
        this.mStepsViewIndicator.setAttentionIcon(attentionIcon);
        return this;
    }

    public HorizontalStepView setTextSize(int textSize) {
        if (textSize > 0) {
            this.mTextSize = textSize;
        }
        return this;
    }

    public void ondrawIndicator() {
        if (this.mTextContainer != null) {
            this.mTextContainer.removeAllViews();
            List<Float> complectedXPosition = this.mStepsViewIndicator.getCircleCenterPointPositionList();
            if (this.mStepBeanList != null && complectedXPosition != null && complectedXPosition.size() > 0) {
                for (int i = 0; i < this.mStepBeanList.size(); i++) {
                    this.mTextView = new TextView(getContext());
                    this.mTextView.setTextSize(2, (float) this.mTextSize);
                    this.mTextView.setText(((StepBean) this.mStepBeanList.get(i)).getName());
                    int spec = MeasureSpec.makeMeasureSpec(0, 0);
                    this.mTextView.measure(spec, spec);
                    this.mTextView.setX(((Float) complectedXPosition.get(i)).floatValue() - ((float) (this.mTextView.getMeasuredWidth() / 2)));
                    this.mTextView.setLayoutParams(new LayoutParams(-2, -2));
                    if (i <= this.mComplectingPosition) {
                        this.mTextView.setTypeface(null, 1);
                        this.mTextView.setTextColor(this.mComplectedTextColor);
                    } else {
                        this.mTextView.setTextColor(this.mUnComplectedTextColor);
                    }
                    this.mTextContainer.addView(this.mTextView);
                }
            }
        }
    }
}
