package com.example.footplaystats.ui.stats;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.footplaystats.databinding.FragmentPlayerStatsBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlayerStatsFragment extends Fragment {

    private FragmentPlayerStatsBinding binding;
    private FirebaseFirestore db;
    private String playerId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentPlayerStatsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        binding.toolbar.setNavigationOnClickListener(v ->
                NavHostFragment.findNavController(this).navigateUp()
        );

        if (getArguments() != null) {
            playerId = getArguments().getString("playerId");
        }

        if (playerId == null) {
            Toast.makeText(requireContext(),
                    "Error cargando jugador",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        loadPlayer();
    }

    private void loadPlayer() {

        db.collection("players")
                .document(playerId)
                .get()
                .addOnSuccessListener(this::parsePlayer)
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(),
                                "Error cargando datos",
                                Toast.LENGTH_SHORT).show()
                );
    }

    private void parsePlayer(DocumentSnapshot doc) {

        if (!doc.exists()) return;

        Double totalPoints = doc.getDouble("totalPoints");

        if (totalPoints != null) {
            binding.textTotalPoints.setText(
                    String.format("%.1f", totalPoints)
            );
        }

        Map<String, Object> categoriesMap =
                (Map<String, Object>) doc.get("categories");

        List<CategoryItem> categoryList = new ArrayList<>();

        if (categoriesMap != null) {

            for (String categoryName : categoriesMap.keySet()) {

                Map<String, Object> subStatsMap =
                        (Map<String, Object>) categoriesMap.get(categoryName);

                List<SubStatItem> subStats = new ArrayList<>();
                double sum = 0;
                int count = 0;

                if (subStatsMap != null) {

                    for (String subName : subStatsMap.keySet()) {

                        Double value =
                                ((Number) subStatsMap.get(subName)).doubleValue();

                        subStats.add(
                                new SubStatItem(
                                        formatName(subName),
                                        value
                                )
                        );

                        sum += value;
                        count++;
                    }
                }

                double average = count > 0 ? sum / count : 0;

                categoryList.add(
                        new CategoryItem(
                                capitalize(categoryName),
                                average,
                                subStats
                        )
                );
            }
        }

        setupRecycler(categoryList);
    }

    private void setupRecycler(List<CategoryItem> categories) {

        CategoryAdapter adapter = new CategoryAdapter(categories);

        binding.recyclerCategories.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        binding.recyclerCategories.setAdapter(adapter);
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    private String formatName(String text) {
        return text.replace("_", " ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}