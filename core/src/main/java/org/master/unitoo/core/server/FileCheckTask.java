/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.server;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.master.unitoo.core.api.annotation.Component;
import org.master.unitoo.core.base.BaseBackgroundTask;
import org.master.unitoo.core.api.annotation.Attribute;
import org.master.unitoo.core.api.annotation.Logger;
import org.master.unitoo.core.api.util.IMonitor;
import org.master.unitoo.core.impl.FileMonitor;
import org.master.unitoo.core.impl.FileMonitorEvent;

/**
 *
 * @author Andrey
 */
@Component(value = "task-file-check")
@Logger(MainLog.class)
public class FileCheckTask extends BaseBackgroundTask {

    private final ConcurrentHashMap<String, FileMonitor> monitors = new ConcurrentHashMap();

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
        log().debug("Start scan file modifications...");
        ArrayList<String> rmv = new ArrayList<>();
        for (Map.Entry<String, FileMonitor> entry : monitors.entrySet()) {
            FileMonitor item = entry.getValue();
            String key = entry.getKey();

            log().debug("Checking for modifications: " + key);

            if (item.stopped()) {
                log().debug("Remove monitor for: " + key);
                rmv.add(key);

            } else if (!item.paused()) {
                item.check();
            } else {
                log().debug("Monitor paused for: " + key);
            }
        }

        log().debug("Cleanup file modifications monitors...");
        for (String key : rmv) {
            monitors.remove(key);
        }
    }

    public IMonitor<FileMonitorEvent> startCheck(File file) {
        if (file != null) {
            String fname = file.getPath();

            FileMonitor item;
            item = monitors.get(fname);
            if (item == null) {
                item = new FileMonitor(file, log());
                FileMonitor prev = monitors.putIfAbsent(fname, item);
                if (prev != null) {
                    item = prev;
                }
            }

            return item;
        } else {
            return null;
        }
    }

}
