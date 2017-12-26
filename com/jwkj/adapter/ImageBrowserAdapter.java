package com.jwkj.adapter;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import com.hikam.C0291R;
import com.jwkj.activity.ImageBrowser;
import com.jwkj.widget.MyImageView;
import com.jwkj.widget.NormalDialog;
import com.jwkj.widget.NormalDialog.OnButtonOkListener;
import java.io.File;
import java.io.FileFilter;

public class ImageBrowserAdapter extends BaseAdapter {
    Context context;
    File[] data = new File(Environment.getExternalStorageDirectory().getPath() + "/screenshot").listFiles(new C04671());

    class C04671 implements FileFilter {
        C04671() {
        }

        public boolean accept(File pathname) {
            if (pathname.getName().endsWith(".jpg")) {
                return true;
            }
            return false;
        }
    }

    class C04704 implements FileFilter {
        C04704() {
        }

        public boolean accept(File pathname) {
            if (pathname.getName().endsWith(".jpg")) {
                return true;
            }
            return false;
        }
    }

    public ImageBrowserAdapter(Context context) {
        this.context = context;
        if (this.data == null) {
            this.data = new File[0];
        }
    }

    public int getCount() {
        return this.data.length;
    }

    public Object getItem(int arg0) {
        return null;
    }

    public long getItemId(int arg0) {
        return 0;
    }

    public View getView(final int arg0, View arg1, ViewGroup arg2) {
        RelativeLayout view = (RelativeLayout) arg1;
        if (view == null) {
            view = (RelativeLayout) LayoutInflater.from(this.context).inflate(C0291R.layout.list_imgbrowser_item, null);
        }
        ((MyImageView) view.findViewById(C0291R.id.img)).setImageFilePath(this.data[arg0].getPath());
        view.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                ((ImageBrowser) ImageBrowserAdapter.this.context).createGalleryDialog(arg0);
            }
        });
        view.setOnLongClickListener(new OnLongClickListener() {

            class C10951 implements OnButtonOkListener {
                C10951() {
                }

                public void onClick() {
                    try {
                        ImageBrowserAdapter.this.data[arg0].delete();
                        ImageBrowserAdapter.this.updateData();
                    } catch (Exception e) {
                        Log.e("my", "delete file error->ImageBrowserAdapter.java");
                    }
                }
            }

            public boolean onLongClick(View view) {
                NormalDialog dialog = new NormalDialog(ImageBrowserAdapter.this.context, ImageBrowserAdapter.this.context.getResources().getString(C0291R.string.delete), ImageBrowserAdapter.this.context.getResources().getString(C0291R.string.confirm_delete), ImageBrowserAdapter.this.context.getResources().getString(C0291R.string.delete), ImageBrowserAdapter.this.context.getResources().getString(C0291R.string.cancel));
                dialog.setOnButtonOkListener(new C10951());
                dialog.showDialog();
                return true;
            }
        });
        Log.e("my", Runtime.getRuntime().totalMemory() + "");
        return view;
    }

    public void updateData() {
        this.data = new File(Environment.getExternalStorageDirectory().getPath() + "/screenshot").listFiles(new C04704());
        notifyDataSetChanged();
    }
}
