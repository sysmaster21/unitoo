/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.impl;

import org.master.unitoo.core.types.CounterStrategy;
import org.master.unitoo.core.server.StoredSetting;
import org.master.unitoo.core.api.annotation.Attribute;

/**
 *
 * @author Andrey
 */
public abstract class SimpleCounter extends SettingsBasedCounter {

    @Attribute(name = "value", value = "0")
    public StoredSetting<Long> value;

    @Attribute(name = "cache", value = "100")
    public StoredSetting<Long> cache;

    @Attribute(name = "reset", value = "0")
    public StoredSetting<Long> reset;

    @Attribute(name = "state", value = "S")
    public StoredSetting<String> state;

    public SimpleCounter(CounterStrategy strategy) {
        super(strategy);
    }

    @Override
    protected StoredSetting<Long> lastValue() {
        return value;
    }

    @Override
    protected StoredSetting<Long> cacheSize() {
        return cache;
    }

    @Override
    protected StoredSetting<Long> resetTime() {
        return reset;
    }

    @Override
    protected StoredSetting<String> storeState() {
        return state;
    }

}
