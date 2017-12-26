package com.jwkj.widget;

import android.content.Context;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.hikam.C0291R;

public class MyInputDialog {
    String btn1_str;
    String btn2_str;
    Context context;
    EditText input1;
    boolean isShow = false;
    private OnButtonCancelListener onButtonCancelListener;
    private OnButtonOkListener onButtonOkListener;
    String title_str;

    class C05871 implements OnTouchListener {
        C05871() {
        }

        public boolean onTouch(View arg0, MotionEvent arg1) {
            return true;
        }
    }

    class C05904 implements AnimationListener {
        C05904() {
        }

        public void onAnimationEnd(Animation arg0) {
            ((InputMethodManager) MyInputDialog.this.input1.getContext().getSystemService("input_method")).toggleSoftInput(0, 2);
        }

        public void onAnimationRepeat(Animation arg0) {
        }

        public void onAnimationStart(Animation arg0) {
        }
    }

    public interface OnButtonCancelListener {
        void onClick();
    }

    public interface OnButtonOkListener {
        void onClick();
    }

    public MyInputDialog(Context context) {
        this.context = context;
    }

    public void show(final View view) {
        view.setOnTouchListener(new C05871());
        TextView title = (TextView) view.findViewById(C0291R.id.title_text);
        TextView button1 = (TextView) view.findViewById(C0291R.id.button1_text);
        TextView button2 = (TextView) view.findViewById(C0291R.id.button2_text);
        this.input1 = (EditText) view.findViewById(C0291R.id.input1);
        this.input1.setText("");
        this.input1.requestFocus();
        title.setText(this.title_str);
        button1.setText(this.btn1_str);
        button2.setText(this.btn2_str);
        button1.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (MyInputDialog.this.onButtonOkListener != null) {
                    MyInputDialog.this.onButtonOkListener.onClick();
                } else {
                    MyInputDialog.this.hideDialog(view);
                }
            }
        });
        button2.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (MyInputDialog.this.onButtonCancelListener == null) {
                    MyInputDialog.this.hideDialog(view);
                } else {
                    MyInputDialog.this.onButtonCancelListener.onClick();
                }
            }
        });
        showDialog(view);
    }

    public void setOnButtonOkListener(OnButtonOkListener onButtonOkListener) {
        this.onButtonOkListener = onButtonOkListener;
    }

    public void setOnButtonCancelListener(OnButtonCancelListener onButtonCancelListener) {
        this.onButtonCancelListener = onButtonCancelListener;
    }

    public void setTitle(String title) {
        this.title_str = title;
    }

    public void setBtn1_str(String btn1_str) {
        this.btn1_str = btn1_str;
    }

    public void setBtn2_str(String btn2_str) {
        this.btn2_str = btn2_str;
    }

    private void showDialog(View v) {
        this.isShow = true;
        v.setVisibility(0);
        LinearLayout dialog = (LinearLayout) v.findViewById(C0291R.id.dialog_input);
        dialog.setVisibility(0);
        Animation anim = AnimationUtils.loadAnimation(this.context, C0291R.anim.scale_in);
        anim.setAnimationListener(new C05904());
        dialog.startAnimation(anim);
    }

    private void hideDialog(final View v) {
        this.isShow = false;
        final LinearLayout dialog = (LinearLayout) v.findViewById(C0291R.id.dialog_input);
        dialog.setVisibility(0);
        Animation anim = AnimationUtils.loadAnimation(this.context, C0291R.anim.scale_out);
        anim.setAnimationListener(new AnimationListener() {
            public void onAnimationEnd(Animation arg0) {
                dialog.setVisibility(8);
                v.setVisibility(8);
            }

            public void onAnimationRepeat(Animation arg0) {
            }

            public void onAnimationStart(Animation arg0) {
            }
        });
        dialog.startAnimation(anim);
    }

    public void setInput1HintText(String hint) {
        this.input1.setHint(hint);
    }

    public void setInput1HintText(int rid) {
        this.input1.setHint(rid);
    }

    public String getInput1Text() {
        return this.input1.getText().toString();
    }

    public void setInput1Type_number() {
        this.input1.setInputType(2);
        this.input1.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    public void hide(View view) {
        if (this.isShow) {
            hideDialog(view);
        }
    }

    public boolean isShowing() {
        return this.isShow;
    }
}
