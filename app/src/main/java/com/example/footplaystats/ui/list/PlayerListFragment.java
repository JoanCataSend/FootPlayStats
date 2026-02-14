package com.example.footplaystats.ui.list;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.footplaystats.R;
import com.example.footplaystats.databinding.FragmentPlayerListBinding;
import com.example.footplaystats.model.Player;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class PlayerListFragment extends Fragment {

    private FragmentPlayerListBinding binding;
    private PlayersAdapter adapter;
    private FirebaseFirestore db;
    private ListenerRegistration registration;

    private static final String TAG = "PlayerListFragment";

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

        db = FirebaseFirestore.getInstance();

        setupRecycler();
        setupAddButton();
        listenPlayers();
    }

    // ---------------------------------------------------
    // RECYCLER CONFIGURATION
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
    // ADD BUTTON
    // ---------------------------------------------------
    private void setupAddButton() {

        binding.buttonAdd.setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.addPlayerFragment)
        );
    }

    // ---------------------------------------------------
    // FIRESTORE LISTENER
    // ---------------------------------------------------
    private void listenPlayers() {

        registration = db.collection("players")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, e) -> {

                    if (e != null) {
                        Log.e(TAG, "Firestore error", e);
                        Toast.makeText(requireContext(),
                                "Error loading players",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (snapshots == null) return;

                    List<Player> players = new ArrayList<>();

                    for (QueryDocumentSnapshot doc : snapshots) {

                        Player player = doc.toObject(Player.class);
                        player.setId(doc.getId());
                        players.add(player);
                    }

                    adapter.updateList(players);
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (registration != null) {
            registration.remove();
        }

        binding = null;
    }
}