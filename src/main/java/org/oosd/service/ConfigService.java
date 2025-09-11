package org.oosd.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.oosd.model.Config;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class ConfigService {
    private static volatile ConfigService instance;
    public static ConfigService getInstance() {
        if (instance == null) {
            synchronized (ConfigService.class) {
                if (instance == null) instance = new ConfigService();
            }
        }
        return instance;
    }

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Path configFile = Paths.get("JavaTetrisConfig.json");
    private Config config = new Config();

    private final List<ConfigObserver> observers = new ArrayList<>();

    private ConfigService(){}

    public synchronized Config get(){ return config; }

    public synchronized void addObserver(ConfigObserver o){ observers.add(o); }
    public synchronized void removeObserver(ConfigObserver o){ observers.remove(o); }

    /** mutate + persist + notify */
    public synchronized void update(Consumer<Config> mutator){
        Config old = gson.fromJson(gson.toJson(config), Config.class); // shallow copy
        mutator.accept(config);
        save();
        for (ConfigObserver o : observers) o.onConfigChanged(old, config);
    }

    public synchronized void load(){
        try{
            if (Files.exists(configFile)){
                String json = Files.readString(configFile);
                Config disk = gson.fromJson(json, Config.class);
                if (disk != null) config = disk;
            } else {
                save();
            }
        }catch(IOException e){
            System.err.println("[ConfigService] load failed: " + e.getMessage());
        }
    }

    public synchronized void save() {
        try {
            Path parent = configFile.getParent();
            if (parent != null && !Files.exists(parent)) Files.createDirectories(parent);

            Files.writeString(
                    configFile,
                    gson.toJson(config),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (IOException e) {
            System.err.println("[ConfigService] save failed: " + e.getMessage());
        }
    }
}
