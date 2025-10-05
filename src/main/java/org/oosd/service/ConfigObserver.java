package org.oosd.service;

import org.oosd.model.Config;

public interface ConfigObserver {
    void onConfigChanged(Config oldCfg, Config newCfg);
}
