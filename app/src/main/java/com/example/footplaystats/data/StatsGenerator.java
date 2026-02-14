package com.example.footplaystats.data;

import java.util.HashMap;
import java.util.Map;

public class StatsGenerator {

    public static Map<String, Object> generateStats(String role) {

        Map<String, Object> categories = new HashMap<>();

        if ("FIELD".equals(role)) {

            categories.put("tecnica", tecnicaField());
            categories.put("tactica", tacticaField());
            categories.put("fisico", fisico());
            categories.put("psicologico", psicologico());

        } else {

            categories.put("tecnica", tecnicaGoalkeeper());
            categories.put("tactica", tacticaGoalkeeper());
            categories.put("fisico", fisico());
            categories.put("psicologico", psicologico());
        }

        return categories;
    }

    private static Map<String, Double> tecnicaField() {
        Map<String, Double> map = new HashMap<>();
        map.put("pases_cortos", 0.0);
        map.put("pases_largos", 0.0);
        map.put("controles", 0.0);
        map.put("conduccion", 0.0);
        map.put("regate", 0.0);
        map.put("remate", 0.0);
        map.put("juego_aereo", 0.0);
        map.put("robos_intercepciones", 0.0);
        return map;
    }

    private static Map<String, Double> tacticaField() {
        Map<String, Double> map = new HashMap<>();
        map.put("toma_decisiones", 0.0);
        map.put("posicionamiento_defensivo", 0.0);
        map.put("juego_sin_balon", 0.0);
        map.put("transiciones", 0.0);
        map.put("abp", 0.0);
        return map;
    }

    private static Map<String, Double> tecnicaGoalkeeper() {
        Map<String, Double> map = new HashMap<>();
        map.put("blocajes", 0.0);
        map.put("reflejos", 0.0);
        map.put("salidas_alto", 0.0);
        map.put("uno_vs_uno", 0.0);
        map.put("juego_pies_corto", 0.0);
        map.put("juego_pies_largo", 0.0);
        return map;
    }

    private static Map<String, Double> tacticaGoalkeeper() {
        Map<String, Double> map = new HashMap<>();
        map.put("posicionamiento", 0.0);
        map.put("lectura_juego", 0.0);
        map.put("organizacion_defensiva", 0.0);
        map.put("toma_decisiones", 0.0);
        return map;
    }

    private static Map<String, Double> fisico() {
        Map<String, Double> map = new HashMap<>();
        map.put("velocidad", 0.0);
        map.put("aceleracion", 0.0);
        map.put("fuerza", 0.0);
        map.put("salto", 0.0);
        map.put("resistencia", 0.0);
        map.put("coordinacion", 0.0);
        return map;
    }

    private static Map<String, Double> psicologico() {
        Map<String, Double> map = new HashMap<>();
        map.put("concentracion", 0.0);
        map.put("gestion_presion", 0.0);
        map.put("confianza", 0.0);
        map.put("liderazgo", 0.0);
        map.put("motivacion", 0.0);
        map.put("gestion_error", 0.0);
        return map;
    }
}