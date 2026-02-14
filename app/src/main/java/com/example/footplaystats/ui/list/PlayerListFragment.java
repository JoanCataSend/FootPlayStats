package com.example.footplaystats.ui.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.footplaystats.R;
import com.example.footplaystats.data.PlayerRepository;
import com.example.footplaystats.databinding.FragmentPlayerListBinding;
import com.example.footplaystats.model.Player;
import com.example.footplaystats.session.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class PlayerListFragment extends Fragment {

    private FragmentPlayerListBinding binding;
    private PlayersAdapter adapter;
    private PlayerRepository repository;

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

        setupRecycler();
        setupPermissions();
        listenPlayers();
    }

    // ---------------------------------------------------
    // RECYCLER
    // ---------------------------------------------------
    private void setupRecycler() {

        adapter = new PlayersAdapter(new ArrayList<>(), player -> {

            Bundle bundle = new Bundle();
            bundle.putString("playerId", player.getId());

            NavHostFragment.findNavController(this)
                    .navigate(R.id.playerStatsFragment, bundle);
        });

        binding.recyclerPlayers.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        binding.recyclerPlayers.setAdapter(adapter);
    }

    // ---------------------------------------------------
    // PERMISSIONS
    // ---------------------------------------------------
    private void setupPermissions() {

        boolean isCoach = SessionManager.isCoach(requireContext());

        // BOTÓN AÑADIR SOLO COACH
        if (!isCoach) {
            binding.buttonAdd.setVisibility(View.GONE);
        } else {
            binding.buttonAdd.setOnClickListener(v ->
                    NavHostFragment.findNavController(this)
                            .navigate(R.id.addPlayerFragment)
            );
        }

        // BOTÓN CERRAR SESIÓN SOLO COACH
        if (isCoach) {
            binding.buttonLogout.setVisibility(View.VISIBLE);

            binding.buttonLogout.setOnClickListener(v -> {

                SessionManager.clearSession(requireContext());

                NavHostFragment.findNavController(this)
                        .navigate(R.id.loginFragment);
            });

        } else {
            binding.buttonLogout.setVisibility(View.GONE);
        }
    }

    // ---------------------------------------------------
    // FIRESTORE VIA REPOSITORY
    // ---------------------------------------------------
    private void listenPlayers() {

        repository.listenToPlayers(new PlayerRepository.PlayersCallback() {
            @Override
            public void onPlayersLoaded(List<Player> players) {
                adapter.updateList(players);
            }

            @Override
            public void onError(Exception e) {
                // Puedes añadir Toast si quieres
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        repository.removeListener();
        binding = null;
    }
}