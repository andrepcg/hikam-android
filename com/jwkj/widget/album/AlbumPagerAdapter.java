package com.jwkj.widget.album;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import java.util.List;

public class AlbumPagerAdapter extends FragmentStatePagerAdapter {
    List<Fragment> mList;

    public AlbumPagerAdapter(FragmentManager fm, List<Fragment> list) {
        super(fm);
        this.mList = list;
    }

    public Fragment getItem(int position) {
        return (Fragment) this.mList.get(position);
    }

    public int getCount() {
        return this.mList.size();
    }

    public int getItemPosition(Object object) {
        return -2;
    }

    public CharSequence getPageTitle(int position) {
        return "11";
    }
}
