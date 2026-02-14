package com.example.footplaystats.ui.players;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.footplaystats.R;
import com.example.footplaystats.databinding.FragmentPlayersListBinding;
import com.example.footplaystats.session.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class PlayersListFragment extends Fragment {

    private FragmentPlayersListBinding binding;
    private PlayersAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentPlayersListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        NavController navController = NavHostFragment.findNavController(this);

        adapter = new PlayersAdapter(getDummyPlayers(), player ->
                navController.navigate(R.id.playerStatsFragment)
        );

        binding.recyclerPlayers.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerPlayers.setAdapter(adapter);

        // Mostrar botón añadir solo si es entrenador
        if (SessionManager.isCoach()) {
            binding.buttonAdd.setVisibility(View.VISIBLE);
        }

        binding.buttonAdd.setOnClickListener(v ->
                navController.navigate(R.id.addPlayerFragment)
        );
    }

    private List<PlayerItem> getDummyPlayers() {

        List<PlayerItem> list = new ArrayList<>();
        list.add(new PlayerItem("Carlos Martínez", 7.8));
        list.add(new PlayerItem("David López", 8.4));
        list.add(new PlayerItem("Álvaro Gómez", 6.9));
        list.add(new PlayerItem("Mario Sánchez", 9.1));

        return list;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}