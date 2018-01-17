/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.impl;

import org.master.unitoo.core.base.BaseCounter;
import org.master.unitoo.core.types.CounterStrategy;
import org.master.unitoo.core.server.StoredSetting;

/**
 *
 * @author Andrey
 */
public abstract class SettingsBasedCounter extends BaseCounter {

    public SettingsBasedCounter(CounterStrategy strategy) {
        super(strategy);
    }

    protected abstract StoredSetting<Long> lastValue();

    protected abstract StoredSetting<Long> cacheSize();

    protected abstract StoredSetting<Long> resetTime();

    protected abstract StoredSetting<String> storeState();

    @Override
    protected CounterData load() throws Exception {
        CounterState state = app().parse(storeState().val(), CounterState.class);
        storeState().val(CounterState.Loaded.code());
        return new CounterData()
                .state(state)
                .lastValue(lastValue().val())
                .cacheSize(cacheSize().val())
                .resetTime(resetTime().val());
    }

    @Override
    protected void flush(CounterData data) throws Exception {
        lastValue().val(data.lastValue());
        resetTime().val(data.resetTime());
        storeState().val(data.state().code());
    }

    @Override
    public long cache() {
        return cacheSize().val();
    }

}
