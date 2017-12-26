package com.jwkj.widget.album;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.hikam.C0291R;
import com.jwkj.activity.BaseActivity;
import com.jwkj.entity.MediaPacket;
import com.jwkj.entity.MediaPacket.TYPE;
import com.jwkj.utils.C0568T;
import com.jwkj.utils.Utils;
import java.util.ArrayList;
import java.util.List;

@SuppressLint({"NewApi"})
public class AlbumGalleryActivity extends BaseActivity implements OnClickListener {
    private int autoPlayIndex = -1;
    private Context context;
    private int currentIndex = 0;
    private ImageView img_back;
    private boolean isAutoPlay = false;
    private List<Fragment> list;
    private ArrayList<MediaPacket> mediaList;
    private AlbumPagerAdapter pagerAdapter;
    private TextView tv_save;
    private ViewPager vp;

    class C11321 implements OnPageChangeListener {
        C11321() {
        }

        public void onPageSelected(int arg0) {
            AlbumGalleryActivity.this.currentIndex = arg0;
            if (((MediaPacket) AlbumGalleryActivity.this.mediaList.get(AlbumGalleryActivity.this.currentIndex)).getMediaType() == TYPE.PICTURE) {
                AlbumGalleryActivity.this.tv_save.setVisibility(0);
            } else {
                AlbumGalleryActivity.this.tv_save.setVisibility(8);
            }
        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        public void onPageScrollStateChanged(int arg0) {
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0291R.layout.activity_album_gallery);
        this.context = this;
        this.currentIndex = getIntent().getIntExtra("index", 0);
        this.mediaList = (ArrayList) getIntent().getSerializableExtra("list");
        if (this.mediaList == null || this.mediaList.size() <= 0) {
            finish();
        }
        if (((MediaPacket) this.mediaList.get(this.currentIndex)).getMediaType() == TYPE.MEDIA) {
            this.isAutoPlay = true;
        }
        initView();
    }

    private void initView() {
        this.img_back = (ImageView) findViewById(C0291R.id.img_back);
        this.tv_save = (TextView) findViewById(C0291R.id.tv_save);
        this.img_back.setOnClickListener(this);
        this.tv_save.setOnClickListener(this);
        if (((MediaPacket) this.mediaList.get(this.currentIndex)).getMediaType() == TYPE.PICTURE) {
            this.tv_save.setVisibility(0);
        } else {
            this.tv_save.setVisibility(8);
            this.autoPlayIndex = this.currentIndex;
        }
        this.vp = (ViewPager) findViewById(C0291R.id.vp);
        this.pagerAdapter = new AlbumPagerAdapter(getSupportFragmentManager(), getPagerList());
        this.vp.setAdapter(this.pagerAdapter);
        this.vp.addOnPageChangeListener(new C11321());
        this.vp.setCurrentItem(this.currentIndex);
    }

    private List<Fragment> getPagerList() {
        if (this.list != null) {
            this.list.clear();
        } else {
            this.list = new ArrayList();
        }
        int len = this.mediaList.size();
        for (int i = 0; i < len; i++) {
            if (((MediaPacket) this.mediaList.get(i)).getMediaType() == TYPE.PICTURE) {
                this.list.add(i, AlbumPagerFragment.newInstance(((MediaPacket) this.mediaList.get(i)).getPicPath()));
            } else if (this.autoPlayIndex == i) {
                Log.e("few", "ch auto3 " + this.autoPlayIndex);
                this.list.add(i, AlbumPagerVideoFragment.newInstance((MediaPacket) this.mediaList.get(i), true));
            } else {
                Log.e("few", "ch auto4 " + this.autoPlayIndex);
                this.list.add(i, AlbumPagerVideoFragment.newInstance((MediaPacket) this.mediaList.get(i), false));
            }
        }
        return this.list;
    }

    public int getActivityInfo() {
        return 102;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case C0291R.id.img_back:
                finish();
                return;
            case C0291R.id.tv_save:
                save();
                return;
            default:
                return;
        }
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() != 25 && event.getKeyCode() != 24) {
            return super.dispatchKeyEvent(event);
        }
        if (this.list != null && (this.list.get(this.vp.getCurrentItem()) instanceof AlbumPagerVideoFragment)) {
            ((AlbumPagerVideoFragment) this.list.get(this.vp.getCurrentItem())).onAudioKeyDown(event.getAction(), event.getKeyCode());
        }
        return super.dispatchKeyEvent(event);
    }

    public void save() {
        TYPE type = ((MediaPacket) this.mediaList.get(this.currentIndex)).getMediaType();
        if (type == TYPE.PICTURE) {
            saveImage(((MediaPacket) this.mediaList.get(this.currentIndex)).getPicPath());
        } else if (type != TYPE.MEDIA) {
        }
    }

    public void saveImage(String path) {
        String savePath = Utils.getPathFromUri(this, Uri.parse(Media.insertImage(getContentResolver(), BitmapFactory.decodeFile(path), "", "")));
        C0568T.showShort((Context) this, (int) C0291R.string.saveto_success);
        sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.parse("file://" + savePath)));
    }

    public void saveMp4() {
    }
}
