/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.impl;

import java.io.File;
import org.master.unitoo.core.api.ILogger;
import org.master.unitoo.core.api.util.IMonitorListener;
import org.master.unitoo.core.types.CRUD;
import org.master.unitoo.core.types.DateTime;
import org.master.unitoo.core.utils.AbstractMonitor;

/**
 *
 * @author Andrey
 */
public class FileMonitor extends AbstractMonitor<FileMonitorEvent> {

    private final File file;
    private final ILogger log;
    private long timestamp;

    public FileMonitor(File file, ILogger log) {
        this.file = file;
        this.log = log;
        update();
    }

    @Override
    public final void update() {
        if (file.exists()) {
            timestamp = file.lastModified();
        } else {
            timestamp = 0;
        }
    }

    @Override
    public void check() {
        String fname = file.getPath();
        FileMonitorEvent evt = null;
        if (file.exists()) {
            long modified = file.lastModified();
            if (timestamp == 0) {
                evt = event(modified, CRUD.Create);
                log.info("File created: " + fname);
                timestamp = modified;
            } else if (timestamp != modified) {
                evt = event(modified, CRUD.Update);
                log.info("File modified: " + fname);
                timestamp = modified;
            }
        } else if (timestamp != 0) {
            evt = event(0, CRUD.Delete);
            log.info("File removed: " + fname);
            timestamp = 0;
        }

        if (evt != null) {
            for (IMonitorListener<FileMonitorEvent> listener : listeners()) {
                log.debug("Call monitor listener '" + listener.getClass().getName() + "' for: " + fname);
                listener.onEvent(evt);
            }
        }
    }

    public FileMonitorEvent event(long time, CRUD crud) {
        timestamp = time;
        return new FileMonitorEvent(file.getPath(), new DateTime(time), crud);
    }

}
