/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.server;

import java.util.concurrent.ConcurrentHashMap;
import org.master.unitoo.core.api.annotation.Component;
import org.master.unitoo.core.base.BaseBackgroundTask;
import org.master.unitoo.core.api.annotation.Attribute;
import org.master.unitoo.core.api.annotation.Logger;
import org.master.unitoo.core.impl.DatabaseStorage;

/**
 *
 * @author Andrey
 */
@Component(value = "task-db-check")
@Logger(MainLog.class)
public class DatabaseCheckTask extends BaseBackgroundTask {

    private final ConcurrentHashMap<DatabaseStorage, DatabaseStorage> monitors = new ConcurrentHashMap();

    @Attribute(name = "delay", value = "1000")
    public Setting<Long> delay;

    @Attribute(name = "immediately", value = "true")
    public Setting<Boolean> startImmediately;

    @Override
    protected boolean startImmediately() {
        return startImmediately.val() == null ? true : startImmediately.val();
    }

    @Override
    protected long calcNext() {
        return delay.val() == null ? 1000 : delay.val();
    }

    @Override
    protected void execute() throws Throwable {
        log().debug("Start scan db modifications...");
        for (DatabaseStorage storage : monitors.values()) {
            log().debug("\tChecking: " + storage.getClass().getName());
            storage.checkChanges();
        }
    }

    public void startCheck(DatabaseStorage storage) {
        monitors.put(storage, storage);
    }

}
