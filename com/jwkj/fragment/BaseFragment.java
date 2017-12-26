package com.jwkj.fragment;

import android.support.v4.app.Fragment;
import com.jwkj.global.MyApp;

public class BaseFragment extends Fragment {
    private boolean isRun = false;

    public void onPause() {
        super.onPause();
        this.isRun = false;
    }

    public void onResume() {
        super.onResume();
        this.isRun = true;
    }

    public boolean getIsRun() {
        return this.isRun;
    }

    public void onDestroy() {
        super.onDestroy();
        MyApp.getRefWatcher(getActivity()).watch(this);
    }
}
