package com.example.footplaystats.ui.login;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.footplaystats.R;
import com.example.footplaystats.databinding.FragmentLoginBinding;
import com.example.footplaystats.session.SessionManager;
import com.example.footplaystats.session.UserRole;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;

    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASSWORD = "1234";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        NavController navController = NavHostFragment.findNavController(this);

        // Entrenador
        binding.buttonLogin.setOnClickListener(v -> {

            String username = binding.editTextUsername.getText().toString().trim();
            String password = binding.editTextPassword.getText().toString().trim();

            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                Toast.makeText(requireContext(),
                        "Completa todos los campos",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (username.equals(ADMIN_USER) && password.equals(ADMIN_PASSWORD)) {

                SessionManager.setRole(UserRole.COACH);
                navController.navigate(R.id.playerListFragment);

            } else {
                Toast.makeText(requireContext(),
                        "Credenciales incorrectas",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Jugador
        binding.buttonPlayerMode.setOnClickListener(v -> {

            SessionManager.setRole(UserRole.PLAYER);
            navController.navigate(R.id.playerListFragment);

        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}