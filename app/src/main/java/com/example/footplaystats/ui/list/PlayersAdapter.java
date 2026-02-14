package com.example.footplaystats.ui.list;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
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

        // 🔹 Nombre
        holder.binding.textName.setText(
                player.getName() != null ? player.getName() : ""
        );

        // 🔹 Puntos
        holder.binding.textPoints.setText(
                String.format("%.1f", player.getTotalPoints())
        );

        // 🔹 Rol bonito
        String role = player.getRole();
        String roleText;

        if ("GOALKEEPER".equals(role)) {
            roleText = "Portero";
        } else if ("FIELD".equals(role)) {
            roleText = "Jugador de campo";
        } else {
            roleText = "";
        }

        holder.binding.textSub.setText(roleText);

        // 🔹 Imagen desde Firebase
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