package com.jwkj.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.hikam.C0291R;

public class MyPwindow {
    private String content = "";
    private Context context;
    private View parent;
    private PopupWindow pwindow;

    public MyPwindow(Context context, View parent) {
        this.context = context;
        this.parent = parent;
    }

    public void showToast() {
        dismiss();
        View view = LayoutInflater.from(this.context).inflate(C0291R.layout.dialog_toast, null);
        ((TextView) view.findViewById(C0291R.id.content)).setText(this.content);
        this.pwindow = new PopupWindow(view, -2, -2);
        this.pwindow.setTouchable(false);
        this.pwindow.setAnimationStyle(C0291R.style.dialog_normal);
        this.pwindow.showAtLocation(this.parent, 80, 0, (int) this.context.getResources().getDimension(C0291R.dimen.ipc_toast_margin_bottom));
    }

    public void dismiss() {
        if (this.pwindow != null) {
            this.pwindow.dismiss();
        }
    }

    public void setContentText(String content) {
        this.content = content;
    }
}
