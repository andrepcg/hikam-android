package com.jwkj;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import com.hikam.C0291R;
import com.jwkj.widget.StarLinesView;
import org.apache.http.cookie.ClientCookie;

public class StarLinesActivity extends Activity implements OnClickListener {
    private ImageView img;
    private ImageView imgLeft;
    private ImageView imgReset;
    private ImageView imgRight;
    private String path;
    private StarLinesView slView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0291R.layout.activity_star_lines);
        this.path = getIntent().getStringExtra(ClientCookie.PATH_ATTR);
        init();
    }

    private void init() {
        this.img = (ImageView) findViewById(C0291R.id.img);
        this.imgLeft = (ImageView) findViewById(C0291R.id.img_left);
        this.imgRight = (ImageView) findViewById(C0291R.id.img_right);
        this.imgReset = (ImageView) findViewById(C0291R.id.img_reset);
        this.imgLeft.setOnClickListener(this);
        this.imgRight.setOnClickListener(this);
        this.imgReset.setOnClickListener(this);
        this.img.setImageBitmap(getImage(this.path));
        this.slView = (StarLinesView) findViewById(C0291R.id.slView);
    }

    public Bitmap getImage(String path) {
        return BitmapFactory.decodeFile(path);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case C0291R.id.img_left:
                this.slView.pop();
                return;
            case C0291R.id.img_reset:
                this.slView.reset();
                return;
            case C0291R.id.img_right:
                this.slView.backup();
                return;
            default:
                return;
        }
    }
}
