package com.example.footplaystats.ui.list;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.footplaystats.R;
import com.example.footplaystats.databinding.ItemPlayerBinding;
import com.example.footplaystats.model.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayersAdapter extends RecyclerView.Adapter<PlayersAdapter.PlayerViewHolder> {

    public interface OnPlayerClickListener {
        void onPlayerClick(Player player);
    }

    private List<Player> players = new ArrayList<>();
    private final OnPlayerClickListener listener;

    public PlayersAdapter(List<Player> players, OnPlayerClickListener listener) {
        if (players != null) {
            this.players = players;
        }
        this.listener = listener;
    }

    public Player getPlayerAt(int position) {
        if (position >= 0 && position < players.size()) {
            return players.get(position);
        }
        return null;
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemPlayerBinding binding = ItemPlayerBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );

        return new PlayerViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {

        Player player = players.get(position);

        holder.binding.textName.setText(
                player.getName() != null ? player.getName() : ""
        );

        long pointsRounded = Math.round(player.getTotalPoints());
        holder.binding.textPoints.setText(String.valueOf(pointsRounded));

        int colorRes;

        if (pointsRounded < 50) {
            colorRes = R.color.score_red;
        } else if (pointsRounded < 70) {
            colorRes = R.color.score_orange;
        } else {
            colorRes = R.color.score_green;
        }

        GradientDrawable background =
                (GradientDrawable) holder.binding.textPoints.getBackground().mutate();

        background.setColor(
                ContextCompat.getColor(holder.binding.getRoot().getContext(), colorRes)
        );

        String imageUrl = player.getImageUrl();

        if (imageUrl != null && !imageUrl.trim().isEmpty()) {

            Glide.with(holder.binding.getRoot().getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_player_placeholder)
                    .error(R.drawable.ic_player_placeholder)
                    .centerCrop()
                    .into(holder.binding.imageAvatar);

        } else {
            holder.binding.imageAvatar.setImageResource(
                    R.drawable.ic_player_placeholder
            );
        }

        holder.binding.getRoot().setOnClickListener(v -> {
            if (listener != null) {
                listener.onPlayerClick(player);
            }
        });
    }

    @Override
    public int getItemCount() {
        return players != null ? players.size() : 0;
    }

    public void updateList(List<Player> newList) {
        if (newList != null) {
            this.players = newList;
            notifyDataSetChanged();
        }
    }

    static class PlayerViewHolder extends RecyclerView.ViewHolder {

        ItemPlayerBinding binding;

        PlayerViewHolder(ItemPlayerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}