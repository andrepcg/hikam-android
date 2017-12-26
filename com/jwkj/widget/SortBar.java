package com.jwkj.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;
import com.hikam.C0291R;

public class SortBar extends Button {
    private String[] assort;
    private OnTouchSortListener onTouchSortListener;
    private Paint paint;
    private int selectIndex;

    public interface OnTouchSortListener {
        void onTouchAssortListener(String str);

        void onTouchAssortUP();
    }

    public SortBar(Context context) {
        super(context);
        this.assort = new String[]{"?", "#", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", NDEFRecord.TEXT_WELL_KNOWN_TYPE, NDEFRecord.URI_WELL_KNOWN_TYPE, "V", "W", "X", "Y", "Z"};
        this.paint = new Paint();
        this.selectIndex = -1;
    }

    public SortBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.assort = new String[]{"?", "#", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", NDEFRecord.TEXT_WELL_KNOWN_TYPE, NDEFRecord.URI_WELL_KNOWN_TYPE, "V", "W", "X", "Y", "Z"};
        this.paint = new Paint();
        this.selectIndex = -1;
    }

    public SortBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.assort = new String[]{"?", "#", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", NDEFRecord.TEXT_WELL_KNOWN_TYPE, NDEFRecord.URI_WELL_KNOWN_TYPE, "V", "W", "X", "Y", "Z"};
        this.paint = new Paint();
        this.selectIndex = -1;
    }

    public void setOnTouchSortListener(OnTouchSortListener onTouchSortListener) {
        this.onTouchSortListener = onTouchSortListener;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int height = getHeight();
        int width = getWidth();
        int interval = height / this.assort.length;
        int length = this.assort.length;
        for (int i = 0; i < length; i++) {
            this.paint.setAntiAlias(true);
            this.paint.setTypeface(Typeface.DEFAULT_BOLD);
            this.paint.setTextSize(getResources().getDimension(C0291R.dimen.size_12));
            this.paint.setColor(-1);
            if (i == this.selectIndex) {
                this.paint.setColor(Color.parseColor("#3399ff"));
                this.paint.setFakeBoldText(true);
                this.paint.setTextSize(30.0f);
            }
            canvas.drawText(this.assort[i], ((float) (width / 2)) - (this.paint.measureText(this.assort[i]) / 2.0f), (float) ((interval * i) + interval), this.paint);
            this.paint.reset();
        }
    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        int index = (int) ((event.getY() / ((float) getHeight())) * ((float) this.assort.length));
        if (index >= 0 && index < this.assort.length) {
            switch (event.getAction()) {
                case 0:
                    this.selectIndex = index;
                    if (this.onTouchSortListener != null) {
                        this.onTouchSortListener.onTouchAssortListener(this.assort[this.selectIndex]);
                        break;
                    }
                    break;
                case 1:
                    if (this.onTouchSortListener != null) {
                        this.onTouchSortListener.onTouchAssortUP();
                    }
                    this.selectIndex = -1;
                    break;
                case 2:
                    if (this.selectIndex != index) {
                        this.selectIndex = index;
                        if (this.onTouchSortListener != null) {
                            this.onTouchSortListener.onTouchAssortListener(this.assort[this.selectIndex]);
                            break;
                        }
                    }
                    break;
            }
        }
        this.selectIndex = -1;
        if (this.onTouchSortListener != null) {
            this.onTouchSortListener.onTouchAssortUP();
        }
        invalidate();
        return true;
    }

    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
}
