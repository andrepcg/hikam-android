package com.jwkj.widget;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.AnimationDrawable;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.hikam.C0291R;
import com.jwkj.adapter.SelectorDialogAdapter;
import com.jwkj.global.Constants.Action;
import com.jwkj.global.MyApp;
import com.jwkj.web.HKWebView;

public class NormalDialog {
    public static final int DIALOG_STYLE_DOWNLOAD = 4;
    public static final int DIALOG_STYLE_LOADING = 2;
    public static final int DIALOG_STYLE_NORMAL = 1;
    public static final int DIALOG_STYLE_PROMPT = 5;
    public static final int DIALOG_STYLE_UPDATE = 3;
    String btn1_str;
    String btn2_str;
    String content_str;
    Context context;
    AlertDialog dialog;
    String[] list_data;
    private NumberProgressBar numberProgressBar;
    private OnButtonCancelListener onButtonCancelListener;
    private OnButtonOkListener onButtonOkListener;
    private OnCancelListener onCancelListener;
    private OnItemClickListener onItemClickListener;
    private int style;
    String title_str;

    class C05921 implements OnClickListener {
        C05921() {
        }

        public void onClick(View arg0) {
            if (NormalDialog.this.dialog != null) {
                NormalDialog.this.dialog.dismiss();
            }
        }
    }

    class C05932 implements OnClickListener {
        C05932() {
        }

        public void onClick(View arg0) {
            if (NormalDialog.this.dialog != null) {
                NormalDialog.this.dialog.dismiss();
            }
        }
    }

    class C05943 implements OnClickListener {
        C05943() {
        }

        public void onClick(View arg0) {
            if (NormalDialog.this.dialog != null) {
                NormalDialog.this.dialog.dismiss();
            }
        }
    }

    class C05954 implements OnClickListener {
        C05954() {
        }

        public void onClick(View v) {
            if (NormalDialog.this.dialog != null) {
                NormalDialog.this.dialog.dismiss();
            }
            NormalDialog.this.onButtonOkListener.onClick();
        }
    }

    class C05965 implements OnClickListener {
        C05965() {
        }

        public void onClick(View v) {
            if (NormalDialog.this.onButtonCancelListener != null) {
                NormalDialog.this.onButtonCancelListener.onClick();
            } else if (NormalDialog.this.dialog != null) {
                NormalDialog.this.dialog.cancel();
            }
        }
    }

    class C05976 implements OnClickListener {
        C05976() {
        }

        public void onClick(View v) {
            if (NormalDialog.this.onButtonCancelListener != null) {
                NormalDialog.this.onButtonCancelListener.onClick();
            } else if (NormalDialog.this.dialog != null) {
                NormalDialog.this.dialog.dismiss();
            }
        }
    }

    class C05987 implements OnClickListener {
        C05987() {
        }

        public void onClick(View v) {
            if (NormalDialog.this.onButtonCancelListener != null) {
                NormalDialog.this.onButtonCancelListener.onClick();
            } else if (NormalDialog.this.dialog != null) {
                NormalDialog.this.dialog.dismiss();
            }
        }
    }

    class C05998 implements OnClickListener {
        C05998() {
        }

        public void onClick(View arg0) {
            Intent it = new Intent();
            it.setAction(Action.INSERT_INFRARED_BACK);
            MyApp.app.sendBroadcast(it);
        }
    }

    class C06009 implements OnClickListener {
        C06009() {
        }

        public void onClick(View arg0) {
            if (NormalDialog.this.dialog != null) {
                NormalDialog.this.dialog.dismiss();
            }
        }
    }

    public interface OnButtonCancelListener {
        void onClick();
    }

    public interface OnButtonOkListener {
        void onClick();
    }

    public NormalDialog(Context context, String title, String content, String btn1, String btn2) {
        this.list_data = new String[0];
        this.style = 999;
        this.context = context;
        this.title_str = title;
        this.content_str = content;
        this.btn1_str = btn1;
        this.btn2_str = btn2;
    }

    public NormalDialog(Context context) {
        this.list_data = new String[0];
        this.style = 999;
        this.context = context;
        this.title_str = "";
        this.content_str = "";
        this.btn1_str = "";
        this.btn2_str = "";
    }

    public void showDialog() {
        switch (this.style) {
            case 1:
                showNormalDialog();
                return;
            case 2:
                showLoadingDialog();
                return;
            case 5:
                showPromptDialog();
                return;
            default:
                showNormalDialog();
                return;
        }
    }

    public void showLoadingDialog() {
        View view = LayoutInflater.from(this.context).inflate(C0291R.layout.dialog_loading, null);
        ((TextView) view.findViewById(C0291R.id.title_text)).setText(this.title_str);
        this.dialog = new Builder(this.context, C0291R.style.hikamDialog).create();
        this.dialog.show();
        this.dialog.setContentView(view);
        LayoutParams layout = (LayoutParams) view.getLayoutParams();
        layout.width = (int) this.context.getResources().getDimension(C0291R.dimen.Loading_dialog_width);
        view.setLayoutParams(layout);
        this.dialog.setOnCancelListener(this.onCancelListener);
        this.dialog.setCanceledOnTouchOutside(false);
        this.dialog.getWindow().setWindowAnimations(C0291R.style.dialog_normal);
    }

    public void showProgressDialog(int option, int num) {
        if (option == 0) {
            View view = LayoutInflater.from(this.context).inflate(C0291R.layout.dialog_progress, null);
            ((TextView) view.findViewById(C0291R.id.title_text)).setText(this.title_str);
            this.dialog = new Builder(this.context, C0291R.style.hikamDialog).create();
            this.dialog.show();
            this.dialog.setContentView(view);
            LayoutParams layout = (LayoutParams) view.getLayoutParams();
            layout.width = (int) this.context.getResources().getDimension(C0291R.dimen.Loading_dialog_width);
            view.setLayoutParams(layout);
            this.numberProgressBar = (NumberProgressBar) view.findViewById(C0291R.id.progress);
            this.dialog.setOnCancelListener(this.onCancelListener);
            this.dialog.setCanceledOnTouchOutside(false);
            this.dialog.getWindow().setWindowAnimations(C0291R.style.dialog_normal);
        } else if (option == 1) {
            this.numberProgressBar.setProgress(num);
        }
    }

    public void showAboutDialog() {
        View view = LayoutInflater.from(this.context).inflate(C0291R.layout.dialog_about, null);
        view.setOnClickListener(new C05921());
        try {
            ((TextView) view.findViewById(C0291R.id.version)).setText(this.context.getPackageManager().getPackageInfo(this.context.getPackageName(), 0).versionName);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        this.dialog = new Builder(this.context, C0291R.style.hikamDialog).create();
        this.dialog.show();
        this.dialog.setContentView(view);
        LayoutParams layout = (LayoutParams) view.getLayoutParams();
        layout.width = (int) this.context.getResources().getDimension(C0291R.dimen.about_dialog_width);
        view.setLayoutParams(layout);
        this.dialog.setOnCancelListener(this.onCancelListener);
        this.dialog.setCanceledOnTouchOutside(true);
        this.dialog.getWindow().setWindowAnimations(C0291R.style.dialog_normal);
    }

    public void showHelpInfoDialog(String help_info) {
        View view = LayoutInflater.from(this.context).inflate(C0291R.layout.dialog_help_info, null);
        view.setOnClickListener(new C05932());
        ((TextView) view.findViewById(C0291R.id.help_info)).setText(help_info);
        this.dialog = new Builder(this.context, C0291R.style.hikamDialog).create();
        this.dialog.show();
        this.dialog.setContentView(view);
        LayoutParams layout = (LayoutParams) view.getLayoutParams();
        layout.width = (int) this.context.getResources().getDimension(C0291R.dimen.about_dialog_width);
        view.setLayoutParams(layout);
        this.dialog.setOnCancelListener(this.onCancelListener);
        this.dialog.setCanceledOnTouchOutside(true);
        this.dialog.getWindow().setWindowAnimations(C0291R.style.dialog_normal);
    }

    public void showDeviceInfoDialog(String deviceID, String curVersion, String uBootVersion, String kernelVersion, String rootfsVersion, String deviceIP) {
        View view = LayoutInflater.from(this.context).inflate(C0291R.layout.dialog_device_info, null);
        TextView text_curVersion = (TextView) view.findViewById(C0291R.id.text_curVersion);
        TextView text_uBootVersion = (TextView) view.findViewById(C0291R.id.text_uBootVersion);
        TextView text_kernelVersion = (TextView) view.findViewById(C0291R.id.text_kernelVersion);
        TextView text_rootfsVersion = (TextView) view.findViewById(C0291R.id.text_rootfsVersion);
        LinearLayout linearLayout_lanIP = (LinearLayout) view.findViewById(C0291R.id.linearLayout_lanIP);
        TextView text_lanIP = (TextView) view.findViewById(C0291R.id.text_lanIP);
        ((TextView) view.findViewById(C0291R.id.text_deviceID)).setText(deviceID);
        text_curVersion.setText(curVersion);
        text_uBootVersion.setText(uBootVersion);
        text_kernelVersion.setText(kernelVersion);
        text_rootfsVersion.setText(rootfsVersion);
        if (deviceIP != null) {
            text_lanIP.setText(deviceIP);
        } else {
            linearLayout_lanIP.setVisibility(8);
        }
        view.setOnClickListener(new C05943());
        this.dialog = new Builder(this.context, C0291R.style.hikamDialog).create();
        this.dialog.show();
        this.dialog.setContentView(view);
        LayoutParams layout = (LayoutParams) view.getLayoutParams();
        layout.width = (int) this.context.getResources().getDimension(C0291R.dimen.device_info_dialog_width);
        view.setLayoutParams(layout);
        this.dialog.setOnCancelListener(this.onCancelListener);
        this.dialog.setCanceledOnTouchOutside(true);
        this.dialog.getWindow().setWindowAnimations(C0291R.style.dialog_normal);
    }

    public void showLoadingDialog2() {
        View view = LayoutInflater.from(this.context).inflate(C0291R.layout.dialog_loading2, null);
        this.dialog = new Builder(this.context, C0291R.style.hikamDialog).create();
        this.dialog.show();
        this.dialog.setContentView(view);
        LayoutParams layout = (LayoutParams) view.getLayoutParams();
        layout.width = (int) this.context.getResources().getDimension(C0291R.dimen.Loading_dialog2_width);
        view.setLayoutParams(layout);
        this.dialog.setOnCancelListener(this.onCancelListener);
        this.dialog.setCanceledOnTouchOutside(false);
        this.dialog.getWindow().setWindowAnimations(C0291R.style.dialog_normal);
    }

    public void showNormalDialog() {
        View view = LayoutInflater.from(this.context).inflate(C0291R.layout.dialog_normal, null);
        TextView content = (TextView) view.findViewById(C0291R.id.content_text);
        TextView button1 = (TextView) view.findViewById(C0291R.id.button1_text);
        TextView button2 = (TextView) view.findViewById(C0291R.id.button2_text);
        ((TextView) view.findViewById(C0291R.id.title_text)).setText(this.title_str);
        content.setText(this.content_str);
        content.setMovementMethod(ScrollingMovementMethod.getInstance());
        button1.setText(this.btn1_str);
        button2.setText(this.btn2_str);
        button1.setOnClickListener(new C05954());
        button2.setOnClickListener(new C05965());
        this.dialog = new Builder(this.context, C0291R.style.hikamDialog).create();
        this.dialog.show();
        this.dialog.setContentView(view);
        LayoutParams layout = (LayoutParams) view.getLayoutParams();
        layout.width = (int) this.context.getResources().getDimension(C0291R.dimen.normal_dialog_width);
        view.setLayoutParams(layout);
        this.dialog.setCanceledOnTouchOutside(true);
        this.dialog.getWindow().setWindowAnimations(C0291R.style.dialog_normal);
    }

    public void showSelectorDialog() {
        View view = LayoutInflater.from(this.context).inflate(C0291R.layout.dialog_selector, null);
        ((TextView) view.findViewById(C0291R.id.title_text)).setText(this.title_str);
        ListView content = (ListView) view.findViewById(C0291R.id.content_text);
        content.setAdapter(new SelectorDialogAdapter(this.context, this.list_data));
        content.setOnItemClickListener(this.onItemClickListener);
        this.dialog = new Builder(this.context, C0291R.style.hikamDialog).create();
        this.dialog.show();
        this.dialog.setContentView(view);
        int itemHeight = (int) this.context.getResources().getDimension(C0291R.dimen.selector_dialog_item_height);
        int margin = (int) this.context.getResources().getDimension(C0291R.dimen.selector_dialog_margin);
        int separatorHeight = (int) this.context.getResources().getDimension(C0291R.dimen.selector_dialog_separator_height);
        LayoutParams layout = (LayoutParams) view.getLayoutParams();
        layout.width = (int) this.context.getResources().getDimension(C0291R.dimen.selector_dialog_width);
        layout.height = ((this.list_data.length * itemHeight) + (margin * 2)) + ((this.list_data.length - 1) * separatorHeight);
        view.setLayoutParams(layout);
        this.dialog.setCanceledOnTouchOutside(true);
        this.dialog.setCancelable(true);
        this.dialog.getWindow().setWindowAnimations(C0291R.style.dialog_normal);
    }

    public void showPromptDialog() {
        View view = LayoutInflater.from(this.context).inflate(C0291R.layout.dialog_prompt, null);
        TextView content = (TextView) view.findViewById(C0291R.id.content_text);
        TextView title = (TextView) view.findViewById(C0291R.id.title_text);
        TextView button2 = (TextView) view.findViewById(C0291R.id.button2_text);
        content.setText(this.content_str);
        content.setMovementMethod(ScrollingMovementMethod.getInstance());
        title.setText(this.title_str);
        button2.setOnClickListener(new C05976());
        this.dialog = new Builder(this.context, C0291R.style.hikamDialog).create();
        this.dialog.show();
        this.dialog.setContentView(view);
        LayoutParams layout = (LayoutParams) view.getLayoutParams();
        layout.width = (int) this.context.getResources().getDimension(C0291R.dimen.normal_dialog_width);
        view.setLayoutParams(layout);
        this.dialog.setCancelable(true);
        this.dialog.setCanceledOnTouchOutside(false);
        this.dialog.getWindow().setWindowAnimations(C0291R.style.dialog_normal);
    }

    public void showPromptDialog2() {
        View view = LayoutInflater.from(this.context).inflate(C0291R.layout.dialog_prompt, null);
        TextView title = (TextView) view.findViewById(C0291R.id.title_text);
        TextView button2 = (TextView) view.findViewById(C0291R.id.button2_text);
        ((TextView) view.findViewById(C0291R.id.content_text)).setText(this.content_str);
        title.setText(this.title_str);
        button2.setOnClickListener(new C05987());
        this.dialog = new Builder(this.context, C0291R.style.hikamDialog).create();
        this.dialog.show();
        this.dialog.setContentView(view);
        LayoutParams layout = (LayoutParams) view.getLayoutParams();
        layout.width = (int) this.context.getResources().getDimension(C0291R.dimen.normal_dialog_width);
        view.setLayoutParams(layout);
        this.dialog.setCanceledOnTouchOutside(true);
        this.dialog.setCancelable(true);
        this.dialog.getWindow().setWindowAnimations(C0291R.style.dialog_normal);
    }

    public void showRemindDiaglog(boolean isShow) {
        View view = LayoutInflater.from(this.context).inflate(C0291R.layout.dialog_insert_infrared, null);
        ((Button) view.findViewById(C0291R.id.bt_back)).setOnClickListener(new C05998());
        this.dialog = new Builder(this.context, C0291R.style.hikamDialog).create();
        if (isShow) {
            this.dialog.show();
        }
        this.dialog.setContentView(view);
        this.dialog.setCanceledOnTouchOutside(false);
        this.dialog.setCancelable(false);
        LayoutParams layout = (LayoutParams) view.getLayoutParams();
        layout.width = (int) this.context.getResources().getDimension(C0291R.dimen.dialog_remind_width);
        layout.height = (int) this.context.getResources().getDimension(C0291R.dimen.dialog_reming_height);
        view.setLayoutParams(layout);
        this.dialog.getWindow().setWindowAnimations(C0291R.style.dialog_normal);
    }

    public void showPromoptDiaglog() {
        View view = LayoutInflater.from(this.context).inflate(C0291R.layout.dialog_promopt_box2, null);
        view.setOnClickListener(new C06009());
        ((Button) view.findViewById(C0291R.id.bt_determine)).setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                if (NormalDialog.this.dialog != null) {
                    NormalDialog.this.dialog.dismiss();
                }
            }
        });
        this.dialog = new Builder(this.context, C0291R.style.hikamDialog).create();
        this.dialog.show();
        this.dialog.setContentView(view);
        this.dialog.setCanceledOnTouchOutside(true);
        LayoutParams layout = (LayoutParams) view.getLayoutParams();
        layout.width = (int) this.context.getResources().getDimension(C0291R.dimen.dialog_promopt_width);
        layout.height = (int) this.context.getResources().getDimension(C0291R.dimen.dialog_promopt_height);
        view.setLayoutParams(layout);
        this.dialog.setCanceledOnTouchOutside(true);
        this.dialog.getWindow().setWindowAnimations(C0291R.style.dialog_normal);
    }

    public void showListenDialog() {
        View view = LayoutInflater.from(this.context).inflate(C0291R.layout.dialog_listen, null);
        view.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                if (NormalDialog.this.dialog != null) {
                    NormalDialog.this.dialog.dismiss();
                }
            }
        });
        Button bt2 = (Button) view.findViewById(C0291R.id.bt_no_hear);
        ((Button) view.findViewById(C0291R.id.bt_hear)).setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                NormalDialog.this.dialog.dismiss();
                Intent it = new Intent();
                it.setAction(Action.HEARED);
                NormalDialog.this.context.sendBroadcast(it);
            }
        });
        bt2.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                NormalDialog.this.dialog.dismiss();
            }
        });
        ((AnimationDrawable) ((ImageView) view.findViewById(C0291R.id.anim_load)).getDrawable()).start();
        this.dialog = new Builder(this.context, C0291R.style.hikamDialog).create();
        this.dialog.show();
        this.dialog.setContentView(view);
        this.dialog.setCanceledOnTouchOutside(true);
        LayoutParams layout = (LayoutParams) view.getLayoutParams();
        layout.width = (int) this.context.getResources().getDimension(C0291R.dimen.dialog_remind_width);
        layout.height = (int) this.context.getResources().getDimension(C0291R.dimen.dialog_reming_height);
        view.setLayoutParams(layout);
        this.dialog.getWindow().setWindowAnimations(C0291R.style.dialog_normal);
    }

    public void showWaitConnectionDialog() {
        View view = LayoutInflater.from(this.context).inflate(C0291R.layout.dialog_wait_connection, null);
        ((AnimationDrawable) ((ImageView) view.findViewById(C0291R.id.anim_wait)).getDrawable()).start();
        this.dialog = new Builder(this.context, C0291R.style.hikamDialog).create();
        this.dialog.show();
        this.dialog.setContentView(view);
        this.dialog.setCanceledOnTouchOutside(false);
        LayoutParams layout = (LayoutParams) view.getLayoutParams();
        layout.width = (int) this.context.getResources().getDimension(C0291R.dimen.dialog_remind_width);
        layout.height = (int) this.context.getResources().getDimension(C0291R.dimen.dialog_reming_height);
        view.setLayoutParams(layout);
        this.dialog.getWindow().setWindowAnimations(C0291R.style.dialog_normal);
    }

    public void showQRcodehelp() {
        View view = LayoutInflater.from(this.context).inflate(C0291R.layout.dialog_help, null);
        view.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                if (NormalDialog.this.dialog != null) {
                    NormalDialog.this.dialog.dismiss();
                }
            }
        });
        this.dialog = new Builder(this.context, C0291R.style.hikamDialog).create();
        this.dialog.show();
        this.dialog.setContentView(view);
        this.dialog.setCanceledOnTouchOutside(true);
        LayoutParams layout = (LayoutParams) view.getLayoutParams();
        layout.width = (int) this.context.getResources().getDimension(C0291R.dimen.dialog_remind_width);
        layout.height = (int) this.context.getResources().getDimension(C0291R.dimen.dialog_reming_height);
        view.setLayoutParams(layout);
        this.dialog.getWindow().setWindowAnimations(C0291R.style.dialog_normal);
    }

    public void successDialog() {
        View view = LayoutInflater.from(this.context).inflate(C0291R.layout.dialog_success, null);
        view.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                if (NormalDialog.this.dialog != null) {
                    NormalDialog.this.dialog.dismiss();
                }
            }
        });
        this.dialog = new Builder(this.context, C0291R.style.hikamDialog).create();
        this.dialog.show();
        this.dialog.setContentView(view);
        this.dialog.setCanceledOnTouchOutside(true);
        LayoutParams layout = (LayoutParams) view.getLayoutParams();
        layout.width = (int) this.context.getResources().getDimension(C0291R.dimen.dialog_success_width);
        layout.height = (int) this.context.getResources().getDimension(C0291R.dimen.dialog_success_height);
        view.setLayoutParams(layout);
        this.dialog.getWindow().setWindowAnimations(C0291R.style.dialog_normal);
    }

    public void faildDialog() {
        View view = LayoutInflater.from(this.context).inflate(C0291R.layout.dialog_prompt_box1, null);
        view.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                if (NormalDialog.this.dialog != null) {
                    NormalDialog.this.dialog.dismiss();
                }
            }
        });
        ((Button) view.findViewById(C0291R.id.bt_determine)).setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                if (NormalDialog.this.dialog != null) {
                    NormalDialog.this.dialog.dismiss();
                }
            }
        });
        this.dialog = new Builder(this.context, C0291R.style.hikamDialog).create();
        this.dialog.show();
        this.dialog.setContentView(view);
        this.dialog.setCanceledOnTouchOutside(true);
        LayoutParams layout = (LayoutParams) view.getLayoutParams();
        layout.width = (int) this.context.getResources().getDimension(C0291R.dimen.dialog_promopt_width);
        layout.height = (int) this.context.getResources().getDimension(C0291R.dimen.dialog_promopt_height);
        view.setLayoutParams(layout);
        this.dialog.getWindow().setWindowAnimations(C0291R.style.dialog_normal);
    }

    public void showSmartscanFail() {
        View view = LayoutInflater.from(this.context).inflate(C0291R.layout.dialog_connect_failed, null);
        view.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
            }
        });
        ((Button) view.findViewById(C0291R.id.try_again)).setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                if (NormalDialog.this.dialog != null) {
                    NormalDialog.this.dialog.dismiss();
                }
            }
        });
        this.dialog = new Builder(this.context, C0291R.style.hikamDialog).create();
        this.dialog.show();
        this.dialog.setContentView(view);
        this.dialog.setCanceledOnTouchOutside(false);
        LayoutParams layout = (LayoutParams) view.getLayoutParams();
        layout.width = (int) this.context.getResources().getDimension(C0291R.dimen.dialog_remind_width);
        layout.height = (int) this.context.getResources().getDimension(C0291R.dimen.dialog_reming_height);
        view.setLayoutParams(layout);
        this.dialog.getWindow().setWindowAnimations(C0291R.style.dialog_normal);
    }

    public void showAirlinkFail() {
        View view = LayoutInflater.from(this.context).inflate(C0291R.layout.dialog_airlink_connect_failed, null);
        view.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
            }
        });
        ((Button) view.findViewById(C0291R.id.check_fq)).setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                if (NormalDialog.this.dialog != null) {
                    NormalDialog.this.dialog.dismiss();
                }
                NormalDialog.this.context.startActivity(new Intent(NormalDialog.this.context, HKWebView.class));
            }
        });
        ((Button) view.findViewById(C0291R.id.try_again)).setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                if (NormalDialog.this.dialog != null) {
                    NormalDialog.this.dialog.dismiss();
                }
            }
        });
        this.dialog = new Builder(this.context, C0291R.style.hikamDialog).create();
        this.dialog.show();
        this.dialog.setContentView(view);
        this.dialog.setCanceledOnTouchOutside(false);
        LayoutParams layout = (LayoutParams) view.getLayoutParams();
        layout.width = (int) this.context.getResources().getDimension(C0291R.dimen.dialog_remind_width);
        layout.height = (int) this.context.getResources().getDimension(C0291R.dimen.dialog_reming_height);
        view.setLayoutParams(layout);
        this.dialog.getWindow().setWindowAnimations(C0291R.style.dialog_normal);
    }

    public void setTitle(String title) {
        this.title_str = title;
    }

    public void setTitle(int id) {
        this.title_str = this.context.getResources().getString(id);
    }

    public void setListData(String[] data) {
        this.list_data = data;
    }

    public void setCanceledOnTouchOutside(boolean bool) {
        this.dialog.setCanceledOnTouchOutside(bool);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setCancelable(boolean bool) {
        this.dialog.setCancelable(bool);
    }

    public void cancel() {
        this.dialog.cancel();
    }

    public void dismiss() {
        this.dialog.dismiss();
    }

    public boolean isShowing() {
        return this.dialog.isShowing();
    }

    public void setBtnListener(TextView btn1, TextView btn2) {
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public void setOnButtonOkListener(OnButtonOkListener onButtonOkListener) {
        this.onButtonOkListener = onButtonOkListener;
    }

    public void setOnButtonCancelListener(OnButtonCancelListener onButtonCancelListener) {
        this.onButtonCancelListener = onButtonCancelListener;
    }

    public void setOnCancelListener(OnCancelListener onCancelListener) {
        this.onCancelListener = onCancelListener;
    }
}
