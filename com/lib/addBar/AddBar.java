package com.lib.addBar;

import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.hikam.C0291R;
import com.jwkj.utils.Utils;
import java.util.ArrayList;

public class AddBar extends RelativeLayout {
    Handler handler = new Handler(new C06551());
    boolean isVisiableArrow = true;
    private int item_count = 0;
    ArrayList<View> items = new ArrayList();
    Context mContext;
    int max_count = 0;
    OnItemChangeListener onItemChangeListener;
    OnItemClickListener onItemClickListener;
    OnLeftIconClickListener onLeftIconClickListener;
    LinearLayout parent;
    LayoutParams parent_params;

    class C06551 implements Callback {
        C06551() {
        }

        public boolean handleMessage(Message msg) {
            int length = msg.arg1;
            if (AddBar.this.parent_params != null) {
                LayoutParams layoutParams = AddBar.this.parent_params;
                layoutParams.height += length;
                AddBar.this.parent.setLayoutParams(AddBar.this.parent_params);
            }
            return false;
        }
    }

    public AddBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public void updateItem(int position, String content) {
        try {
            View view = (View) this.items.get(position);
            if (view != null) {
                ((TextView) view.findViewById(C0291R.id.alarmId_text)).setText(content);
            }
        } catch (Exception e) {
            Log.e("my", "update view error");
        }
    }

    public void addItem(String id) {
        this.parent = (LinearLayout) findViewById(C0291R.id.add_bar_parent);
        this.parent_params = (LayoutParams) this.parent.getLayoutParams();
        View view = LayoutInflater.from(this.mContext).inflate(C0291R.layout.list_add_bar_item, null);
        RelativeLayout item = (RelativeLayout) view.findViewById(C0291R.id.add_bar_item);
        ImageView arrow = (ImageView) item.findViewById(C0291R.id.arrow);
        if (!this.isVisiableArrow) {
            arrow.setVisibility(4);
        }
        TextView alarmId_text = (TextView) view.findViewById(C0291R.id.alarmId_text);
        if (id.equals("")) {
            alarmId_text.setText(C0291R.string.unbound);
        } else {
            alarmId_text.setText(id);
        }
        LayoutParams params = (LayoutParams) item.getLayoutParams();
        if (this.item_count > 0) {
            ((RelativeLayout) ((View) this.items.get(this.item_count - 1)).findViewById(C0291R.id.add_bar_item)).setBackgroundResource(C0291R.drawable.tiao_bg_center);
        }
        this.items.add(view);
        this.parent.addView(view, this.item_count);
        this.item_count++;
        if (this.onItemChangeListener != null) {
            this.onItemChangeListener.onChange(this.item_count);
        }
        int total = params.height;
        changeParent(total / 10, total % 10);
        UpdateItemListener();
    }

    private synchronized void changeParent(final int count, final int remainder) {
        new Thread() {
            public void run() {
                int n = count;
                while (n >= 0) {
                    n--;
                    Message msg = new Message();
                    if (n == 0) {
                        msg.arg1 = remainder;
                    } else {
                        msg.arg1 = 10;
                    }
                    AddBar.this.handler.sendMessage(msg);
                    Utils.sleepThread(20);
                }
            }
        }.start();
    }

    public void removeItem(int position) {
        this.parent = (LinearLayout) findViewById(C0291R.id.add_bar_parent);
        this.parent_params = (LayoutParams) this.parent.getLayoutParams();
        LayoutParams params = (LayoutParams) ((RelativeLayout) ((View) this.items.get(position)).findViewById(C0291R.id.add_bar_item)).getLayoutParams();
        LayoutParams layoutParams = this.parent_params;
        layoutParams.height -= params.height;
        this.parent.removeViewAt(position);
        this.items.remove(position);
        this.item_count--;
        if (this.onItemChangeListener != null) {
            this.onItemChangeListener.onChange(this.item_count);
        }
        if (this.item_count > 0) {
            ((RelativeLayout) ((View) this.items.get(this.item_count - 1)).findViewById(C0291R.id.add_bar_item)).setBackgroundResource(C0291R.drawable.tiao_bg_bottom);
        }
        UpdateItemListener();
    }

    public void removeAll() {
        while (this.item_count > 0) {
            removeItem(this.item_count - 1);
        }
    }

    private void UpdateItemListener() {
        for (int i = 0; i < this.items.size(); i++) {
            final int position = i;
            View view = (View) this.items.get(i);
            final RelativeLayout item = (RelativeLayout) view.findViewById(C0291R.id.add_bar_item);
            final ImageView left_icon = (ImageView) view.findViewById(C0291R.id.delete_item);
            if (this.onItemClickListener != null) {
                item.setOnClickListener(new OnClickListener() {
                    public void onClick(View arg0) {
                        AddBar.this.onItemClickListener.onClick(item, position);
                    }
                });
            }
            if (this.onLeftIconClickListener != null) {
                left_icon.setOnClickListener(new OnClickListener() {
                    public void onClick(View arg0) {
                        AddBar.this.onLeftIconClickListener.onClick(left_icon, position);
                    }
                });
            }
        }
    }

    public void setOnItemChangeListener(OnItemChangeListener onItemChangeListener) {
        this.onItemChangeListener = onItemChangeListener;
    }

    public void setOnLeftIconClickListener(OnLeftIconClickListener onLeftIconClickListener) {
        this.onLeftIconClickListener = onLeftIconClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public int getItemCount() {
        return this.item_count;
    }

    public int getMax_count() {
        return this.max_count;
    }

    public void setMax_count(int max_count) {
        this.max_count = max_count;
    }

    public void setArrowVisiable(boolean bool) {
        this.isVisiableArrow = bool;
    }
}
