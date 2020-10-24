package com.github.kr328.clipboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

public class AppAdapter extends BaseAdapter {
    private final Context context;

    private List<App> apps;

    public AppAdapter(Context context) {
        this.context = context;
        this.apps = Collections.emptyList();
    }

    @Override
    public int getCount() {
        return apps.size();
    }

    @Override
    public Object getItem(int position) {
        return apps.get(position);
    }

    @Override
    public long getItemId(int position) {
        return apps.get(position).getPackageName().hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.app_adapter, parent, false);
        }

        final View icon = convertView.findViewById(R.id.icon);
        final TextView label = convertView.findViewById(R.id.label);
        final TextView packageName = convertView.findViewById(R.id.packageName);
        final CheckBox selected = convertView.findViewById(R.id.checkbox);

        final App app = apps.get(position);

        icon.setBackground(app.getIcon());
        label.setText(app.getLabel());
        packageName.setText(app.getPackageName());
        selected.setChecked(app.isSelected());

        return convertView;
    }

    public void updateApps(List<App> apps) {
        this.apps = apps;

        this.notifyDataSetChanged();
    }

    public boolean invertSelected(int index) {
        final App app = apps.get(index);

        final boolean selected = !app.isSelected();

        app.setSelected(selected);

        this.notifyDataSetChanged();

        return selected;
    }
}
