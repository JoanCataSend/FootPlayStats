package com.example.footplaystats.ui.stats;

import android.graphics.Color;
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

import com.bumptech.glide.Glide;
import com.example.footplaystats.R;
import com.example.footplaystats.databinding.FragmentPlayerStatsBinding;
import com.example.footplaystats.session.SessionManager;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
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

        // Botón cerrar (X)
        binding.buttonClose.setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigateUp()
        );

        if (getArguments() != null) {
            playerId = getArguments().getString("playerId");
        }

        // Mostrar botón editar SOLO si es coach
        if (SessionManager.isCoach(requireContext())) {
            binding.buttonEdit.setVisibility(View.VISIBLE);
        } else {
            binding.buttonEdit.setVisibility(View.GONE);
        }

        // Acción botón editar
        binding.buttonEdit.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("playerId", playerId);

            NavHostFragment.findNavController(this)
                    .navigate(R.id.editPlayerFragment, bundle);
        });

        loadPlayer();
    }

    private void loadPlayer() {

        db.collection("players")
                .document(playerId)
                .get()
                .addOnSuccessListener(this::parsePlayer)
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(),
                                "Error loading player",
                                Toast.LENGTH_SHORT).show()
                );
    }

    private void parsePlayer(DocumentSnapshot doc) {

        if (!doc.exists()) return;

        // Nombre jugador
        binding.textPlayerName.setText(doc.getString("name"));

        // Total puntos
        Double totalPoints = doc.getDouble("totalPoints");
        if (totalPoints != null) {
            binding.textTotalPoints.setText(
                    String.format("%.1f", totalPoints)
            );
        }

        // Imagen jugador
        String imageUrl = doc.getString("imageUrl");

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .into(binding.imageAvatar);
        } else {
            binding.imageAvatar.setImageResource(R.mipmap.ic_launcher);
        }

        Map<String, Object> categoriesMap =
                (Map<String, Object>) doc.get("categories");

        List<CategoryItem> categoryList = new ArrayList<>();
        List<Float> radarValues = new ArrayList<>();
        List<String> radarLabels = new ArrayList<>();

        String[] orderedCategories = {
                "tecnica",
                "tactica",
                "fisico",
                "psicologico"
        };

        if (categoriesMap != null) {

            for (String categoryName : orderedCategories) {

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
                                        subName.replace("_", " "),
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

                radarValues.add((float) average);
                radarLabels.add(capitalize(categoryName));
            }
        }

        setupRecycler(categoryList);
        setupRadarChart(radarValues, radarLabels);
    }

    private void setupRecycler(List<CategoryItem> categories) {

        CategoryAdapter adapter = new CategoryAdapter(categories);

        binding.recyclerCategories.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        binding.recyclerCategories.setAdapter(adapter);
    }

    private void setupRadarChart(List<Float> values, List<String> labels) {

        List<RadarEntry> entries = new ArrayList<>();
        for (Float value : values) {
            entries.add(new RadarEntry(value));
        }

        RadarDataSet dataSet = new RadarDataSet(entries, "");
        dataSet.setColor(Color.parseColor("#4CAF50"));
        dataSet.setFillColor(Color.parseColor("#4CAF50"));
        dataSet.setDrawFilled(true);
        dataSet.setFillAlpha(160);
        dataSet.setLineWidth(2f);

        RadarData data = new RadarData(dataSet);
        data.setDrawValues(false);

        binding.radarChart.setData(data);

        XAxis xAxis = binding.radarChart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return labels.get((int) value % labels.size());
            }
        });

        binding.radarChart.getDescription().setEnabled(false);
        binding.radarChart.getLegend().setEnabled(false);
        binding.radarChart.getYAxis().setAxisMinimum(0f);
        binding.radarChart.getYAxis().setAxisMaximum(100f);
        binding.radarChart.invalidate();
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
}