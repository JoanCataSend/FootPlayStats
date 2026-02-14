package com.example.footplaystats.ui.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.footplaystats.databinding.ItemPlayerBinding;
import com.example.footplaystats.model.Player;

import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder> {

    public interface OnPlayerClickListener {
        void onPlayerClick(Player player);
    }

    private final List<Player> players;
    private final OnPlayerClickListener listener;

    public PlayerAdapter(List<Player> players, OnPlayerClickListener listener) {
        this.players = players;
        this.listener = listener;
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

        holder.binding.textName.setText(player.getName());
        holder.binding.textPoints.setText(String.valueOf(player.getTotalPoints()));

        holder.binding.getRoot().setOnClickListener(v ->
                listener.onPlayerClick(player)
        );
    }


    @Override
    public int getItemCount() {
        return players.size();
    }

    static class PlayerViewHolder extends RecyclerView.ViewHolder {

        ItemPlayerBinding binding;

        PlayerViewHolder(ItemPlayerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}