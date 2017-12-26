package com.jwkj.adapter;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.hikam.C0291R;
import com.jwkj.data.Contact;
import com.jwkj.fragment.KeyboardFrag;
import java.util.List;

public class FilterUserAdapter extends BaseAdapter {
    private Context context;
    private List<Contact> data;

    public FilterUserAdapter(Context context, List<Contact> data) {
        this.context = context;
        this.data = data;
    }

    public int getCount() {
        return this.data.size();
    }

    public Object getItem(int arg0) {
        return null;
    }

    public long getItemId(int arg0) {
        return 0;
    }

    public View getView(int position, View arg1, ViewGroup arg2) {
        View view = arg1;
        if (view == null) {
            view = LayoutInflater.from(this.context).inflate(C0291R.layout.list_filter_user_item, null);
        }
        TextView account = (TextView) view.findViewById(C0291R.id.account);
        Contact contact = (Contact) this.data.get(position);
        ((TextView) view.findViewById(C0291R.id.name)).setText(contact.contactName);
        SpannableStringBuilder builder = new SpannableStringBuilder(contact.contactId);
        builder.setSpan(new ForegroundColorSpan(this.context.getResources().getColor(C0291R.color.text_color_blue)), contact.contactId.indexOf(KeyboardFrag.searchTellNum), contact.contactId.indexOf(KeyboardFrag.searchTellNum) + KeyboardFrag.searchTellNum.length(), 33);
        account.setText(builder);
        return view;
    }

    public void upDateData(List<Contact> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void clear() {
        this.data.clear();
        notifyDataSetChanged();
    }
}
