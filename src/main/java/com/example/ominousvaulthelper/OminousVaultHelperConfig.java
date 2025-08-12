package com.example.ominousvaulthelper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class OminousVaultHelperConfig {
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "ominousvaulthelper.json");
    private static final Gson GSON = new Gson();

    public static List<String> loadConfig() {
        try {
            if (!CONFIG_FILE.exists()) saveConfig(new ArrayList<>());
            FileReader reader = new FileReader(CONFIG_FILE);
            Type type = new TypeToken<List<String>>() {}.getType();
            return GSON.fromJson(reader, type);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static void saveConfig(List<String> items) {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(items, writer);
        } catch (Exception ignored) {}
    }
}