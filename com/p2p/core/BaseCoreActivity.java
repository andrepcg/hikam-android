package com.p2p.core;

import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.p2p.core.utils.HomeWatcher;
import com.p2p.core.utils.OnHomePressedListener;
import java.util.HashMap;

public abstract class BaseCoreActivity extends FragmentActivity implements OnHomePressedListener {
    public static HashMap<Integer, Integer> activity_stack = new HashMap();
    private boolean isGoExit = false;
    public HomeWatcher mHomeWatcher;

    public abstract int getActivityInfo();

    protected abstract void onExit();

    protected abstract void onGoBack();

    protected abstract void onGoFront();

    private void onStackChange() {
        int start = 0;
        int stop = 0;
        for (Integer key : activity_stack.keySet()) {
            int status = ((Integer) activity_stack.get(key)).intValue();
            if (status == 0) {
                start++;
            } else if (status == 1) {
                stop++;
            }
        }
        if (activity_stack.size() <= 0 || start != 0) {
            if (activity_stack.size() > 0 && start > 0) {
                onGoFront();
            }
        } else if (!this.isGoExit) {
            onGoBack();
        }
        Log.e("my", "stack size:" + activity_stack.size() + "    start:" + start + "  stop:" + stop);
    }

    public static boolean isBG() {
        int start = 0;
        int stop = 0;
        for (Integer key : activity_stack.keySet()) {
            int status = ((Integer) activity_stack.get(key)).intValue();
            if (status == 0) {
                start++;
            } else if (status == 1) {
                stop++;
            }
        }
        int size = activity_stack.size();
        if (size == 0) {
            return true;
        }
        if (size == stop) {
            return true;
        }
        return false;
    }

    public void onStop() {
        super.onStop();
        if (this.mHomeWatcher != null) {
            this.mHomeWatcher.stopWatch();
            this.mHomeWatcher = null;
        }
        activity_stack.put(Integer.valueOf(getActivityInfo()), Integer.valueOf(1));
        onStackChange();
    }

    protected boolean isOnFront() {
        int start = 0;
        int stop = 0;
        for (Integer key : activity_stack.keySet()) {
            int status = ((Integer) activity_stack.get(key)).intValue();
            if (status == 0) {
                start++;
            } else if (status == 1) {
                stop++;
            }
        }
        if (activity_stack.size() <= 0 || start != 0) {
            return true;
        }
        return false;
    }

    protected void onStart() {
        super.onStart();
        this.mHomeWatcher = new HomeWatcher(this);
        this.mHomeWatcher.setOnHomePressedListener(this);
        this.mHomeWatcher.startWatch();
        activity_stack.put(Integer.valueOf(getActivityInfo()), Integer.valueOf(0));
        onStackChange();
    }

    protected void onDestroy() {
        super.onDestroy();
        activity_stack.remove(Integer.valueOf(getActivityInfo()));
        onStackChange();
    }

    protected void isGoExit(boolean isGoExit) {
        this.isGoExit = isGoExit;
    }

    public void onHomePressed() {
    }

    public void onHomeLongPressed() {
    }
}
