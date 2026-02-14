package com.example.footplaystats.ui.players;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.footplaystats.databinding.ItemPlayerBinding;

import java.util.List;

public class PlayersAdapter extends RecyclerView.Adapter<PlayersAdapter.PlayerViewHolder> {

    public interface OnPlayerClickListener {
        void onPlayerClick(PlayerItem player);
    }

    private List<PlayerItem> players;
    private OnPlayerClickListener listener;

    public PlayersAdapter(List<PlayerItem> players,
                          OnPlayerClickListener listener) {
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
        PlayerItem player = players.get(position);
        holder.bind(player);
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    class PlayerViewHolder extends RecyclerView.ViewHolder {

        private final ItemPlayerBinding binding;

        public PlayerViewHolder(ItemPlayerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(PlayerItem player) {
            binding.textPlayerName.setText(player.getName());
            binding.textPlayerPoints.setText(String.valueOf(player.getPoints()));

            binding.getRoot().setOnClickListener(v ->
                    listener.onPlayerClick(player)
            );
        }
    }
}