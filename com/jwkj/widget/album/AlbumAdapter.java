package com.jwkj.widget.album;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.hikam.C0291R;
import com.jwkj.entity.MediaPacket;
import com.jwkj.entity.MediaPacket.TYPE;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class AlbumAdapter extends BaseAdapter {
    private static final int MODE_BROWSE = 0;
    private static final int MODE_EDITOR = 1;
    public static final int TYPE_AUDIO = 2;
    public static final int TYPE_MEDIA = 4;
    public static final int TYPE_PICTURE = 1;
    public static final int TYPE_VIDEO = 3;
    private ArrayList<MediaPacket> albumList = new ArrayList();
    private List<Boolean> checkList = new ArrayList();
    private Context context;
    private File[] imageList;
    private int mode = 0;
    private OnEditorListener onEditorListener;
    private File[] recordList;

    class C06173 implements FileFilter {
        C06173() {
        }

        public boolean accept(File pathname) {
            if (pathname.getName().endsWith(".jpg")) {
                return true;
            }
            return false;
        }
    }

    class C06184 implements FileFilter {
        C06184() {
        }

        public boolean accept(File pathname) {
            if (pathname.getName().endsWith(".jpg")) {
                return true;
            }
            return false;
        }
    }

    public class FileComparator implements Comparator<MediaPacket> {
        public int compare(MediaPacket file1, MediaPacket file2) {
            if (file1.getLastModified() < file2.getLastModified()) {
                return 1;
            }
            return -1;
        }
    }

    public interface OnEditorListener {
        void enterGallery(int i);

        void enterMovie(String str);

        void onEnter();

        void onExit();
    }

    static class ViewHolder {
        ImageView imageView;
        ImageView imgPlay;
        ImageView tag;

        ViewHolder(View view) {
            this.imageView = (ImageView) view.findViewById(C0291R.id.img);
            this.tag = (ImageView) view.findViewById(C0291R.id.tag);
            this.imgPlay = (ImageView) view.findViewById(C0291R.id.img_play);
        }
    }

    public void setOnEditorListener(OnEditorListener onEditorListener) {
        this.onEditorListener = onEditorListener;
    }

    public AlbumAdapter(Context context) {
        this.context = context;
        initDataSouce();
    }

    public int getCount() {
        return this.albumList.size();
    }

    public Object getItem(int position) {
        return this.albumList.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public int getItemViewType(int position) {
        if (TYPE.PICTURE == ((MediaPacket) this.albumList.get(position)).getMediaType()) {
            return 1;
        }
        return 4;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(this.context).inflate(C0291R.layout.item_album, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Glide.with(this.context).load(((MediaPacket) this.albumList.get(position)).getPicPath()).into(holder.imageView);
        holder.imageView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                switch (AlbumAdapter.this.mode) {
                    case 0:
                        if (AlbumAdapter.this.onEditorListener != null) {
                            AlbumAdapter.this.onEditorListener.enterGallery(position);
                            return;
                        }
                        return;
                    case 1:
                        AlbumAdapter.this.checkSwitch(position, holder);
                        return;
                    default:
                        return;
                }
            }
        });
        holder.imageView.setOnLongClickListener(new OnLongClickListener() {
            public boolean onLongClick(View v) {
                switch (AlbumAdapter.this.mode) {
                    case 0:
                        AlbumAdapter.this.enterEditor(position);
                        break;
                }
                return false;
            }
        });
        if (getItemViewType(position) == 4) {
            holder.imgPlay.setVisibility(0);
        } else {
            holder.imgPlay.setVisibility(8);
        }
        if (this.mode == 1) {
            holder.tag.setVisibility(0);
            if (((Boolean) this.checkList.get(position)).booleanValue()) {
                holder.tag.setImageResource(C0291R.drawable.check_on);
            } else {
                holder.tag.setImageResource(C0291R.drawable.check_off);
            }
        } else {
            holder.tag.setVisibility(8);
        }
        return convertView;
    }

    private void checkSwitch(int position, ViewHolder holder) {
        if (((Boolean) this.checkList.get(position)).booleanValue()) {
            holder.tag.setImageResource(C0291R.drawable.check_off);
            this.checkList.set(position, Boolean.valueOf(false));
            return;
        }
        holder.tag.setImageResource(C0291R.drawable.check_on);
        this.checkList.set(position, Boolean.valueOf(true));
    }

    private void enterEditor(int position) {
        this.mode = 1;
        this.checkList.clear();
        int size = this.albumList.size();
        for (int i = 0; i < size; i++) {
            if (i == position) {
                this.checkList.add(Boolean.valueOf(true));
            } else {
                this.checkList.add(Boolean.valueOf(false));
            }
        }
        if (this.onEditorListener != null) {
            this.onEditorListener.onEnter();
        }
        notifyDataSetChanged();
    }

    public void exitEditor() {
        this.mode = 0;
        this.checkList.clear();
        if (this.onEditorListener != null) {
            this.onEditorListener.onExit();
        }
        notifyDataSetChanged();
    }

    public void delete() {
        Exception e;
        int size = this.checkList.size();
        for (int i = 0; i < size; i++) {
            if (((Boolean) this.checkList.get(i)).booleanValue()) {
                String picPath = ((MediaPacket) this.albumList.get(i)).getPicPath();
                new File(picPath).delete();
                if (((MediaPacket) this.albumList.get(i)).getMediaType() == TYPE.MEDIA) {
                    String prefix = picPath.substring(0, picPath.length() - 4);
                    try {
                        File file = new File(prefix + ".pcm");
                        try {
                            file.delete();
                            new File(prefix + ".aac").delete();
                            file = new File(prefix + ".h264");
                            file.delete();
                            new File(prefix + ".mp4").delete();
                        } catch (Exception e2) {
                            e = e2;
                            File file2 = file;
                            Log.e("FileExp", e.toString());
                        }
                    } catch (Exception e3) {
                        e = e3;
                    }
                }
            }
        }
        this.imageList = null;
        this.recordList = null;
        this.albumList.clear();
        initDataSouce();
        exitEditor();
    }

    public void selectAll() {
        int size = this.checkList.size();
        for (int i = 0; i < size; i++) {
            this.checkList.set(i, Boolean.valueOf(true));
        }
        notifyDataSetChanged();
    }

    public void unselectAll() {
        int size = this.checkList.size();
        for (int i = 0; i < size; i++) {
            this.checkList.set(i, Boolean.valueOf(false));
        }
        notifyDataSetChanged();
    }

    private void initDataSouce() {
        initImageList();
        initRecordList();
        Collections.sort(this.albumList, new FileComparator());
    }

    private void initImageList() {
        this.imageList = new File(Environment.getExternalStorageDirectory().getPath() + "/screenshot").listFiles(new C06173());
        if (this.imageList != null) {
            for (File item : this.imageList) {
                MediaPacket mp = new MediaPacket();
                mp.setMediaType(TYPE.PICTURE);
                mp.setPicPath(item.getPath().toString());
                mp.setLastModified(item.lastModified());
                this.albumList.add(mp);
            }
            if (this.imageList == null) {
                this.imageList = new File[0];
            }
        }
    }

    private void initRecordList() {
        this.recordList = new File(Environment.getExternalStorageDirectory().getPath() + "/hikam_record").listFiles(new C06184());
        if (this.recordList != null) {
            for (File item : this.recordList) {
                String picPath = item.getPath().toString();
                String prefix = picPath.substring(0, picPath.length() - 4);
                MediaPacket mp = new MediaPacket();
                mp.setMediaType(TYPE.MEDIA);
                mp.setPicPath(picPath);
                StringBuilder sb = new StringBuilder();
                sb.append(prefix);
                sb.append(".mp4");
                mp.setMediaPath(sb.toString());
                mp.setLastModified(item.lastModified());
                this.albumList.add(mp);
            }
            if (this.recordList == null) {
                this.recordList = new File[0];
            }
        }
    }

    public File[] getFileList() {
        int size = 0;
        Iterator it = this.albumList.iterator();
        while (it.hasNext()) {
            if (((MediaPacket) it.next()).getMediaType() == TYPE.PICTURE) {
                size++;
            }
        }
        File[] imageList = new File[size];
        int i = 0;
        it = this.albumList.iterator();
        while (it.hasNext()) {
            MediaPacket item = (MediaPacket) it.next();
            if (item.getMediaType() == TYPE.PICTURE) {
                imageList[i] = new File(item.getPicPath());
                i++;
            }
        }
        return imageList;
    }

    public ArrayList<MediaPacket> getMediaList() {
        return this.albumList;
    }
}
