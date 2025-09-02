package org.oosd.service;

public final class ConfigService {
    private static volatile ConfigService instance;

    private ConfigService() {}  // private constructor

    public static ConfigService getInstance() {
        if (instance == null) {
            synchronized (ConfigService.class) {
                if (instance == null) {
                    instance = new ConfigService();
                }
            }
        }
        return instance;
    }

    // TODO: Add configuration fields like fieldWidth, musicOn, etc.
    // TODO: Add getters/setters for each field
}
