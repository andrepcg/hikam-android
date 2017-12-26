package com.jwkj.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import com.hikam.C0291R;

public class FaultFragment extends Fragment {
    TextView cleck_refresh;
    String error_text = "";
    boolean isCanRefresh = false;
    TextView text;

    class C05271 implements OnClickListener {
        C05271() {
        }

        public void onClick(View v) {
            if (!FaultFragment.this.isCanRefresh) {
            }
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(C0291R.layout.fragment_fault, container, false);
        view.setOnClickListener(new C05271());
        initComponent(view);
        return view;
    }

    public void initComponent(View view) {
        this.text = (TextView) view.findViewById(C0291R.id.default_text);
        this.cleck_refresh = (TextView) view.findViewById(C0291R.id.click_refresh);
        if (this.isCanRefresh) {
            this.cleck_refresh.setVisibility(0);
        }
        this.text.setText(this.error_text);
    }

    public void setErrorText(String error) {
        this.error_text = error;
    }

    public void setClickRefresh() {
        if (!this.isCanRefresh) {
            this.isCanRefresh = true;
        }
    }
}
