package com.example.footplaystats.data;

import com.example.footplaystats.model.Player;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class PlayerRepository {

    private final FirebaseFirestore db;
    private ListenerRegistration registration;

    public interface PlayersCallback {
        void onPlayersLoaded(List<Player> players);
        void onError(Exception e);
    }

    public PlayerRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public void listenToPlayers(PlayersCallback callback) {

        registration = db.collection("players")
                .orderBy("totalPoints", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, error) -> {

                    if (error != null) {
                        callback.onError(error);
                        return;
                    }

                    if (snapshots == null) return;

                    List<Player> players = new ArrayList<>();

                    for (var doc : snapshots.getDocuments()) {

                        Player player = doc.toObject(Player.class);

                        if (player != null) {
                            player.setId(doc.getId());
                            players.add(player);
                        }
                    }

                    callback.onPlayersLoaded(players);
                });
    }

    // 🔥 NUEVO MÉTODO
    public void deletePlayer(String playerId) {
        if (playerId == null) return;

        db.collection("players")
                .document(playerId)
                .delete();
    }

    public void removeListener() {
        if (registration != null) {
            registration.remove();
        }
    }
}