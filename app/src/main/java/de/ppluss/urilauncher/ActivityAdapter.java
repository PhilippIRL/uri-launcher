package de.ppluss.urilauncher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder> {

    private List<AppInfo> mDataset;
    private MainActivity mContext;

    public ActivityAdapter(List<AppInfo> dataset, MainActivity context) {
        mDataset = dataset;
        mContext = context;
    }

    public static class ActivityViewHolder extends RecyclerView.ViewHolder {

        public TextView appTitle;
        public TextView appPackage;
        public ImageView appIcon;

        public ActivityViewHolder(View itemView) {
            super(itemView);

            appIcon = itemView.findViewById(R.id.appIcon);
            appPackage = itemView.findViewById(R.id.appPackage);
            appTitle = itemView.findViewById(R.id.appTitle);
        }

    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View appView = inflater.inflate(R.layout.list_app_entry, parent, false);

        return new ActivityViewHolder(appView);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {

        AppInfo info = mDataset.get(position);

        holder.appPackage.setText(info.packageName);
        holder.appTitle.setText(info.appName);
        holder.appIcon.setImageDrawable(info.icon);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = mContext.activityList.getChildLayoutPosition(v);
                mContext.launchApp(mDataset.get(pos));
            }
        });

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
