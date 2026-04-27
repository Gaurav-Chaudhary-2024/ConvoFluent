package com.example.convofluent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {

    private List<LeaderboardItem> items;

    public LeaderboardAdapter(List<LeaderboardItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_leaderboard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LeaderboardItem item = items.get(position);
        holder.tvRank.setText(item.getRank());
        holder.tvAvatarText.setText(item.getAvatarText());
        holder.tvName.setText(item.getName());
        holder.tvCountry.setText(item.getCountry());
        holder.tvXp.setText(item.getXp());
        holder.tvLevel.setText(item.getLevel());
        holder.tvNextLevelXp.setText(item.getNextLevelXp());
        holder.progressBar.setProgress(item.getProgress());
        holder.ivAvatar.setBackgroundColor(item.getAvatarColor());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRank, tvAvatarText, tvName, tvCountry, tvXp, tvLevel, tvNextLevelXp;
        ProgressBar progressBar;
        View ivAvatar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tvRank);
            tvAvatarText = itemView.findViewById(R.id.tvAvatarText);
            tvName = itemView.findViewById(R.id.tvName);
            tvCountry = itemView.findViewById(R.id.tvCountry);
            tvXp = itemView.findViewById(R.id.tvXp);
            tvLevel = itemView.findViewById(R.id.tvLevel);
            tvNextLevelXp = itemView.findViewById(R.id.tvNextLevelXp);
            progressBar = itemView.findViewById(R.id.progressBar);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
        }
    }
}
