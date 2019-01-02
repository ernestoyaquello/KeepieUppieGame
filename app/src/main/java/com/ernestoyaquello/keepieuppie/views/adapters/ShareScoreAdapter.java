package com.ernestoyaquello.keepieuppie.views.adapters;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ernestoyaquello.keepieuppie.R;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ShareScoreAdapter extends RecyclerView.Adapter<ShareScoreAdapter.ViewHolder> {

    public interface ItemClickListener {
        void onListItemClick(View view, int position);
    }

    private final PackageManager packageManager;
    private final LayoutInflater inflater;
    private List<ResolveInfo> data;
    private ItemClickListener itemClickListener;

    public ShareScoreAdapter(Context context) {
        this.packageManager = context.getPackageManager();
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    @NonNull
    public ShareScoreAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.share_score_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ResolveInfo item = getItem(position);
        if (item != null) {
            holder.appNameTextView.setText(item.loadLabel(packageManager));
            holder.appIconImageView.setImageDrawable(item.loadIcon(packageManager));
        }
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    public ResolveInfo getItem(int id) {
        return data != null ? data.get(id) : null;
    }

    public void setDataSet(List<ResolveInfo> data) {
        if (data != null) {
            Collections.sort(data, new Comparator<ResolveInfo>() {
                @Override
                public int compare(ResolveInfo first, ResolveInfo second) {
                    int result = 0;

                    if (first != second && first != null && second != null) {
                        Integer x1 = first.preferredOrder;
                        Integer x2 = second.preferredOrder;
                        result = x1.compareTo(x2);

                        if (result == 0) {
                            x1 = first.priority;
                            x2 = second.priority;
                            result = x1.compareTo(x2);

                            if (result == 0) {
                                x1 = first.match;
                                x2 = second.match;
                                result = x1.compareTo(x2);

                                if (result == 0) {
                                    String y1 = first.loadLabel(packageManager).toString();
                                    String y2 = second.loadLabel(packageManager).toString();

                                    result = y1.compareTo(y2);
                                }
                            }
                        }
                    }

                    return result;
                }
            });
        }

        this.data = data;

        notifyDataSetChanged();
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView appNameTextView;
        final ImageView appIconImageView;

        ViewHolder(View v) {
            super(v);

            v.setOnClickListener(this);

            appNameTextView = v.findViewById(R.id.appName);
            appIconImageView = v.findViewById(R.id.appIcon);
        }

        @Override
        public void onClick(View view) {
            if (itemClickListener != null) {
                itemClickListener.onListItemClick(view, getAdapterPosition());
            }
        }
    }
}
