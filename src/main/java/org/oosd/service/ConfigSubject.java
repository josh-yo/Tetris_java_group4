package org.oosd.service;

public interface ConfigSubject {
    void registerObserver(ConfigObserver o);
    void removeObserver(ConfigObserver o);
    void notifyObservers();
}
