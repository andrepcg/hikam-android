package com.jwkj.widget.album;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.hikam.C0291R;

public class AlbumPagerFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private SmartImageView img;
    private String path;

    public static AlbumPagerFragment newInstance(String param1) {
        AlbumPagerFragment fragment = new AlbumPagerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.path = getArguments().getString(ARG_PARAM1);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(C0291R.layout.fragment_album_pager, container, false);
        this.img = (SmartImageView) view.findViewById(C0291R.id.img);
        this.img.setImageBitmap(getImage(this.path));
        return view;
    }

    public Bitmap getImage(String path) {
        return BitmapFactory.decodeFile(path);
    }
}
