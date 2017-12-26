package com.jwkj.widget.stepview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hikam.C0291R;
import com.jwkj.widget.stepview.VerticalStepViewIndicator.OnDrawIndicatorListener;
import java.util.List;

public class VerticalStepView extends LinearLayout implements OnDrawIndicatorListener {
    private int mComplectedTextColor;
    private int mComplectingPosition;
    private VerticalStepViewIndicator mStepsViewIndicator;
    private RelativeLayout mTextContainer;
    private int mTextSize;
    private TextView mTextView;
    private List<String> mTexts;
    private int mUnComplectedTextColor;

    public VerticalStepView(Context context) {
        this(context, null);
    }

    public VerticalStepView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalStepView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mUnComplectedTextColor = ContextCompat.getColor(getContext(), C0291R.color.uncompleted_text_color);
        this.mComplectedTextColor = ContextCompat.getColor(getContext(), 17170443);
        this.mTextSize = 14;
        init();
    }

    private void init() {
        View rootView = LayoutInflater.from(getContext()).inflate(C0291R.layout.widget_vertical_stepview, this);
        this.mStepsViewIndicator = (VerticalStepViewIndicator) rootView.findViewById(C0291R.id.steps_indicator);
        this.mStepsViewIndicator.setOnDrawListener(this);
        this.mTextContainer = (RelativeLayout) rootView.findViewById(C0291R.id.rl_text_container);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public VerticalStepView setStepViewTexts(List<String> texts) {
        this.mTexts = texts;
        if (texts != null) {
            this.mStepsViewIndicator.setStepNum(this.mTexts.size());
        } else {
            this.mStepsViewIndicator.setStepNum(0);
        }
        return this;
    }

    public VerticalStepView setStepsViewIndicatorComplectingPosition(int complectingPosition) {
        this.mComplectingPosition = complectingPosition;
        this.mStepsViewIndicator.setComplectingPosition(complectingPosition);
        return this;
    }

    public VerticalStepView setStepViewUnComplectedTextColor(int unComplectedTextColor) {
        this.mUnComplectedTextColor = unComplectedTextColor;
        return this;
    }

    public VerticalStepView setStepViewComplectedTextColor(int complectedTextColor) {
        this.mComplectedTextColor = complectedTextColor;
        return this;
    }

    public VerticalStepView setStepsViewIndicatorUnCompletedLineColor(int unCompletedLineColor) {
        this.mStepsViewIndicator.setUnCompletedLineColor(unCompletedLineColor);
        return this;
    }

    public VerticalStepView setStepsViewIndicatorCompletedLineColor(int completedLineColor) {
        this.mStepsViewIndicator.setCompletedLineColor(completedLineColor);
        return this;
    }

    public VerticalStepView setStepsViewIndicatorDefaultIcon(Drawable defaultIcon) {
        this.mStepsViewIndicator.setDefaultIcon(defaultIcon);
        return this;
    }

    public VerticalStepView setStepsViewIndicatorCompleteIcon(Drawable completeIcon) {
        this.mStepsViewIndicator.setCompleteIcon(completeIcon);
        return this;
    }

    public VerticalStepView setStepsViewIndicatorAttentionIcon(Drawable attentionIcon) {
        this.mStepsViewIndicator.setAttentionIcon(attentionIcon);
        return this;
    }

    public VerticalStepView reverseDraw(boolean isReverSe) {
        this.mStepsViewIndicator.reverseDraw(isReverSe);
        return this;
    }

    public VerticalStepView setLinePaddingProportion(float linePaddingProportion) {
        this.mStepsViewIndicator.setIndicatorLinePaddingProportion(linePaddingProportion);
        return this;
    }

    public VerticalStepView setTextSize(int textSize) {
        if (textSize > 0) {
            this.mTextSize = textSize;
        }
        return this;
    }

    public void ondrawIndicator() {
        if (this.mTextContainer != null) {
            this.mTextContainer.removeAllViews();
            List<Float> complectedXPosition = this.mStepsViewIndicator.getCircleCenterPointPositionList();
            if (this.mTexts != null && complectedXPosition != null && complectedXPosition.size() > 0) {
                for (int i = 0; i < this.mTexts.size(); i++) {
                    this.mTextView = new TextView(getContext());
                    this.mTextView.setTextSize(2, (float) this.mTextSize);
                    this.mTextView.setText((CharSequence) this.mTexts.get(i));
                    this.mTextView.setY(((Float) complectedXPosition.get(i)).floatValue() - (this.mStepsViewIndicator.getCircleRadius() / 2.0f));
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
