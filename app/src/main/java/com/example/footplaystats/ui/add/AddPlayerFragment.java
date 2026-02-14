package com.example.footplaystats.ui.add;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.footplaystats.databinding.FragmentAddPlayerBinding;
import com.example.footplaystats.data.StatsGenerator;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddPlayerFragment extends Fragment {

    private FragmentAddPlayerBinding binding;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentAddPlayerBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        // Botón cerrar (X)
        binding.toolbar.setNavigationOnClickListener(v ->
                NavHostFragment.findNavController(this).navigateUp()
        );

        binding.buttonCreate.setOnClickListener(v -> createPlayer());
    }

    private void createPlayer() {

        String name = binding.editTextName.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(requireContext(),
                    "Introduce el nombre del jugador",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String role = binding.radioGoalkeeper.isChecked()
                ? "GOALKEEPER"
                : "FIELD";

        // Generar categorías automáticamente
        Map<String, Object> categories = StatsGenerator.generateStats(role);

        // Construir documento completo
        Map<String, Object> player = new HashMap<>();
        player.put("name", name);
        player.put("role", role);
        player.put("totalPoints", 0.0);
        player.put("createdAt", Timestamp.now());
        player.put("categories", categories);

        // Guardar en Firestore
        db.collection("players")
                .add(player)
                .addOnSuccessListener(documentReference -> {

                    Toast.makeText(requireContext(),
                            "Jugador creado correctamente",
                            Toast.LENGTH_SHORT).show();

                    NavHostFragment.findNavController(this).navigateUp();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(),
                                "Error al crear jugador",
                                Toast.LENGTH_SHORT).show()
                );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}