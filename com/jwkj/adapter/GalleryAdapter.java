package com.jwkj.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import com.hikam.C0291R;
import com.jwkj.utils.Utils;
import com.jwkj.widget.MyImageView;
import java.io.File;
import java.io.FileFilter;

public class GalleryAdapter extends BaseAdapter {
    Context context;
    File[] data = new File(Environment.getExternalStorageDirectory().getPath() + "/screenshot").listFiles(new C04661());
    int screenWidth;
    int selectedItemId;

    class C04661 implements FileFilter {
        C04661() {
        }

        public boolean accept(File pathname) {
            if (pathname.getName().endsWith(".jpg")) {
                return true;
            }
            return false;
        }
    }

    public GalleryAdapter(Context context, int screenWidth) {
        this.context = context;
        this.screenWidth = screenWidth;
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

    public void setSelectedItem(int selectedItemId) {
        if (this.selectedItemId != selectedItemId) {
            this.selectedItemId = selectedItemId;
            notifyDataSetChanged();
        }
    }

    public View getView(int position, View arg1, ViewGroup arg2) {
        RelativeLayout view = (RelativeLayout) arg1;
        if (view == null) {
            view = (RelativeLayout) LayoutInflater.from(this.context).inflate(C0291R.layout.list_imgbrowser_item, null);
        }
        String path = this.data[position].getPath();
        MyImageView img = (MyImageView) view.findViewById(C0291R.id.img);
        LayoutParams params = (LayoutParams) img.getLayoutParams();
        params.width = this.screenWidth / 5;
        params.height = this.screenWidth / 5;
        img.setLayoutParams(params);
        if (this.selectedItemId == position) {
            img.setImageBitmap(Utils.montageBitmap(BitmapFactory.decodeResource(this.context.getResources(), C0291R.drawable.frame), BitmapFactory.decodeFile(path), 200, 200));
            int selectedWidth = (int) (((double) (this.screenWidth / 5)) * 1.4d);
            LayoutParams selectedParams = (LayoutParams) img.getLayoutParams();
            selectedParams.width = selectedWidth;
            selectedParams.height = selectedWidth;
            img.setLayoutParams(selectedParams);
        } else {
            img.setImageBitmap(BitmapFactory.decodeFile(path));
            img.setLayoutParams(params);
        }
        Log.e("my", Runtime.getRuntime().totalMemory() + "");
        return view;
    }

    public void updateData(File[] files) {
        this.data = files;
        notifyDataSetChanged();
    }
}
