package com.example.footplaystats.ui.stats;

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
import com.example.footplaystats.databinding.FragmentPlayerStatsBinding;
import com.example.footplaystats.session.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class PlayerStatsFragment extends Fragment {

    private FragmentPlayerStatsBinding binding;

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

        // Botón cerrar (X)
        binding.toolbar.setNavigationOnClickListener(v ->
                NavHostFragment.findNavController(this).navigateUp()
        );

        // Mostrar botón modificar solo si es entrenador
        if (SessionManager.isCoach()) {
            binding.buttonEdit.setVisibility(View.VISIBLE);

            binding.buttonEdit.setOnClickListener(v ->
                    NavHostFragment.findNavController(this)
                            .navigate(R.id.editPlayerFragment)
            );
        }

        setupRecycler();
    }

    private void setupRecycler() {

        List<CategoryItem> dummyCategories = new ArrayList<>();

        // ⚽ Técnica
        List<SubStatItem> tecnica = new ArrayList<>();
        tecnica.add(new SubStatItem("Pases Cortos", 8.5));
        tecnica.add(new SubStatItem("Regate", 7.9));
        tecnica.add(new SubStatItem("Remate", 8.1));

        dummyCategories.add(
                new CategoryItem("⚽ Técnica", 8.2, tecnica)
        );

        // 🧠 Táctica
        List<SubStatItem> tactica = new ArrayList<>();
        tactica.add(new SubStatItem("Toma de decisiones", 8.3));
        tactica.add(new SubStatItem("Posicionamiento", 7.8));

        dummyCategories.add(
                new CategoryItem("🧠 Táctica", 7.9, tactica)
        );

        // 🏃 Físico
        List<SubStatItem> fisico = new ArrayList<>();
        fisico.add(new SubStatItem("Velocidad", 8.6));
        fisico.add(new SubStatItem("Resistencia", 8.4));

        dummyCategories.add(
                new CategoryItem("🏃 Físico", 8.6, fisico)
        );

        // 🧘 Psicológico
        List<SubStatItem> psicologico = new ArrayList<>();
        psicologico.add(new SubStatItem("Concentración", 8.2));
        psicologico.add(new SubStatItem("Confianza", 8.0));

        dummyCategories.add(
                new CategoryItem("🧘 Psicológico", 8.1, psicologico)
        );

        CategoryAdapter adapter = new CategoryAdapter(dummyCategories);

        binding.recyclerCategories.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        binding.recyclerCategories.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}