package com.example.footplaystats.ui.list;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.footplaystats.R;
import com.example.footplaystats.data.PlayerRepository;
import com.example.footplaystats.databinding.FragmentPlayerListBinding;
import com.example.footplaystats.model.Player;
import com.example.footplaystats.session.SessionManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PlayerListFragment extends Fragment {

    private FragmentPlayerListBinding binding;

    private PlayersAdapter goalkeepersAdapter;
    private PlayersAdapter fieldPlayersAdapter;

    private PlayerRepository repository;

    private boolean isCoach; // 🔥 NUEVO

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentPlayerListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        repository = new PlayerRepository();
        isCoach = SessionManager.isCoach(requireContext()); // 🔥 NUEVO

        setupRecyclerViews();
        setupPermissions();
        setupHeaderButtons();
        setupSwipe(); // 🔥 NUEVO
        listenPlayers();
    }

    // ---------------------------------------------------
    // SWIPE DELETE (NUEVO)
    // ---------------------------------------------------
    private void setupSwipe() {

        if (!isCoach) return;

        ItemTouchHelper.SimpleCallback callback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView,
                                          @NonNull RecyclerView.ViewHolder viewHolder,
                                          @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                        RecyclerView recycler = (RecyclerView) viewHolder.itemView.getParent();

                        PlayersAdapter adapter =
                                recycler == binding.recyclerGoalkeepers
                                        ? goalkeepersAdapter
                                        : fieldPlayersAdapter;

                        int position = viewHolder.getBindingAdapterPosition();

                        Player player = adapter.getPlayerAt(position);

                        if (player != null) {
                            repository.deletePlayer(player.getId());
                        }
                    }

                    @Override
                    public void onChildDraw(@NonNull Canvas c,
                                            @NonNull RecyclerView recyclerView,
                                            @NonNull RecyclerView.ViewHolder viewHolder,
                                            float dX,
                                            float dY,
                                            int actionState,
                                            boolean isCurrentlyActive) {

                        View itemView = viewHolder.itemView;

                        if (dX < 0) {

                            Drawable background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_swipe_red);

                            background.setBounds(
                                    itemView.getRight() + (int) dX,
                                    itemView.getTop(),
                                    itemView.getRight(),
                                    itemView.getBottom()
                            );

                            background.draw(c);

                            Drawable icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_basura);

                            int iconSize = 60;
                            int iconMargin = (itemView.getHeight() - iconSize) / 2;

                            int iconTop = itemView.getTop() + iconMargin;
                            int iconBottom = iconTop + iconSize;
                            int iconLeft = itemView.getRight() - iconMargin - iconSize;
                            int iconRight = itemView.getRight() - iconMargin;

                            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                            icon.draw(c);
                        }

                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    }
                };

        new ItemTouchHelper(callback).attachToRecyclerView(binding.recyclerGoalkeepers);
        new ItemTouchHelper(callback).attachToRecyclerView(binding.recyclerFieldPlayers);
    }

    // ---------------------------------------------------
    // RECYCLERS
    // ---------------------------------------------------
    private void setupRecyclerViews() {

        goalkeepersAdapter = new PlayersAdapter(new ArrayList<>(), player -> openPlayerStats(player));
        fieldPlayersAdapter = new PlayersAdapter(new ArrayList<>(), player -> openPlayerStats(player));

        binding.recyclerGoalkeepers.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerGoalkeepers.setAdapter(goalkeepersAdapter);
        binding.recyclerGoalkeepers.setNestedScrollingEnabled(false);

        binding.recyclerFieldPlayers.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerFieldPlayers.setAdapter(fieldPlayersAdapter);
        binding.recyclerFieldPlayers.setNestedScrollingEnabled(false);
    }

    private void openPlayerStats(Player player) {
        if (player == null) return;

        Bundle bundle = new Bundle();
        bundle.putString("playerId", player.getId());

        NavHostFragment.findNavController(this)
                .navigate(R.id.playerStatsFragment, bundle);
    }

    // ---------------------------------------------------
    // HEADER
    // ---------------------------------------------------
    private void setupHeaderButtons() {

        binding.buttonSettings.setOnClickListener(v -> {
            // futura pantalla settings
        });
    }

    // ---------------------------------------------------
    // PERMISSIONS
    // ---------------------------------------------------
    private void setupPermissions() {

        if (!isCoach) {

            binding.buttonAdd.setVisibility(View.GONE);
            binding.footerContainer.setVisibility(View.GONE);
            binding.buttonLogout.setVisibility(View.GONE);

            binding.scrollContent.setPadding(
                    binding.scrollContent.getPaddingLeft(),
                    binding.scrollContent.getPaddingTop(),
                    binding.scrollContent.getPaddingRight(),
                    24
            );

        } else {

            binding.buttonAdd.setVisibility(View.VISIBLE);
            binding.footerContainer.setVisibility(View.VISIBLE);
            binding.buttonLogout.setVisibility(View.VISIBLE);

            binding.buttonAdd.setOnClickListener(v ->
                    NavHostFragment.findNavController(this)
                            .navigate(R.id.addPlayerFragment)
            );

            binding.buttonLogout.setOnClickListener(v -> {
                SessionManager.clearSession(requireContext());
                NavHostFragment.findNavController(this)
                        .navigate(R.id.loginFragment);
            });
        }
    }

    // ---------------------------------------------------
    // FIRESTORE
    // ---------------------------------------------------
    private void listenPlayers() {

        repository.listenToPlayers(new PlayerRepository.PlayersCallback() {
            @Override
            public void onPlayersLoaded(List<Player> players) {

                if (players == null) players = new ArrayList<>();

                if (players.isEmpty()) {
                    binding.cardTopPlayer.setVisibility(View.GONE);
                    binding.textPorteros.setVisibility(View.GONE);
                    binding.textFieldPlayers.setVisibility(View.GONE);
                    goalkeepersAdapter.updateList(new ArrayList<>());
                    fieldPlayersAdapter.updateList(new ArrayList<>());
                    return;
                }

                Collections.sort(players, new Comparator<Player>() {
                    @Override
                    public int compare(Player o1, Player o2) {
                        return Double.compare(o2.getTotalPoints(), o1.getTotalPoints());
                    }
                });

                Player topPlayer = players.get(0);
                bindTopPlayer(topPlayer);

                List<Player> rest = new ArrayList<>();
                if (players.size() > 1) {
                    rest.addAll(players.subList(1, players.size()));
                }

                List<Player> goalkeepers = new ArrayList<>();
                List<Player> fieldPlayers = new ArrayList<>();

                for (Player p : rest) {
                    if (p == null) continue;

                    String role = p.getRole();
                    if ("GOALKEEPER".equals(role)) {
                        goalkeepers.add(p);
                    } else if ("FIELD".equals(role)) {
                        fieldPlayers.add(p);
                    }
                }

                binding.textPorteros.setVisibility(goalkeepers.isEmpty() ? View.GONE : View.VISIBLE);
                binding.textFieldPlayers.setVisibility(fieldPlayers.isEmpty() ? View.GONE : View.VISIBLE);

                goalkeepersAdapter.updateList(goalkeepers);
                fieldPlayersAdapter.updateList(fieldPlayers);
            }

            @Override
            public void onError(Exception e) {}
        });
    }

    // ---------------------------------------------------
    // TOP PLAYER
    // ---------------------------------------------------
    private void bindTopPlayer(Player player) {

        if (player == null) {
            binding.cardTopPlayer.setVisibility(View.GONE);
            return;
        }

        binding.cardTopPlayer.setVisibility(View.VISIBLE);

        String name = player.getName() != null ? player.getName() : "";
        binding.textTopName.setText(name);

        // 🔥 ACTIVAR BADGE TOP
        binding.topBadge.setVisibility(View.VISIBLE);

        long pointsRounded = Math.round(player.getTotalPoints());
        binding.textTopPoints.setText(String.valueOf(pointsRounded));

        GradientDrawable background =
                (GradientDrawable) binding.textTopPoints.getBackground().mutate();

        background.setColor(
                ContextCompat.getColor(requireContext(), R.color.score_gold)
        );

        String imageUrl = player.getImageUrl();

        if (imageUrl != null && !imageUrl.trim().isEmpty()) {

            Glide.with(requireContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_player_placeholder)
                    .error(R.drawable.ic_player_placeholder)
                    .centerCrop()
                    .into(binding.imageTopAvatar);

        } else {
            binding.imageTopAvatar.setImageResource(R.drawable.ic_player_placeholder);
        }

        binding.cardTopPlayer.setOnClickListener(v -> openPlayerStats(player));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        repository.removeListener();
        binding = null;
    }
}