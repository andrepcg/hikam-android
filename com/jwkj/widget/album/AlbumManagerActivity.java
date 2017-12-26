package com.jwkj.widget.album;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.hikam.C0291R;
import com.jwkj.NativePlayerActivity;
import com.jwkj.activity.BaseActivity;
import com.jwkj.entity.MediaPacket;
import com.jwkj.widget.album.AlbumAdapter.OnEditorListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.cookie.ClientCookie;

public class AlbumManagerActivity extends BaseActivity implements OnClickListener {
    private AlbumAdapter adapter;
    private ImageView back_btn;
    private ImageView cancel_btn;
    private Context context;
    private ImageView delete_btn;
    private GridView gridView;
    private ImageView img_all;
    private ImageView img_back;
    private boolean isAll = false;
    private boolean isEditor = false;
    private boolean isGallery = false;
    private RelativeLayout layout_editor;
    private RelativeLayout layout_title;
    private LinearLayout ll_all;
    private AlbumPagerAdapter pagerAdapter;
    private RelativeLayout rl_gallery;
    private ViewPager vp;

    class C11331 implements OnEditorListener {
        C11331() {
        }

        public void onExit() {
            AlbumManagerActivity.this.exitEditor();
        }

        public void onEnter() {
            AlbumManagerActivity.this.enterEditor();
        }

        public void enterGallery(int position) {
            ArrayList<MediaPacket> list = AlbumManagerActivity.this.adapter.getMediaList();
            Intent intent = new Intent(AlbumManagerActivity.this, AlbumGalleryActivity.class);
            intent.putExtra("list", list);
            intent.putExtra("index", position);
            AlbumManagerActivity.this.startActivity(intent);
        }

        public void enterMovie(String path) {
            Intent intent = new Intent(AlbumManagerActivity.this.context, NativePlayerActivity.class);
            intent.putExtra(ClientCookie.PATH_ATTR, path);
            AlbumManagerActivity.this.startActivity(intent);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0291R.layout.activity_album_manager);
        initView();
        this.context = this;
    }

    private void initView() {
        this.layout_title = (RelativeLayout) findViewById(C0291R.id.layout_title);
        this.layout_editor = (RelativeLayout) findViewById(C0291R.id.layout_editor);
        this.rl_gallery = (RelativeLayout) findViewById(C0291R.id.rl_gallery);
        this.delete_btn = (ImageView) findViewById(C0291R.id.delete_btn);
        this.delete_btn.setOnClickListener(this);
        this.back_btn = (ImageView) findViewById(C0291R.id.back_btn);
        this.back_btn.setOnClickListener(this);
        this.cancel_btn = (ImageView) findViewById(C0291R.id.cancel_btn);
        this.cancel_btn.setOnClickListener(this);
        this.img_all = (ImageView) findViewById(C0291R.id.img_all);
        this.img_back = (ImageView) findViewById(C0291R.id.img_back);
        this.img_back.setOnClickListener(this);
        this.ll_all = (LinearLayout) findViewById(C0291R.id.ll_all);
        this.ll_all.setOnClickListener(this);
        this.gridView = (GridView) findViewById(C0291R.id.gridView);
        this.vp = (ViewPager) findViewById(C0291R.id.vp);
        this.adapter = new AlbumAdapter(this);
        this.adapter.setOnEditorListener(new C11331());
        this.pagerAdapter = new AlbumPagerAdapter(getSupportFragmentManager(), getPagerList());
        this.vp.setAdapter(this.pagerAdapter);
        this.gridView.setAdapter(this.adapter);
    }

    private List<Fragment> getPagerList() {
        List<Fragment> list = new ArrayList();
        File[] fileList = this.adapter.getFileList();
        int len = fileList.length;
        for (int i = 0; i < len; i++) {
            list.add(i, AlbumPagerFragment.newInstance(fileList[i].getPath()));
        }
        return list;
    }

    private void enterEditor() {
        this.isEditor = true;
        this.layout_title.setVisibility(8);
        this.layout_editor.setVisibility(0);
    }

    private void exitEditor() {
        this.isEditor = false;
        this.layout_title.setVisibility(0);
        this.layout_editor.setVisibility(8);
    }

    public int getActivityInfo() {
        return 101;
    }

    public void onBackPressed() {
        if (this.isGallery) {
            this.isGallery = false;
            this.rl_gallery.setVisibility(8);
        } else if (this.isEditor) {
            this.adapter.exitEditor();
        } else {
            super.onBackPressed();
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case C0291R.id.back_btn:
                finish();
                return;
            case C0291R.id.cancel_btn:
                this.adapter.exitEditor();
                return;
            case C0291R.id.delete_btn:
                this.adapter.delete();
                return;
            case C0291R.id.img_back:
                this.isGallery = false;
                this.rl_gallery.setVisibility(8);
                return;
            case C0291R.id.ll_all:
                if (this.isAll) {
                    this.isAll = false;
                    this.adapter.unselectAll();
                    this.img_all.setImageDrawable(getResources().getDrawable(C0291R.drawable.check_off));
                    return;
                }
                this.isAll = true;
                this.adapter.selectAll();
                this.img_all.setImageDrawable(getResources().getDrawable(C0291R.drawable.check_on));
                return;
            default:
                return;
        }
    }
}
