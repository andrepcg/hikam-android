package com.lib.quick_action_bar;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import java.lang.ref.WeakReference;

public class QuickAction {
    public Drawable mDrawable;
    public CharSequence mTitle;
    WeakReference<View> mView;

    public QuickAction(Drawable d, CharSequence title) {
        this.mDrawable = d;
        this.mTitle = title;
    }

    public QuickAction(Context ctx, int drawableId, CharSequence title) {
        this.mDrawable = ctx.getResources().getDrawable(drawableId);
        this.mTitle = title;
    }

    public QuickAction(Context ctx, Drawable d, int titleId) {
        this.mDrawable = d;
        this.mTitle = ctx.getResources().getString(titleId);
    }

    public QuickAction(Context ctx, int drawableId, int titleId) {
        this.mDrawable = ctx.getResources().getDrawable(drawableId);
        this.mTitle = ctx.getResources().getString(titleId);
    }
}
