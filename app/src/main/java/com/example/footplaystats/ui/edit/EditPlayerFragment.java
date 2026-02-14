package com.example.footplaystats.ui.edit;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.footplaystats.databinding.FragmentEditPlayerBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditPlayerFragment extends Fragment {

    private FragmentEditPlayerBinding binding;
    private FirebaseFirestore db;
    private String playerId;

    // Guardaremos aquí los EditText dinámicos
    private Map<String, Map<String, TextInputEditText>> editFields = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentEditPlayerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            playerId = getArguments().getString("playerId");
        }

        binding.toolbar.setNavigationOnClickListener(v ->
                NavHostFragment.findNavController(this).navigateUp()
        );

        if (playerId == null) {
            Toast.makeText(requireContext(),
                    "Error cargando jugador",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        loadPlayer();

        binding.buttonSave.setOnClickListener(v -> saveChanges());
    }

    // ---------------------------------------------------
    // LOAD PLAYER
    // ---------------------------------------------------
    private void loadPlayer() {

        db.collection("players")
                .document(playerId)
                .get()
                .addOnSuccessListener(this::populateFields)
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(),
                                "Error cargando datos",
                                Toast.LENGTH_SHORT).show()
                );
    }

    private void populateFields(DocumentSnapshot doc) {

        if (!doc.exists()) return;

        binding.editTextName.setText(doc.getString("name"));

        Map<String, Object> categories =
                (Map<String, Object>) doc.get("categories");

        if (categories == null) return;

        binding.containerCategories.removeAllViews();
        editFields.clear();

        for (String categoryName : categories.keySet()) {

            TextView categoryTitle = new TextView(requireContext());
            categoryTitle.setText(categoryName.toUpperCase());
            categoryTitle.setTextSize(16);
            categoryTitle.setPadding(0, 32, 0, 16);

            binding.containerCategories.addView(categoryTitle);

            Map<String, Object> subStats =
                    (Map<String, Object>) categories.get(categoryName);

            Map<String, TextInputEditText> subStatFields = new HashMap<>();

            if (subStats != null) {

                for (String subName : subStats.keySet()) {

                    LinearLayout row = new LinearLayout(requireContext());
                    row.setOrientation(LinearLayout.HORIZONTAL);
                    row.setPadding(0, 8, 0, 8);

                    TextView label = new TextView(requireContext());
                    label.setText(subName.replace("_", " "));
                    label.setLayoutParams(new LinearLayout.LayoutParams(
                            0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

                    TextInputEditText input = new TextInputEditText(requireContext());
                    input.setLayoutParams(new LinearLayout.LayoutParams(
                            200, ViewGroup.LayoutParams.WRAP_CONTENT));
                    input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
                    input.setText(String.valueOf(
                            ((Number) subStats.get(subName)).doubleValue()
                    ));

                    row.addView(label);
                    row.addView(input);

                    binding.containerCategories.addView(row);

                    subStatFields.put(subName, input);
                }
            }

            editFields.put(categoryName, subStatFields);
        }
    }

    // ---------------------------------------------------
    // SAVE
    // ---------------------------------------------------
    private void saveChanges() {

        String name = binding.editTextName.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(requireContext(),
                    "El nombre no puede estar vacío",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updatedCategories = new HashMap<>();
        double totalSum = 0;
        int totalCount = 0;

        for (String category : editFields.keySet()) {

            Map<String, Object> subMap = new HashMap<>();

            for (String sub : editFields.get(category).keySet()) {

                String valueStr = editFields.get(category)
                        .get(sub)
                        .getText()
                        .toString();

                double value = TextUtils.isEmpty(valueStr)
                        ? 0
                        : Double.parseDouble(valueStr);

                subMap.put(sub, value);

                totalSum += value;
                totalCount++;
            }

            updatedCategories.put(category, subMap);
        }

        double totalPoints = totalCount > 0 ? totalSum / totalCount : 0;

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("categories", updatedCategories);
        updates.put("totalPoints", totalPoints);

        db.collection("players")
                .document(playerId)
                .update(updates)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(requireContext(),
                            "Jugador actualizado",
                            Toast.LENGTH_SHORT).show();
                    NavHostFragment.findNavController(this).navigateUp();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(),
                                "Error guardando cambios",
                                Toast.LENGTH_SHORT).show()
                );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}